package com.future.saf.http.apache.httpcomponents.filter;

import java.io.IOException;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.future.saf.monitor.basic.AbstractTimer;
import com.future.saf.monitor.prometheus.metric.profile.PrometheusMetricProfilerProcessor;
import com.future.saf.web.basic.util.HttpUtil;
import com.google.common.collect.Sets;

import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram.Timer;

public class HttpMetricFilter implements Filter {

	private static final PrometheusMetricProfilerProcessor PROFILER_STAT = new PrometheusMetricProfilerProcessor(
			"http_bio_client_ingoing_request", "http_bio_client_ingoing_request", "http_bio_client_ingoing_request",
			new String[] { "url", "method", "status" });

	private static final Gauge FTL_STAT = Gauge.build().name("http_bio_client_ingoing_request_ftl_gauge")
			.help("http_bio_client_ingoing_request_ftl_gauge").labelNames("url", "method", "status").register();

	private static final Set<String> FTL_URL_SET = Sets.newConcurrentHashSet();

	@Override
	public void init(FilterConfig filterConfig) {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest sRequest = (HttpServletRequest) request;
		HttpServletResponse sResponse = (HttpServletResponse) response;
		String url = HttpUtil.getPatternUrl(sRequest.getRequestURI());
		String method = sRequest.getMethod();
		String metrics = method + " " + url;
		long begin = System.currentTimeMillis();
		PROFILER_STAT.inc(url, method, "");
		AbstractTimer<Timer, Gauge, Gauge> timer = PROFILER_STAT.startTimer(url, method, "");
		boolean ftlFlag = false;
		if (!FTL_URL_SET.contains(metrics)) {
			FTL_URL_SET.add(metrics);
			ftlFlag = true;
		}
		try {
			chain.doFilter(request, response);
		} catch (IOException e) {
			PROFILER_STAT.error(url, method, IOException.class.getSimpleName());
			throw e;
		} catch (ServletException e) {
			PROFILER_STAT.error(url, method, ServletException.class.getSimpleName());
			throw e;
		} finally {
			PROFILER_STAT.dec(url, method, "");
			timer.observeDuration(url, method, "");
			if (ftlFlag) {
				FTL_STAT.labels(url, method, String.valueOf(sResponse.getStatus()))
						.set(System.currentTimeMillis() - begin);
			}
		}
	}

	@Override
	public void destroy() {

	}

}