package com.future.saf.rpc.dubbo.core;

import java.lang.reflect.Field;
import java.util.Set;

import org.apache.dubbo.config.MethodConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.bind.Bindable;

import com.ctrip.framework.apollo.enums.PropertyChangeType;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.future.saf.core.CustomizedConfigurationPropertiesBinder;
import com.future.saf.rpc.dubbo.SafDubboConstant;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 这个类的目的：
 * 
 * 想以methodConfig为例实现dubbo config的热更新，但不现实。
 * 
 * 可以很优雅的实现MethodConfig的属性的热更新，但是不能绑定到对应的serviceBean上，就不能生效，详细代码请参见dubbo源码中的ServiceConfig。
 * 
 * 如果硬做也可以，比如用反射等，但是侵入性太强，雷有点大，暂时先停薪留职。看后续dubbo是否提供优雅的口子。
 * 
 * @author hpy
 *
 */
@Slf4j
@ConditionalOnClass({ EnableApolloConfig.class })
@Deprecated
public class SafDubboConfigRefreshAutoConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(SafDubboConfigRefreshAutoConfiguration.class);

	@Autowired
	private CustomizedConfigurationPropertiesBinder binder;

	/**
	 * {
	 * 
	 * dubbo.shoprpc.method - config.test - same - method - name.timeout =
	 * ConfigChange {
	 * 
	 * namespace = 'saf.dubbo.method-config',
	 * 
	 * propertyName =
	 * 'dubbo.shoprpc.method-config.test-same-method-name.timeout',
	 * 
	 * oldValue = '1000',
	 * 
	 * newValue = '2000',
	 * 
	 * changeType = MODIFIED } }
	 **/
	@ApolloConfigChangeListener(value = { "saf.dubbo.method-config" })
	private void onChange(ConfigChangeEvent changeEvent) {
		try {
			log.info(changeEvent.getNamespace());
			Set<String> changedKeys = changeEvent.changedKeys();
			MethodConfig methodConfig = null;
			for (String changedKey : changedKeys) {
				try {

					ConfigChange cc = changeEvent.getChange(changedKey);

					// 不处理删除事件
					if (cc.getChangeType() == PropertyChangeType.DELETED) {
						break;
					}

					// key:
					// dubbo.shoprpc.method-config.test-same-method-name.timeout
					String[] changedKeyArray = changedKey.split("\\.");
					String instance = changedKeyArray[1];
					String methodApolloName = changedKeyArray[3];
					String mckey = instance + "." + methodApolloName;

					// 通过下述主石雕的代码你可以看到propertysource中的值已经被更新
					// MutablePropertySources mutablePropertySources =
					// applicationContext.getEnvironment()
					// .getPropertySources();
					// // ApolloBootstrapPropertySources, ApolloPropertySources
					// CompositePropertySource apolloCompositePropertySource =
					// (CompositePropertySource) mutablePropertySources
					// .get("ApolloPropertySources");
					// Collection<PropertySource<?>> propertySources =
					// apolloCompositePropertySource.getPropertySources();
					//
					// for (PropertySource<?> ps : propertySources) {
					// ConfigPropertySource cps = (ConfigPropertySource) ps;
					// Config config = cps.getSource();
					// System.out.println(config.getPropertyNames());
					// }

					synchronized (SafDubboBeanValueBindingPostProcessor.instanceAndMethodNameToMethodConfigMap) {

						synchronized (SafDubboBeanValueBindingPostProcessor.instanceToMethodConfigMap) {

							methodConfig = SafDubboBeanValueBindingPostProcessor.instanceAndMethodNameToMethodConfigMap
									.get(mckey);

							// if
							// null说明之前没有配置过这个method-config，需要初始化并和对应的serviceBean关联
							boolean methodConfigNotExist = false;
							if (methodConfig == null) {
								methodConfigNotExist = true;

								methodConfig = new MethodConfig();
								methodConfig.setName(captureName(methodApolloName));
							}

							String mcNSPrefix = SafDubboConstant.PREFIX_DUBBO + "." + instance + ".method-config."
									+ methodApolloName;

							Bindable<?> mcTarget = Bindable.of(MethodConfig.class).withExistingValue(methodConfig);
							binder.bind(mcNSPrefix, mcTarget);

							if (methodConfigNotExist) {
								SafDubboBeanValueBindingPostProcessor.putMethodConfig(instance, methodApolloName,
										methodConfig);
							}

							log.info(
									String.format("changed dubbo-method-config success: %s, old-value:%s, new-value:%s",
											mckey, cc.getOldValue(), cc.getNewValue()));
						}
					}

				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private String captureName(String name) {
		String[] tempArray = name.split("-");
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String tempStr : tempArray) {
			if (first) {
				sb.append(tempStr);
				first = false;
			} else {
				char[] cs = tempStr.toCharArray();
				cs[0] -= 32;
				sb.append(String.valueOf(cs));
			}
		}
		return sb.toString();
	}

	public static void main(String[] array) throws Exception {
		String test = "test-same-method-name";
		System.out.println(new SafDubboConfigRefreshAutoConfiguration().captureName(test));

		MethodConfig methodConfig = new MethodConfig();
		Class<?> clazz = methodConfig.getClass();// 获取字节码对象
		Field f = clazz.getDeclaredField("name");
		f.setAccessible(true);
		f.set(methodConfig, "1000");

		System.out.println(methodConfig.getName());

	}

}
