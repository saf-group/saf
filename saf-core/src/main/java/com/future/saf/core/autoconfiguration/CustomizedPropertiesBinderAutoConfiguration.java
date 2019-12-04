package com.future.saf.core.autoconfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import com.future.saf.core.CustomizedConfigurationPropertiesBinder;

@Order(1)
public class CustomizedPropertiesBinderAutoConfiguration {
	@Bean
	public CustomizedConfigurationPropertiesBinder customizedConfigurationPropertiesBinder() {
		return new CustomizedConfigurationPropertiesBinder();
	}
}
