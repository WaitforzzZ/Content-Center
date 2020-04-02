package com.waitfor.contentcenter;

import java.util.Date;
import java.util.List;

import com.waitfor.contentcenter.domain.dto.user.UserDTO;
import com.waitfor.contentcenter.feignclient.TestUserCenterFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
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
}
