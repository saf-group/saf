package com.future.saf.rpc.dubbo.core;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.MetricsConfig;
import org.apache.dubbo.config.ModuleConfig;
import org.apache.dubbo.config.MonitorConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.spring.ReferenceBean;
import org.apache.dubbo.config.spring.ServiceBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

import com.future.saf.core.CustomizedConfigurationPropertiesBinder;
import com.future.saf.rpc.dubbo.SafDubboConstant;
import com.future.saf.rpc.dubbo.util.SafDubboUtil;

public class SafDubboBeanPostProcessor implements BeanPostProcessor, Ordered, EnvironmentAware, BeanFactoryAware {

	@Autowired
	protected CustomizedConfigurationPropertiesBinder binder;

	private Environment environment;
	@SuppressWarnings("unused")
	private BeanFactory beanFactory;

	public static final String PREFIX_APP_MOTAN = "motan";

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

		String project = SafDubboRegistrar.projectMap.get(beanName);
		String instance = SafDubboRegistrar.instanceMap.get(beanName);

		// ApplicationConfig 每个project只有一个
		if (bean instanceof ApplicationConfig) {

			ApplicationConfig applicationConfigBean = (ApplicationConfig) bean;
			SafDubboUtil.initApplicationConfig(applicationConfigBean);

			String nsPrefix = SafDubboConstant.PREFIX_DUBBO + "." + project + ".application";

			Bindable<?> target = Bindable.of(ApplicationConfig.class).withExistingValue(applicationConfigBean);
			binder.bind(nsPrefix, target);

		}
		// ModuleConfig 每个project只有一个
		else if (bean instanceof ModuleConfig) {
			ModuleConfig moduleConfigBean = (ModuleConfig) bean;
			SafDubboUtil.initModuleConfig(moduleConfigBean);

			String nsPrefix = SafDubboConstant.PREFIX_DUBBO + "." + project + ".module";

			Bindable<?> target = Bindable.of(ModuleConfig.class).withExistingValue(moduleConfigBean);
			binder.bind(nsPrefix, target);
		}
		// RegistryConfig jvm只有一个，不支持多注册中心(天坑)
		else if (bean instanceof RegistryConfig) {
			RegistryConfig registryConfigBean = (RegistryConfig) bean;
			SafDubboUtil.initRegistryConfig(registryConfigBean);

			String globalPrefix = SafDubboConstant.PREFIX_DUBBO + ".registry";
			String nsPrefix = SafDubboConstant.PREFIX_DUBBO + "." + project + ".registry";

			if (StringUtils.isAllEmpty(environment.getProperty(globalPrefix + ".address"),
					environment.getProperty(nsPrefix + ".address"))) {
				throw new IllegalArgumentException(String.format("%s or %s can not be null!!", globalPrefix, nsPrefix));
			}

			if (StringUtils.isNotEmpty(environment.getProperty(globalPrefix + ".address"))) {
				Bindable<?> target = Bindable.of(RegistryConfig.class).withExistingValue(registryConfigBean);
				binder.bind(globalPrefix, target);
			}

			Bindable<?> target = Bindable.of(RegistryConfig.class).withExistingValue(registryConfigBean);
			binder.bind(nsPrefix, target);
		}
		// MonitorConfig jvm只有一个
		else if (bean instanceof MonitorConfig) {
			MonitorConfig monitorConfigBean = (MonitorConfig) bean;
			SafDubboUtil.initMonitorConfig(monitorConfigBean);

			String nsPrefix = SafDubboConstant.PREFIX_DUBBO + "." + project + ".monitor";

			Bindable<?> target = Bindable.of(MonitorConfig.class).withExistingValue(monitorConfigBean);
			binder.bind(nsPrefix, target);
		}
		// MetricsConfig jvm只有一个
		else if (bean instanceof MetricsConfig) {
			MetricsConfig metricsConfigBean = (MetricsConfig) bean;
			SafDubboUtil.initMetricsConfig(metricsConfigBean);

			String nsPrefix = SafDubboConstant.PREFIX_DUBBO + "." + project + ".metric";

			Bindable<?> target = Bindable.of(MetricsConfig.class).withExistingValue(metricsConfigBean);
			binder.bind(nsPrefix, target);
		} else if (bean instanceof ProviderConfig) {
			ProviderConfig providerConfigBean = (ProviderConfig) bean;
			SafDubboUtil.initProviderConfig(providerConfigBean);

			String nsPrefix = SafDubboConstant.PREFIX_DUBBO + "." + project + ".provider-config";

			Bindable<?> target = Bindable.of(ProviderConfig.class).withExistingValue(providerConfigBean);
			binder.bind(nsPrefix, target);
		} else if (bean instanceof ConsumerConfig) {
			ConsumerConfig consumerConfigBean = (ConsumerConfig) bean;
			SafDubboUtil.initConsumerConfig(consumerConfigBean);

			String nsPrefix = SafDubboConstant.PREFIX_DUBBO + "." + project + ".consumer-config";

			Bindable<?> target = Bindable.of(ConsumerConfig.class).withExistingValue(consumerConfigBean);
			binder.bind(nsPrefix, target);
		}
		// 可以有多个，这样不同允许不同的remote service使用不同的port
		else if (bean instanceof ProtocolConfig) {
			ProtocolConfig protocolConfigBean = (ProtocolConfig) bean;
			SafDubboUtil.initProtocolConfig(protocolConfigBean);

			String nsPrefix = SafDubboConstant.PREFIX_DUBBO + "." + project + ".protocol";

			Bindable<?> target = Bindable.of(ProtocolConfig.class).withExistingValue(protocolConfigBean);
			binder.bind(nsPrefix, target);
		}
		// 注入Service apollo配置
		else if (bean instanceof ServiceBean) {
			ServiceBean<?> serviceBean = (ServiceBean<?>) bean;

			String nsPrefix = SafDubboConstant.PREFIX_DUBBO + "." + instance + ".protocol";

			Bindable<?> target = Bindable.of(ServiceBean.class).withExistingValue(serviceBean);
			binder.bind(nsPrefix, target);
		}
		// 注入Reference apollo配置
		else if (bean instanceof ReferenceBean) {
			ReferenceBean<?> referenceBean = (ReferenceBean<?>) bean;

			String nsPrefix = SafDubboConstant.PREFIX_DUBBO + "." + instance + ".protocol";

			Bindable<?> target = Bindable.of(ReferenceBean.class).withExistingValue(referenceBean);
			binder.bind(nsPrefix, target);
		}

		return bean;
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
}
