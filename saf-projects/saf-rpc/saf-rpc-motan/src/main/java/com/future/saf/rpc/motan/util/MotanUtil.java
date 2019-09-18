package com.future.saf.rpc.motan.util;

import org.apache.commons.lang3.StringUtils;

import com.weibo.api.motan.config.springsupport.AnnotationBean;
import com.weibo.api.motan.config.springsupport.BasicRefererConfigBean;
import com.weibo.api.motan.config.springsupport.BasicServiceConfigBean;
import com.weibo.api.motan.config.springsupport.ProtocolConfigBean;
import com.weibo.api.motan.config.springsupport.RegistryConfigBean;

public class MotanUtil {

	public static void initProtocolConfig(ProtocolConfigBean config) {
		config.setName("motan");
		config.setMinWorkerThread(20);
		config.setMaxWorkerThread(200);
		config.setFilter("safProfiler");
		config.setHaStrategy("failover");
	}

	public static void initRegistryConfig(RegistryConfigBean config) {
		config.setAddress("localhost:2181");
		config.setRegProtocol("zookeeper");
		config.setRequestTimeout(1000);
		config.setConnectTimeout(3000);
	}

	public static void initBasicServiceConfig(RegistryConfigBean defaultRegistry, ProtocolConfigBean defaultProtocol,
			int port, BasicServiceConfigBean config) {
		config.setRegistry(defaultRegistry);
		config.setProtocol(defaultProtocol);
		config.setRegistry(defaultRegistry.getId());
		config.setCheck(false);
		config.setShareChannel(true);
		config.setRequestTimeout(30000);
		config.setExport(defaultProtocol.getId() + ":" + port);
	}

	public static void initBasicRefererConfig(RegistryConfigBean defaultRegistry, ProtocolConfigBean defaultProtocol,
			BasicRefererConfigBean config) {
		config.setAccessLog(false);
		config.setCheck(false);
		config.setShareChannel(true);
		config.setProtocol(defaultProtocol);
		config.setProtocol(defaultProtocol.getId());
		config.setRegistry(defaultRegistry);
		config.setRegistry(defaultRegistry.getId());
		config.setRequestTimeout(20000);
	}

	public static AnnotationBean createAnnotationBean() {
		return new AnnotationBean();
	}

	public static ProtocolConfigBean createProtocolConfig() {
		ProtocolConfigBean config = new ProtocolConfigBean();
		initProtocolConfig(config);
		return config;
	}

	public static RegistryConfigBean createRegistryConfig() {
		RegistryConfigBean config = new RegistryConfigBean();
		initRegistryConfig(config);
		return config;
	}

	public static BasicServiceConfigBean createBasicServiceConfig(RegistryConfigBean defaultRegistry,
			ProtocolConfigBean defaultProtocol, int port) {
		BasicServiceConfigBean config = new BasicServiceConfigBean();
		initBasicServiceConfig(defaultRegistry, defaultProtocol, port, config);
		return config;
	}

	public static BasicRefererConfigBean createBasicRefererConfig(RegistryConfigBean defaultRegistry,
			ProtocolConfigBean defaultProtocol) {
		BasicRefererConfigBean config = new BasicRefererConfigBean();
		initBasicRefererConfig(defaultRegistry, defaultProtocol, config);
		return config;
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
