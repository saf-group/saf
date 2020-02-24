package com.future.saf.rpc.dubbo.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.dubbo.rpc.RpcException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.future.saf.core.web.WebResult;
import com.future.saf.logging.basic.Loggers;
import com.future.saf.web.basic.util.HttpUtil;

@ControllerAdvice
public class HttpRestController {

	@ExceptionHandler({ RpcException.class })
	@ResponseBody
	public WebResult<String> handle(HttpServletRequest request, HttpServletResponse response, RpcException ex) {
		// public WebResult handle(HttpServletRequest request,
		// HttpServletResponse response) {
		String metrics = request.getMethod() + " " + HttpUtil.getPatternUrl(request.getRequestURI());
		Loggers.getAccessLogger().warn("dubbo rpc exception, you should process dubbo rpc exception! -> {}", metrics);
		return WebResult.exception(ex.getMessage());
	}

}