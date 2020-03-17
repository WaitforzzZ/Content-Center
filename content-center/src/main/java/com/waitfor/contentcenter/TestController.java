package com.waitfor.contentcenter;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waitfor.contentcenter.dao.user.UserMapper;
import com.waitfor.contentcenter.domain.entity.user.User;

@RestController
public class TestController {

	@Autowired
	private UserMapper userMapper;
	@GetMapping("/test")
	public User testInsert(){
		User user = new User();
		user.setAvatarUrl("xxx");
		user.setBonus(100);
		user.setCreateTime(new Date());
		user.setUpdateTime(new Date());
		this.userMapper.insertSelective(user);
		return user;
	}
}
