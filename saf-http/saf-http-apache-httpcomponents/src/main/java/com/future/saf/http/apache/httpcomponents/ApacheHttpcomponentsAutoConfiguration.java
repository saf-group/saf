package com.future.saf.http.apache.httpcomponents;

import javax.servlet.Filter;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.future.saf.core.autoconfiguration.CustomizedPropertiesBinderAutoConfiguration;
import com.future.saf.flowcontrol.sentinel.basic.SentinelMetricTimer;
import com.future.saf.http.apache.httpcomponents.filter.ApacheHttpcomponentsMetricFilter;
import com.future.saf.http.apache.httpcomponents.flowcontrol.sentinel.ApacheHttpcomponentsSentinelHttpRestController;
//import com.future.saf.http.apache.httpcomponents.asyncimpl.CHttpClient;
import com.future.saf.http.apache.httpcomponents.syncimpl.ApacheHttpcomponentsBioClient;

@Configuration
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
@ConditionalOnWebApplication
@ConditionalOnClass({ ApacheHttpcomponentsBioClient.class, SphU.class })
@AutoConfigureAfter(CustomizedPropertiesBinderAutoConfiguration.class)
public class ApacheHttpcomponentsAutoConfiguration {

	// @Bean
	// public HealthIndicator chttpclientHealthIndicator() {
	// return () -> {
	// if (CHttpClient.readyForRequest()) {
	// return Health.up().build();
	// } else {
	// return Health.down().withDetail("CHttpClient", "Down").build();
	// }
	// };
	// }

	@Bean
	public HealthIndicator chttpbioclientHealthIndicator() {
		return () -> {
			if (ApacheHttpcomponentsBioClient.readyForRequest()) {
				return Health.up().build();
			} else {
				return Health.down().withDetail("CHttpBioClient", "Down").build();
			}
		};
	}

	@Bean
	public FilterRegistrationBean<Filter> profilerFilterRegistration() {
		FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new ApacheHttpcomponentsMetricFilter());
		registration.addUrlPatterns("/*");
		registration.setName("httpMetricFilter");
		registration.setOrder(1);

		return registration;
	}

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

	@Bean
	public ApacheHttpcomponentsSentinelHttpRestController sentinelHttpRestController() {
		return new ApacheHttpcomponentsSentinelHttpRestController();
	}
}
