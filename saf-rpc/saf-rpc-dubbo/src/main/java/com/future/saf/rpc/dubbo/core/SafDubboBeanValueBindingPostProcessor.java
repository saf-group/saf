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
import org.springframework.util.Assert;

import com.future.saf.core.CustomizedConfigurationPropertiesBinder;
import com.future.saf.rpc.dubbo.SafDubboConstant;
import com.future.saf.rpc.dubbo.util.SafDubboUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SafDubboBeanValueBindingPostProcessor
		implements BeanPostProcessor, Ordered, EnvironmentAware, BeanFactoryAware {

	@Autowired
	protected CustomizedConfigurationPropertiesBinder binder;

	private Environment environment;
	private BeanFactory beanFactory;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

		String project = SafDubboRegistrar.project;
		String instance = SafDubboRegistrar.instanceMap.get(beanName);
		String beanNamePrefix = SafDubboRegistrar.beanNamePrefixMap.get(beanName);

		// ApplicationConfig 每个project只有一个
		if (bean instanceof ApplicationConfig) {
			log.info("begin to bind config to applicationConfig:" + beanName);
			ApplicationConfig applicationConfigBean = (ApplicationConfig) bean;
			SafDubboUtil.initApplicationConfig(applicationConfigBean);

			String nsPrefix = SafDubboConstant.PREFIX_DUBBO + "." + project + ".application";

			Bindable<?> target = Bindable.of(ApplicationConfig.class).withExistingValue(applicationConfigBean);
			binder.bind(nsPrefix, target);

		}
		// ModuleConfig 每个project只有一个
		else if (bean instanceof ModuleConfig) {
			log.info("begin to bind config to moduleConfig:" + beanName);
			ModuleConfig moduleConfigBean = (ModuleConfig) bean;
			SafDubboUtil.initModuleConfig(moduleConfigBean);

			String nsPrefix = SafDubboConstant.PREFIX_DUBBO + "." + project + ".module";

			Bindable<?> target = Bindable.of(ModuleConfig.class).withExistingValue(moduleConfigBean);
			binder.bind(nsPrefix, target);
		}
		// RegistryConfig jvm只有一个，不支持多注册中心(天坑)
		else if (bean instanceof RegistryConfig) {
			log.info("begin to bind config to registryConfig:" + beanName);
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
			log.info("begin to bind config to monitorConfig:" + beanName);
			MonitorConfig monitorConfigBean = (MonitorConfig) bean;
			SafDubboUtil.initMonitorConfig(monitorConfigBean);

			String nsPrefix = SafDubboConstant.PREFIX_DUBBO + "." + project + ".monitor";

			Bindable<?> target = Bindable.of(MonitorConfig.class).withExistingValue(monitorConfigBean);
			binder.bind(nsPrefix, target);
		}
		// MetricsConfig jvm只有一个
		else if (bean instanceof MetricsConfig) {
			log.info("begin to bind config to metricsConfig:" + beanName);
			MetricsConfig metricsConfigBean = (MetricsConfig) bean;
			SafDubboUtil.initMetricsConfig(metricsConfigBean);

			String nsPrefix = SafDubboConstant.PREFIX_DUBBO + "." + project + ".metric";

			Bindable<?> target = Bindable.of(MetricsConfig.class).withExistingValue(metricsConfigBean);
			binder.bind(nsPrefix, target);
		} else if (bean instanceof ProviderConfig) {
			log.info("begin to bind config to providerConfig:" + beanName);
			ProviderConfig providerConfigBean = (ProviderConfig) bean;
			SafDubboUtil.initProviderConfig(providerConfigBean);

			String nsPrefix = SafDubboConstant.PREFIX_DUBBO + "." + project + ".provider-config";

			Bindable<?> target = Bindable.of(ProviderConfig.class).withExistingValue(providerConfigBean);
			binder.bind(nsPrefix, target);

			providerConfigBean.setFilter("safDubboProviderFilter");
		} else if (bean instanceof ConsumerConfig) {
			log.info("begin to bind config to consumerConfig:" + beanName);
			ConsumerConfig consumerConfigBean = (ConsumerConfig) bean;
			SafDubboUtil.initConsumerConfig(consumerConfigBean);

			String nsPrefix = SafDubboConstant.PREFIX_DUBBO + "." + project + ".consumer-config";

			Bindable<?> target = Bindable.of(ConsumerConfig.class).withExistingValue(consumerConfigBean);
			binder.bind(nsPrefix, target);

			consumerConfigBean.setFilter("safDubboConsumerFilter");
		}
		// 可以有多个，这样不同允许不同的remote service使用不同的port
		else if (bean instanceof ProtocolConfig) {
			log.info("begin to bind config to protocolConfig:" + beanName);
			ProtocolConfig protocolConfigBean = (ProtocolConfig) bean;
			SafDubboUtil.initProtocolConfig(protocolConfigBean);

			String nsPrefix = SafDubboConstant.PREFIX_DUBBO + "." + instance + ".protocol";

			Bindable<?> target = Bindable.of(ProtocolConfig.class).withExistingValue(protocolConfigBean);
			binder.bind(nsPrefix, target);
		}
		// 注入Service apollo配置
		else if (bean instanceof ServiceBean) {
			String[] tarray = beanName.split("\\.");
			beanNamePrefix = tarray[tarray.length - 1].toLowerCase();
			log.info(String.format("begin to bind config to serviceBean: %s, beanNamePrefix: %s", beanName,
					beanNamePrefix));

			ServiceBean<?> serviceBean = (ServiceBean<?>) bean;

			// bind applicationconfig
			String applicationBeanName = "default" + ApplicationConfig.class.getSimpleName();
			ApplicationConfig applicationConfigBean = beanFactory.getBean(applicationBeanName, ApplicationConfig.class);
			Assert.notNull(applicationConfigBean,
					String.format("%s does not existed in spring context!", applicationBeanName));
			serviceBean.setApplication(applicationConfigBean);

			// bind moduleconfig
			String moduleBeanName = "default" + ModuleConfig.class.getSimpleName();
			ModuleConfig moduleConfigBean = beanFactory.getBean(moduleBeanName, ModuleConfig.class);
			Assert.notNull(moduleConfigBean, String.format("%s does not existed in spring context!", moduleBeanName));
			serviceBean.setModule(moduleConfigBean);

			// bind registryconfig
			String registryBeanName = "default" + RegistryConfig.class.getSimpleName();
			RegistryConfig registryConfigBean = beanFactory.getBean(registryBeanName, RegistryConfig.class);
			Assert.notNull(registryConfigBean,
					String.format("%s does not existed in spring context!", registryBeanName));
			serviceBean.setRegistry(registryConfigBean);

			// bind protocolconfig
			String protocolBeanName = beanNamePrefix + ProtocolConfig.class.getSimpleName();
			ProtocolConfig protocolConfigBean = beanFactory.getBean(protocolBeanName, ProtocolConfig.class);
			Assert.notNull(protocolConfigBean,
					String.format("%s does not existed in spring context!", protocolBeanName));
			serviceBean.setProtocol(protocolConfigBean);

			String nsPrefix = SafDubboConstant.PREFIX_DUBBO + "." + instance + ".service-bean";

			Bindable<?> target = Bindable.of(ServiceBean.class).withExistingValue(serviceBean);
			binder.bind(nsPrefix, target);
		}
		// // 注入Reference apollo配置
		// else if (bean instanceof ReferenceBean) {
		// String[] tarray = beanName.split("\\.");
		// beanNamePrefix = tarray[tarray.length - 1].toLowerCase();
		// log.info(String.format("begin to bind config to referenceBean: %s,
		// beanNamePrefix: %s", beanName,
		// beanNamePrefix));
		//
		// ReferenceBean<?> referenceBean = (ReferenceBean<?>) bean;
		//
		// // bind applicationconfig
		// String applicationBeanName = beanNamePrefix +
		// ApplicationConfig.class.getSimpleName();
		// ApplicationConfig applicationConfigBean =
		// beanFactory.getBean(applicationBeanName, ApplicationConfig.class);
		// Assert.notNull(applicationConfigBean,
		// String.format("%s does not existed in spring context!",
		// applicationBeanName));
		// referenceBean.setApplication(applicationConfigBean);
		//
		// // bind registryconfig
		// String registryBeanName = beanNamePrefix +
		// RegistryConfig.class.getSimpleName();
		// RegistryConfig registryConfigBean =
		// beanFactory.getBean(registryBeanName, RegistryConfig.class);
		// Assert.notNull(registryConfigBean,
		// String.format("%s does not existed in spring context!",
		// registryBeanName));
		// referenceBean.setRegistry(registryConfigBean);
		//
		// String nsPrefix = SafDubboConstant.PREFIX_DUBBO + "." + instance +
		// ".reference-bean";
		//
		// Bindable<?> target =
		// Bindable.of(ReferenceBean.class).withExistingValue(referenceBean);
		// binder.bind(nsPrefix, target);
		//
		// referenceBean.setFilter("safDubboConsumerFilter");
		// }

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
