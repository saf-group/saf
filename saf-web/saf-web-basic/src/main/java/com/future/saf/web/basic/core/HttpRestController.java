package com.future.saf.web.basic.core;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.future.saf.core.web.WebResult;
import com.future.saf.logging.basic.Loggers;
import com.future.saf.web.basic.util.HttpUtil;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class HttpRestController {

	@ExceptionHandler({ BlockException.class })
	@ResponseBody
	public WebResult<String> handle(HttpServletRequest request, HttpServletResponse response, BlockException ex) {
		// public WebResult handle(HttpServletRequest request,
		// HttpServletResponse response) {
		String metrics = request.getMethod() + " " + HttpUtil.getPatternUrl(request.getRequestURI());
		Loggers.getAccessLogger().warn("http request blocked! -> {}", metrics);
		return WebResult.block();
	}

}