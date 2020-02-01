package com.future.saf.rpc.dubbo.core;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.MetricsConfig;
import org.apache.dubbo.config.ModuleConfig;
import org.apache.dubbo.config.MonitorConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.RegistryConfig;
//import org.apache.dubbo.config.ApplicationConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
//import org.slf4j.Logger;
//import com.future.saf.logging.basic.Loggers;
//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.future.saf.core.autoconfiguration.CustomizedPropertiesBinderAutoConfiguration;
import com.future.saf.rpc.dubbo.util.SafDubboUtil;

@Configuration
@AutoConfigureAfter(CustomizedPropertiesBinderAutoConfiguration.class)
public class SafDubboAutoConfiguration {

	@Bean(name = "defaultApplicationConfig")
	@ConditionalOnMissingBean
	@ConfigurationProperties(prefix = "dubbo.default.application")
	public ApplicationConfig applicationConfigBean() {
		ApplicationConfig config = new ApplicationConfig();
		SafDubboUtil.initApplicationConfig(config);
		return config;
	}

	@Bean(name = "defaultModuleConfig")
	@ConditionalOnMissingBean
	@ConfigurationProperties(prefix = "dubbo.default.module")
	public ModuleConfig moduleConfigBean() {
		ModuleConfig config = new ModuleConfig();
		SafDubboUtil.initModuleConfig(config);
		return config;
	}

	@Bean(name = "defaultRegistryConfig")
	@ConditionalOnMissingBean
	@ConfigurationProperties(prefix = "dubbo.default.registry")
	public RegistryConfig registryConfigBean() {
		RegistryConfig config = new RegistryConfig();
		SafDubboUtil.initRegistryConfig(config);
		return config;
	}

	@Bean(name = "defaultMonitorConfig")
	@ConditionalOnMissingBean
	@ConfigurationProperties(prefix = "dubbo.default.monitor")
	public MonitorConfig monitorConfigBean() {
		MonitorConfig config = new MonitorConfig();
		SafDubboUtil.initMonitorConfig(config);
		return config;
	}

	@Bean(name = "defaultMetricsConfig")
	@ConditionalOnMissingBean
	@ConfigurationProperties(prefix = "dubbo.default.monitor")
	public MetricsConfig metricsConfigBean() {
		MetricsConfig config = new MetricsConfig();
		return config;
	}

	@Bean(name = "defaultProviderConfig")
	@ConditionalOnMissingBean
	@ConfigurationProperties(prefix = "dubbo.default.provider")
	public ProviderConfig providerConfigBean() {
		ProviderConfig config = new ProviderConfig();
		SafDubboUtil.initProviderConfig(config);
		return config;
	}

	@Bean(name = "defaultConsumerConfig")
	@ConditionalOnMissingBean
	@ConfigurationProperties(prefix = "dubbo.default.consumer")
	public ConsumerConfig consumerConfigBean() {
		ConsumerConfig config = new ConsumerConfig();
		SafDubboUtil.initConsumerConfig(config);
		return config;
	}

	@Bean(name = "defaultProtocolConfig")
	@ConditionalOnMissingBean
	@ConfigurationProperties(prefix = "dubbo.default.protocol")
	public ProtocolConfig ProtocolConfigBean() {
		ProtocolConfig config = new ProtocolConfig();
		SafDubboUtil.initProtocolConfig(config);
		return config;
	}
}
