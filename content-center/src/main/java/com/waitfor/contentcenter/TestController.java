package com.waitfor.contentcenter;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waitfor.contentcenter.dao.content.ShareMapper;
import com.waitfor.contentcenter.domain.entity.content.Share;


@RestController
public class TestController {

	@Autowired
	private ShareMapper shareMapper;
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
}
