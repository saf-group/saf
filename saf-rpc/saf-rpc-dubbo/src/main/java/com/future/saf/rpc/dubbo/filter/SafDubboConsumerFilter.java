package com.future.saf.rpc.dubbo.filter;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

import com.future.saf.monitor.config.MonitorConfig;
import com.future.saf.monitor.prometheus.metric.profile.PrometheusMetricProfilerProcessor;
import com.future.saf.rpc.dubbo.util.SafDubboUtil;
import com.google.common.collect.Sets;

import io.prometheus.client.Gauge;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Activate
public class SafDubboConsumerFilter implements Filter {

	private static final PrometheusMetricProfilerProcessor PROFILE_DUBBO_OUT = new PrometheusMetricProfilerProcessor(
			"dubbo_requests_out", "dubbo:out", "dubbo_requests_out", new String[] { "class", "method" });

	private static final Gauge PROFILE_DUBBO_FTL_OUT = Gauge.build().name("dubbo_requests_out_ftl")
			.help("dubbo_requests_out_ftl").labelNames("class", "method").register();

	private static final Set<String> FTL_OUT_MARKED_SET = Sets.newConcurrentHashSet();

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

		// 可以开关控制是否开启monitor
		if (!MonitorConfig.ENABLE_MONITOR_DUBBO) {
			return invoker.invoke(invocation);
		}

		// motanRPC class
		String clazz = SafDubboUtil.getShortName(invoker.getInterface().getName());

		// motanRPC method
		String method = clazz + "." + invocation.getMethodName();

		// invalid and return at once.
		if (StringUtils.isEmpty(method) || StringUtils.isEmpty(clazz)) {
			return invoker.invoke(invocation);
		}

		long begin = System.nanoTime();
		boolean specialException = true;
		boolean isError = false;
		// 判断是否是第一次访问
		final boolean firstAccessFlag = beforeCall(clazz, method);

		Result result = null;

		try {
			result = invoker.invoke(invocation);
			if (result == null) {
				isError = true;
			} else {
				if (result.getException() != null) {
					isError = true;
				}
			}
			specialException = false;
			return result;
		} catch (Exception e) {
			// 这里有可能跑出RuntimeException,这里不能吞异常
			// TODO 后续要对Exception分类
			log.error(e.getMessage(), e);
			isError = true;
			return null;
		} finally {
			if (specialException) {
				isError = true;
			}
			afterCall(clazz, method, begin, isError, firstAccessFlag);
		}
	}

	private void afterCall(String clazz, String method, long begin, boolean isError, boolean firstAccessFlag) {
		PROFILE_DUBBO_OUT.dec(clazz, method);
		PROFILE_DUBBO_OUT.observe(System.nanoTime() - begin, TimeUnit.NANOSECONDS, clazz, method);
		if (isError) {
			PROFILE_DUBBO_OUT.error(clazz, method);
		}
		if (firstAccessFlag) {
			PROFILE_DUBBO_FTL_OUT.labels(clazz, method).set(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - begin));
		}
	}

	private boolean beforeCall(String clazz, String method) {
		boolean firstAccessFlag = false;
		String key = clazz + "::" + method;
		if (!FTL_OUT_MARKED_SET.contains(key)) {
			FTL_OUT_MARKED_SET.add(key);
			firstAccessFlag = true;
		}
		PROFILE_DUBBO_OUT.inc(clazz, method);
		return firstAccessFlag;
	}

}
