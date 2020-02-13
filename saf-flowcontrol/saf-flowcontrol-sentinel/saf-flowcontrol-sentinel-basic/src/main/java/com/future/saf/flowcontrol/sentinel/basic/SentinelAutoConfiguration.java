package com.future.saf.flowcontrol.sentinel.basic;

import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.future.saf.logging.basic.Loggers;

public class SentinelAutoConfiguration implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	/**
	 * 定时统计与度量
	 * 
	 * @return
	 */
	@Bean
	public SentinelMetricTimer sentinelMetricTimer() {
		SentinelMetricTimer timer = new SentinelMetricTimer();
		timer.init();
		return timer;
	}

	@Bean
	public SentinelResourceAspect sentinelResourceAspect() {
		return new SentinelResourceAspect();
	}

	@Bean(name = "safSentinelHolder")
	public AbstractSentinelHolder safSentinelHolder() {
		Set<String> sentinelHolderBeanNameSet = SentinelRegistrar.instanceMap.keySet();

		AbstractSentinelHolder holder = null;
		for (String sentinelHolderBeanName : sentinelHolderBeanNameSet) {
			holder = applicationContext.getBean(sentinelHolderBeanName, AbstractSentinelHolder.class);
			Loggers.getFrameworkLogger()
					.info("load AbstractSentinelHolder( " + sentinelHolderBeanName + " ): " + holder);
		}
		return new AbstractSentinelHolder();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
