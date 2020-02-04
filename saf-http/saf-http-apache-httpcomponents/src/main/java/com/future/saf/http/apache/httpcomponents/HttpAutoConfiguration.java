package com.future.saf.http.apache.httpcomponents;

import javax.servlet.Filter;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.future.saf.http.apache.httpcomponents.filter.HttpMetricFilter;
//import com.future.saf.http.apache.httpcomponents.asyncimpl.CHttpClient;
import com.future.saf.http.apache.httpcomponents.syncimpl.HttpBioClient;

@Configuration
@ConditionalOnClass(HttpBioClient.class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
@ConditionalOnWebApplication
public class HttpAutoConfiguration {

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
			if (HttpBioClient.readyForRequest()) {
				return Health.up().build();
			} else {
				return Health.down().withDetail("CHttpBioClient", "Down").build();
			}
		};
	}

	@Bean
	public FilterRegistrationBean<Filter> profilerFilterRegistration() {
		FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new HttpMetricFilter());
		registration.addUrlPatterns("/*");
		registration.setName("httpMetricFilter");
		registration.setOrder(1);

		return registration;
	}
}
