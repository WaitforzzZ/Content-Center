package com.waitfor.contentcenter.service.content;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.nacos.client.naming.utils.ThreadLocalRandom;
import com.waitfor.contentcenter.dao.content.ShareMapper;
import com.waitfor.contentcenter.domain.dto.content.ShareDTO;
import com.waitfor.contentcenter.domain.dto.user.UserDTO;
import com.waitfor.contentcenter.domain.entity.content.Share;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareService {

    private final ShareMapper shareMapper;
    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    public ShareDTO findById(Integer id) {
        // 获取分享详情
        Share share = this.shareMapper.selectByPrimaryKey(id);
        // 发布人id
        Integer userId = share.getUserId();
        //用户中心所有实例的信息
		/*//未接入ribbon
		List<ServiceInstance> instances = discoveryClient.getInstances("user-center");
		*//*String targetUrl = instances.stream()
		    //数据变换
			.map(instance ->instance.getUri().toString()+ "/users/{id}")
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("当前没有实例"));*//*
		List<String> targetUrls = instances.stream()
	    //数据变换
		.map(instance ->instance.getUri().toString()+ "/users/{id}")
		.collect(Collectors.toList());
		int i = ThreadLocalRandom.current().nextInt(targetUrls.size());
		String targetUrl = targetUrls.get(i);
		log.info("请求的目标地址： {}",targetUrl);*/

        //接入ribbon
        // 通过RestTemplate调用用户微服务的/user/{userId}??
        UserDTO userDTO = this.restTemplate.getForObject(
                "http://user-center/users/{userId}",
                UserDTO.class, userId);
        //消息的装配
        ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickname(userDTO.getWxNickname());
        return shareDTO;
    }

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        // 用HTTP GET方法去请求，并且返回一个对象
		/*String forObject=restTemplate.getForObject(
				"http://localhost:8080/users/{id}",
				String.class,1);
		System.out.println(forObject);*/
        ResponseEntity<String> forEntity = restTemplate.getForEntity(
                "http://localhost:8080/users/{id}",
                String.class, 1);
        System.out.println(forEntity.getBody());
        //相比getForObject可以拿到http的状态码
        System.out.println(forEntity.getStatusCode());
    }
}
