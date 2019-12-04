package com.future.saf.core.autoconfiguration;

import org.springframework.context.annotation.Bean;

import com.future.saf.core.preloader.PreloaderRegistry;

public class SafCoreAutoConfiguration {

	@Bean
	public PreloaderRegistry warmUpRegistry() {
		return new PreloaderRegistry();
	}

}
