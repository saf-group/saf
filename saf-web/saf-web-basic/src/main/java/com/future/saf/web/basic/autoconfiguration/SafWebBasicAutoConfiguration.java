package com.future.saf.web.basic.autoconfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.future.saf.core.preloader.PreloaderResult;
import com.future.saf.monitor.config.MonitorConfig;
import com.future.saf.web.basic.core.HttpSentinelInterceptor;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public class SafWebBasicAutoConfiguration {

	@Bean
	public HealthIndicator preloadIndicator() {
		return () -> {
			if (PreloaderResult.isComplete()) {
				return Health.up().build();
			} else {
				return Health.down().withDetail("preload", "NO").build();
			}
		};
	}

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public WebMvcConfigurer httpInterceptorConfig() {
		return new WebMvcConfigurer() {
			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				if (MonitorConfig.ENABLE_HTTP_PROFILE) {
					registry.addInterceptor(new HttpSentinelInterceptor()).addPathPatterns("/**");
				}
				if (MonitorConfig.ENABLE_HTTP_TRACEING_INTERCEPTOR) {
					// registry.addInterceptor(new
					// HttpTracingInterceptor()).addPathPatterns("/**");
				}
			}
		};
	}
}
