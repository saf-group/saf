package com.future.saf.rpc.motan.core;

import com.future.saf.core.CustomizedConfigurationPropertiesBinder;
import com.future.saf.rpc.motan.MotanConstant;
import com.future.saf.rpc.motan.util.MotanUtil;
import com.weibo.api.motan.config.springsupport.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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

public class MotanBeanPostProcessor implements BeanPostProcessor, Ordered, EnvironmentAware, BeanFactoryAware {

	@Autowired
	protected CustomizedConfigurationPropertiesBinder binder;

	private Environment environment;
	private BeanFactory beanFactory;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

		String instance = MotanRegistrar.instanceMap.get(beanName);
		String beanNamePrefix = MotanRegistrar.beanNamePrefixMap.get(beanName);

		if (bean instanceof RegistryConfigBean) {

			RegistryConfigBean registryConfigBean = (RegistryConfigBean) bean;
			MotanUtil.initRegistryConfig(registryConfigBean);

			String globalPrefix = MotanConstant.PREFIX_MOTAN + ".registry";
			String nsPrefix = MotanConstant.PREFIX_MOTAN + "." + instance + ".registry";

			if (StringUtils.isAllEmpty(environment.getProperty(globalPrefix + ".address"),
					environment.getProperty(nsPrefix + ".address"))) {
				throw new IllegalArgumentException(String.format("%s or %s can not be null!!", globalPrefix, nsPrefix));
			}

			if (StringUtils.isNotEmpty(environment.getProperty(globalPrefix + ".address"))) {
				Bindable<?> target = Bindable.of(RegistryConfigBean.class).withExistingValue(registryConfigBean);
				binder.bind(globalPrefix, target);
			}

			Bindable<?> target = Bindable.of(RegistryConfigBean.class).withExistingValue(registryConfigBean);
			binder.bind(nsPrefix, target);

		} else if (bean instanceof ProtocolConfigBean) {

			ProtocolConfigBean protocolConfigBean = (ProtocolConfigBean) bean;
			MotanUtil.initProtocolConfig(protocolConfigBean);

			Bindable<?> target = Bindable.of(ProtocolConfigBean.class).withExistingValue(protocolConfigBean);
			binder.bind(MotanConstant.PREFIX_MOTAN + "." + instance + ".protocol", target);
			protocolConfigBean.setBeanName(beanName);

		} else if (bean instanceof BasicServiceConfigBean) {

			String registryBeanName = beanNamePrefix + RegistryConfigBean.class.getSimpleName();
			RegistryConfigBean registryConfigBean = beanFactory.getBean(registryBeanName, RegistryConfigBean.class);
			Assert.notNull(registryConfigBean,
					String.format("%s does not existed in spring context!", registryBeanName));

			String protocolBeanName = beanNamePrefix + ProtocolConfigBean.class.getSimpleName();
			ProtocolConfigBean protocolConfigBean = beanFactory.getBean(protocolBeanName, ProtocolConfigBean.class);
			Assert.notNull(protocolConfigBean,
					String.format("%s does not existed in spring context!", protocolBeanName));

			String portKey = MotanConstant.PREFIX_MOTAN + "." + instance + ".port";
			String port = environment.getProperty(portKey);
			if (StringUtils.isEmpty(port)) {
				port = "10010";
			}
			Assert.isTrue(StringUtils.isNotEmpty(port) && NumberUtils.isCreatable(port),
					String.format("%s=%s must be not null! and must be a number!", portKey, port));

			BasicServiceConfigBean basicServiceConfigBean = (BasicServiceConfigBean) bean;
			MotanUtil.initBasicServiceConfig(registryConfigBean, protocolConfigBean, Integer.parseInt(port),
					basicServiceConfigBean);

			Bindable<?> target = Bindable.of(BasicServiceConfigBean.class).withExistingValue(basicServiceConfigBean);
			binder.bind(MotanConstant.PREFIX_MOTAN + "." + instance + ".basic-service", target);

		} else if (bean instanceof BasicRefererConfigBean) {

			String registryBeanName = beanNamePrefix + RegistryConfigBean.class.getSimpleName();
			RegistryConfigBean registryConfigBean = beanFactory.getBean(registryBeanName, RegistryConfigBean.class);
			Assert.notNull(registryConfigBean,
					String.format("%s does not existed in spring context!", registryBeanName));

			String protocolBeanName = beanNamePrefix + ProtocolConfigBean.class.getSimpleName();
			ProtocolConfigBean protocolConfigBean = beanFactory.getBean(protocolBeanName, ProtocolConfigBean.class);
			Assert.notNull(protocolConfigBean,
					String.format("%s does not existed in spring context!", protocolBeanName));

			BasicRefererConfigBean basicRefererConfigBean = (BasicRefererConfigBean) bean;
			MotanUtil.initBasicRefererConfig(registryConfigBean, protocolConfigBean, basicRefererConfigBean);

			Bindable<?> target = Bindable.of(BasicRefererConfigBean.class).withExistingValue(basicRefererConfigBean);
			binder.bind(MotanConstant.PREFIX_MOTAN + "." + instance + ".basic-referer", target);

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
