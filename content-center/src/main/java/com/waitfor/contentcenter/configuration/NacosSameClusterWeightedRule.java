package com.waitfor.contentcenter.configuration;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.core.Balancer;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties;
import org.springframework.cloud.alibaba.nacos.ribbon.NacosServer;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Nacos权重，ribbon自带的负载均衡算法不支持，自定义负载均衡算法
 */
@Slf4j
public class NacosSameClusterWeightedRule extends AbstractLoadBalancerRule {
    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
        //读取配置文件，并初始化NacosWeightedRule
    }

    @Override
    public Server choose(Object o) {
        try {
            // 拿到配置文件的集群名称 BJ
            String clusterName = nacosDiscoveryProperties.getClusterName();
            //ribbon的入口
            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();
            log.info("lb = {}", loadBalancer);
            // 想要请求的微服务的名称
            String name = loadBalancer.getName();
            // 拿到服务发现的相关API
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();
            // 1. 找到指定服务的所有实例 A
            List<Instance> instances = namingService.selectInstances(name, true);
            // 2. 过滤出相同集群下的所有实例 B
            List<Instance> sameClusterInstances = instances.stream()
                    .filter(instance -> Objects.equals(instance.getClusterName(), clusterName))
                    .collect(Collectors.toList());
            // 3. 如果B是空， 就用A
            List<Instance> instancesToBeChosen = new ArrayList<>();
            //同一个集群下没有实例
            if (CollectionUtils.isEmpty(sameClusterInstances)) {
                instancesToBeChosen = instances;
                log.warn("发生跨集群的调用, name = {}, clusterName = {}, instances = {}",
                        name,
                        clusterName,
                        instances
                );
            } else {
                instancesToBeChosen = sameClusterInstances;
            }
            // 4. 基于权重的负载均衡算法， 返回一个实例
            Instance instance = ExtendsBalancer.getHostByRandomWeight2(sameClusterInstances);
            log.info("选择的实例是 port = {}, instance = {}",instance.getPort(),instance);
            return new NacosServer(instance);
        } catch (NacosException e) {
            log.error("发生异常了", e);
            return null;
        }

    }
}

class ExtendsBalancer extends Balancer {
    public static Instance getHostByRandomWeight2(List<Instance> hosts){
        return getHostByRandomWeight(hosts);
    }
}
