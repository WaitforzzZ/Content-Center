package com.waitfor.contentcenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.waitfor.contentcenter.domain.dto.user.UserDTO;
import com.waitfor.contentcenter.feignclient.TestBaiduFeignClient;
import com.waitfor.contentcenter.feignclient.TestUserCenterFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waitfor.contentcenter.dao.content.ShareMapper;
import com.waitfor.contentcenter.domain.entity.content.Share;


@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestController {

	private final ShareMapper shareMapper;
	private final DiscoveryClient discoveryClient;
	private final TestUserCenterFeignClient testUserCenterFeignClient;
	@GetMapping("/test")
	public List<Share> testInsert(){
		Share build = Share.builder()
			.auditStatus("s")
			.author("zl")
			.createTime(new Date())
			.updateTime(new Date())
			.build();
		this.shareMapper.insertSelective(build);
		List<Share> selectAll = this.shareMapper.selectAll();
		return selectAll;
	}
	
	/**
	 * 测试：服务发现， 证明内容中心总能找到用户中心
	 * @return 用户中心所有实例的地址信息
	 */
	@GetMapping("/test2")
	public List<ServiceInstance> getInstances(){
		// 查询指定服务的所有实例信息
		// consul/eureka/zookeeper...
		return this.discoveryClient.getInstances("user-center");
	}

	@GetMapping("/test-get")
	public UserDTO query(UserDTO userDTO){
		return this.testUserCenterFeignClient.query(userDTO);
	}

	@Autowired
	private TestBaiduFeignClient testBaiduFeignClient;
	@GetMapping("baidu")
	public String index(){
		return this.testBaiduFeignClient.index();
	}

	@Autowired
	private TestService testService;
	@GetMapping("test-a")
	public String testA(){
		this.testService.common();
		return "test-a";
	}

	@GetMapping("test-b")
	public String testB(){
		this.testService.common();
		return "test-b";
	}

	@GetMapping("test-host")
	@SentinelResource("hot")
	public String testHost(
			@RequestParam(required = false) String a,
			@RequestParam(required = false) String b
	){
		return a+" "+b;
	}

	@GetMapping("test-add-flow-rule")
	public String testHost(){
		this.initFlowQpsRule();
		return "success";
	}

	private void initFlowQpsRule() {
		List<FlowRule> rules = new ArrayList<>();
		FlowRule rule = new FlowRule("/shares/1");
		// set limit qps to 20
		rule.setCount(20);
		rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
		rule.setLimitApp("default");
		rules.add(rule);
		FlowRuleManager.loadRules(rules);
	}
}
