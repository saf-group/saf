package com.future.saf.monitor.basic;

import javax.annotation.Resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import com.future.saf.monitor.config.MonitorConfig;

@Aspect
public class MetricProfilerAspect {

	@Resource(name = "customMetricProfileProcessor")
	private AbstractMetricProfilerProcessor<?,?,?> customMetricProfileProcessor;

	@Autowired
	private MonitorConfig monitorConfig;

	@Pointcut("@annotation(com.future.saf.monitor.MetricProfile)")
	public void pointcut() {
	}

	@Around("pointcut()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		if (!monitorConfig.isEnableMonitorCustom()) {
			return joinPoint.proceed();
		}

		final String clazz = joinPoint.getSignature().getDeclaringType().getSimpleName();
		final String method = joinPoint.getSignature().toShortString();

		AbstractTimer<?,?,?> timer = customMetricProfileProcessor.startTimer(clazz, method);
		try {
			customMetricProfileProcessor.inc(clazz, method);
			return joinPoint.proceed();
		} catch (Throwable throwable) {
			customMetricProfileProcessor.error(clazz, method);
			throw throwable;
		} finally {
			customMetricProfileProcessor.dec(clazz, method);
			timer.observeDuration(clazz, method);
		}
	}
}
