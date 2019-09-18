package com.future.saf.web.basic.autoconfiguration;

import org.springframework.context.annotation.Bean;

import com.future.saf.core.preloader.PreloaderResult;

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
}
