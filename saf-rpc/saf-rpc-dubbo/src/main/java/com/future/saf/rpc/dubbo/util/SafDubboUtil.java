package com.future.saf.rpc.dubbo.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.MetricsConfig;
import org.apache.dubbo.config.ModuleConfig;
import org.apache.dubbo.config.MonitorConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.RegistryConfig;

public class SafDubboUtil {

	public static void initApplicationConfig(ApplicationConfig config) {
		config.setVersion("1.0.0");
		config.setOwner("saf");
		config.setOrganization("saf-group");
	}

	public static void initModuleConfig(ModuleConfig config) {
		config.setVersion("1.0.0");
		config.setOwner("saf");
		config.setOrganization("saf-group");
	}

	public static void initRegistryConfig(RegistryConfig config) {
		config.setVersion("1.0.0");
	}

	public static void initMonitorConfig(MonitorConfig config) {
		config.setVersion("1.0.0");
	}

	public static void initMetricsConfig(MetricsConfig config) {
		config.setPort("9001");
		config.setPrefix("dubbo");
	}

	public static void initProviderConfig(ProviderConfig config) {
		config.setVersion("1.0.0");
	}

	public static void initConsumerConfig(ConsumerConfig config) {
		config.setVersion("1.0.0");
	}

	public static void initProtocolConfig(ProtocolConfig config) {
		config.setName("dubbo");
		config.setPort(10001);
	}

	public static String getShortName(String str) {
		if (StringUtils.isNotEmpty(str)) {
			final int i1 = StringUtils.lastIndexOf(str, '.');
			final int i2 = StringUtils.lastIndexOf(str, '.', i1 - 1);
			return StringUtils.substring(str, i2 + 1);
		}
		return str;
	}

	public static void main(String[] args) {
		System.out.println(getShortName("com.future.app.sample.rpc.api.IDemoMotanComplexService"));
		System.out.println(getShortName("java.lang.String"));
		System.out.println(getShortName("Void"));
		System.out.println(getShortName("IAddService"));
	}
}
