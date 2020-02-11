package com.future.saf.http.apache.httpcomponents.flowcontrol.sentinel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.future.saf.web.basic.util.HttpUtil;

/**
 * 功能跟过滤器类似，但是提供更精细的的控制能力：
 * 
 * 在request被响应之前、request被响应之后、视图渲染之前以及request全部结束之后。
 * 我们不能通过拦截器修改request内容，但是可以通过抛出异常（或者返回false）来暂停request的执行。
 * 
 * 对服务接受到的http请求进行flowcontrol。
 * 
 */
public class ApacheHttpcomponentsSentinelHttpInterceptor implements HandlerInterceptor {
	private static final String PREFIX = "http:in:";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws BlockException {
		Entry methodEntry, httpEntry;
		String url = HttpUtil.getPatternUrl(request.getRequestURI());
		String metrics = request.getMethod() + " " + url;
		try {
			httpEntry = SphU.entry("Http::In");
			methodEntry = SphU.entry(PREFIX + metrics);
			request.setAttribute("metrics", metrics);
			request.setAttribute("methodEntry", methodEntry);
			request.setAttribute("httpEntry", httpEntry);
		} catch (BlockException ex) {
			throw ex;
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		if (ex != null) {
			Tracer.trace(ex);
		}
		Entry methodEntry = (Entry) request.getAttribute("methodEntry");
		Entry httpEntry = (Entry) request.getAttribute("httpEntry");
		if (methodEntry != null) {
			methodEntry.exit();
		}
		if (httpEntry != null) {
			httpEntry.exit();
		}
	}

}
