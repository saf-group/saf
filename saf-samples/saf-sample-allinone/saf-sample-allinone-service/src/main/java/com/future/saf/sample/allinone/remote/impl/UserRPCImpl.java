package com.future.saf.sample.allinone.remote.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.future.saf.sample.allinone.constant.KVKeyConstant;
import com.future.saf.sample.allinone.remote.api.UserRPC;
import com.future.saf.sample.allinone.remote.dto.UserDTO;
import com.future.saf.sample.allinone.remote.dto.UserDetailDTO;
import com.future.saf.sample.allinone.service.UserService;
import com.weibo.api.motan.config.springsupport.annotation.MotanService;

@MotanService(basicService = "userBasicServiceConfigBean")
public class UserRPCImpl implements UserRPC {

	@Autowired
	private UserService userService;

	@Override
	public UserDetailDTO getUserDetail(Long userId) {
		// TODO 这里还有可能调用其他的RPC完成一些逻辑，同时也有可能添加user关联信息，然后组装DTO。
		return userService.getUserDetail(userId);
	}

	@Override
	public UserDTO getUser(Long userId) {
		// TODO 这里还有可能调用其他的RPC完成一些逻辑
		return userService.getUser(userId);
	}

	public static void main(String[] args) {
		System.out.println(String.format(KVKeyConstant.KEY_USER, 333));
	}
}
