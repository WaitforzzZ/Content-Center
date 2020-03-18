package com.waitfor.contentcenter.service.content;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.waitfor.contentcenter.dao.content.ShareMapper;
import com.waitfor.contentcenter.domain.dto.content.ShareDTO;
import com.waitfor.contentcenter.domain.dto.user.UserDTO;
import com.waitfor.contentcenter.domain.entity.content.Share;

@Service
public class ShareService {
	
	@Autowired
	private ShareMapper shareMapper;
	@Autowired
	private RestTemplate restTemplate;
	public ShareDTO findById(Integer id){
		// 获取分享详情
		Share share = this.shareMapper.selectByPrimaryKey(id);
		// 发布人id
		Integer userId = share.getUserId();
		// 通过RestTemplate调用用户微服务的/user/{userId}??
		UserDTO userDTO = this.restTemplate.getForObject(
				"http://localhost:8080/users/{id}",
				UserDTO.class,userId);
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
				String.class,1);
		System.out.println(forEntity.getBody());
		//相比getForObject可以拿到http的状态码
		System.out.println(forEntity.getStatusCode());
	}
}
