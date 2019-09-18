package com.future.saf.allinone.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.future.saf.core.web.WebResult;
import com.future.saf.sample.allinone.remote.api.UserRPC;
import com.future.saf.sample.allinone.remote.dto.UserDTO;
import com.future.saf.sample.allinone.remote.dto.UserDetailDTO;
import com.weibo.api.motan.config.springsupport.annotation.MotanReferer;

@RestController
@RequestMapping(value = "/user")
public class UserController {

	@MotanReferer(basicReferer = "userBasicRefererConfigBean")
	private UserRPC userrpc;

	@RequestMapping("/getUser")
	public WebResult<UserDTO> getUser(@RequestParam(name = "userId") Long userId) {
		UserDTO userDTO = userrpc.getUser(userId);
		return new WebResult<>(WebResult.CODE_SUCCESS, "getUser success.", userDTO);
	}

	@RequestMapping("/getUserDetail")
	public WebResult<UserDetailDTO> getUserDetail(@RequestParam(name = "userId") Long userId) {
		UserDetailDTO userDTO = userrpc.getUserDetail(userId);
		return new WebResult<>(WebResult.CODE_SUCCESS, "getUserDetail success.", userDTO);
	}
}
