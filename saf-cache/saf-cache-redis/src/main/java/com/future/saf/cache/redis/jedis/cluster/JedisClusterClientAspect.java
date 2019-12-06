package com.future.saf.cache.redis.jedis.cluster;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import com.future.saf.monitor.basic.AbstractTimer;
import com.future.saf.monitor.config.MonitorConfig;
import com.future.saf.monitor.prometheus.metric.profile.PrometheusMetricProfilerProcessor;

@Aspect
public class JedisClusterClientAspect {

	private static final PrometheusMetricProfilerProcessor metricProfileProcessor = new PrometheusMetricProfilerProcessor(
			"jedisCluster", "jedisCluster", "jedisCluster", new String[] { "beanName", "method" },
			new double[] { 0.0001, 0.0005, 0.0009, 0.001, 0.003, 0.005, 0.007, 0.009, 0.01, 0.03, 0.05, 0.07, 0.09, 0.1,
					0.3, 0.5, 0.7, 0.9, 1, 3, 5 });

	@Autowired
	private MonitorConfig monitorConfig;

	@Pointcut("execution(* com.future.saf.cache.redis.jedis.cluster.JedisClusterClient.*(..)) &&"
			+ "!execution(* com.future.saf.cache.redis.jedis.cluster.JedisClusterClient.getAddress(..)) &&"
			+ "!execution(* com.future.saf.cache.redis.jedis.cluster.JedisClusterClient.getBeanName(..))")
	public void pointcut() {
	}

	@Around("pointcut()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		if (!monitorConfig.isEnableMonitorRedis()) {
			return joinPoint.proceed();
		}
		String beanName = "";
		if (joinPoint.getTarget() instanceof JedisClusterClient) {
			beanName = ((JedisClusterClient) joinPoint.getTarget()).getBeanName();
		}
		final String methodName = joinPoint.getSignature().getName();

		AbstractTimer<?, ?, ?> timer = metricProfileProcessor.startTimer(beanName, methodName);
		try {
			metricProfileProcessor.inc(beanName, methodName);
			return joinPoint.proceed();
		} catch (Throwable throwable) {
			metricProfileProcessor.error(beanName, methodName);
			throw throwable;
		} finally {
			metricProfileProcessor.dec(beanName, methodName);
			timer.observeDuration(beanName, methodName);
		}
	}

}
