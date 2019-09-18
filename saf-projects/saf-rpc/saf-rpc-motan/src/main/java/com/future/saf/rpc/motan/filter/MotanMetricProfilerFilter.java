package com.future.saf.rpc.motan.filter;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import com.future.saf.monitor.config.MonitorConfig;
import com.future.saf.monitor.prometheus.metric.profile.LatencyMetricProfilerProcessor;
import com.future.saf.rpc.motan.util.MotanUtil;
import com.google.common.collect.Sets;
import com.weibo.api.motan.core.extension.Activation;
import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.filter.Filter;
import com.weibo.api.motan.rpc.Caller;
import com.weibo.api.motan.rpc.Provider;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;

import io.prometheus.client.Gauge;
import lombok.extern.slf4j.Slf4j;

@SpiMeta(name = "safProfiler")
@Activation(sequence = 2)
@Slf4j
public class MotanMetricProfilerFilter implements Filter {

	private static final LatencyMetricProfilerProcessor PROFILE_MOTAN_IN = new LatencyMetricProfilerProcessor(
			"motan_requests_in", "motan:in", "motan_requests_in", new String[] { "class", "method" });

	private static final LatencyMetricProfilerProcessor PROFILE_MOTAN_OUT = new LatencyMetricProfilerProcessor(
			"motan_requests_out", "motan:out", "motan_requests_out", new String[] { "class", "method" });

	private static final Gauge PROFILE_MOTAN_FTL_IN = Gauge.build().name("motan_requests_in_ftl")
			.help("motan_requests_in_ftl").labelNames("class", "method").register();

	private static final Gauge PROFILE_MOTAN_FTL_OUT = Gauge.build().name("motan_requests_out_ftl")
			.help("motan_requests_out_ftl").labelNames("class", "method").register();

	private static final Set<String> FTL_IN_MARKED_SET = Sets.newConcurrentHashSet();
	private static final Set<String> FTL_OUT_MARKED_SET = Sets.newConcurrentHashSet();

	@Override
	public Response filter(Caller<?> caller, Request request) {
		// 可以开关控制是否开启monitor
		if (!MonitorConfig.ENABLE_MONITOR_MOTAN) {
			return caller.call(request);
		}

		// motanRPC class
		String clazz = MotanUtil.getShortName(request.getInterfaceName());
		// motanRPC method
		String method = clazz + "." + request.getMethodName() + "(" + MotanUtil.getShortName(request.getParamtersDesc())
				+ ")";
		// invalid and return at once.
		if (StringUtils.isEmpty(method) || StringUtils.isEmpty(clazz)) {
			return caller.call(request);
		}

		long begin = System.nanoTime();
		boolean specialException = true;
		boolean isError = false;
		// 判断是否是第一次访问
		final boolean firstAccessFlag = beforeCall(method, clazz, caller);
		try {
			Response response = caller.call(request);
			if (response == null) {
				isError = true;
			} else {
				if (response.getException() != null) {
					isError = true;
				}
			}
			specialException = false;
			return response;
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
			afterCall(clazz, method, caller, begin, isError, firstAccessFlag);
		}
	}

	private void afterCall(String clazz, String method, Caller<?> caller, long begin, boolean isError,
			boolean firstAccessFlag) {
		if (caller instanceof Provider) {
			PROFILE_MOTAN_IN.end(clazz, method);
			if (isError) {
				PROFILE_MOTAN_IN.error(clazz, method);
			}
			if (firstAccessFlag) {
				PROFILE_MOTAN_FTL_IN.labels(clazz, method)
						.set(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - begin));
			}
		} else {
			PROFILE_MOTAN_OUT.end(clazz, method);
			if (isError) {
				PROFILE_MOTAN_OUT.error(clazz, method);
			}
			if (firstAccessFlag) {
				PROFILE_MOTAN_FTL_OUT.labels(clazz, method)
						.set(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - begin));
			}
		}
	}

	private boolean beforeCall(String clazz, String method, Caller<?> caller) {
		boolean firstAccessFlag = false;
		String key = clazz + "::" + method;
		if (caller instanceof Provider) {
			if (!FTL_IN_MARKED_SET.contains(key)) {
				FTL_IN_MARKED_SET.add(key);
				firstAccessFlag = true;
			}
			PROFILE_MOTAN_IN.begin(clazz, method);
		} else {
			if (!FTL_OUT_MARKED_SET.contains(key)) {
				FTL_OUT_MARKED_SET.add(key);
				firstAccessFlag = true;
			}
			PROFILE_MOTAN_OUT.begin(clazz, method);
		}
		return firstAccessFlag;
	}
}
