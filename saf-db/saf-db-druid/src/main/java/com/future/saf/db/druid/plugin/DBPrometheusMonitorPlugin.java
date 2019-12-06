package com.future.saf.db.druid.plugin;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.future.saf.monitor.basic.AbstractTimer;
import com.future.saf.monitor.config.MonitorConfig;
import com.future.saf.monitor.prometheus.metric.profile.PrometheusMetricProfilerProcessor;

import java.util.Properties;

@Intercepts(value = {
		@Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
				RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class }),
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
				RowBounds.class, ResultHandler.class }) })
public class DBPrometheusMonitorPlugin implements Interceptor {

	private final PrometheusMetricProfilerProcessor metricProfileProcessor = new PrometheusMetricProfilerProcessor(
			"mapper", "mapper", "mapper", new String[] { "resource", "method" },
			new double[] { 0.0001, 0.0005, 0.0009, 0.001, 0.003, 0.005, 0.007, 0.009, 0.01, 0.03, 0.05, 0.07, 0.09, 0.1,
					0.3, 0.5, 0.7, 0.9, 1, 3, 5 });

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		if (!MonitorConfig.ENABLE_MONITOR_MAPPER) {
			return invocation.proceed();
		}
		final Object[] args = invocation.getArgs();
		if (args != null && args.length > 0) {
			final MappedStatement mappedStatement = (MappedStatement) args[0];
			if (mappedStatement != null) {
				final String methodName = mappedStatement.getId();
				final String resource = mappedStatement.getResource();
				AbstractTimer<?, ?, ?> timer = metricProfileProcessor.startTimer(resource, methodName);
				try {
					metricProfileProcessor.inc(resource, methodName);
					return invocation.proceed();
				} catch (Throwable throwable) {
					metricProfileProcessor.error(resource, methodName);
					throw throwable;
				} finally {
					metricProfileProcessor.dec(resource, methodName);
					timer.observeDuration(resource, methodName);
				}
			}
		}
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		return;
	}
}