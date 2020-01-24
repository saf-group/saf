package com.future.saf.rpc.dubbo.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.ModuleConfig;
import org.apache.dubbo.config.MonitorConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.spring.ReferenceBean;
import org.apache.dubbo.config.spring.ServiceBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import com.future.saf.core.util.BeanRegistrationUtil;
import com.future.saf.rpc.dubbo.EnableSafDubbo;
import com.future.saf.rpc.dubbo.EnableSafDubbos;

public class SafDubboRegistrar implements ImportBeanDefinitionRegistrar {

	static Map<String, String> projectMap = new ConcurrentHashMap<String, String>();

	static Map<String, String> instanceMap = new ConcurrentHashMap<String, String>();

	static Map<String, String> beanNamePrefixMap = new ConcurrentHashMap<String, String>();

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		boolean processed = false;
		{
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(importingClassMetadata.getAnnotationAttributes(EnableSafDubbo.class.getName()));
			if (attributes != null) {
				register(registry, attributes);
				processed = true;
			}
		}
		{
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(importingClassMetadata.getAnnotationAttributes(EnableSafDubbos.class.getName()));
			if (attributes != null) {
				AnnotationAttributes[] annotationArray = attributes.getAnnotationArray("value");
				for (AnnotationAttributes oneAttributes : annotationArray) {
					register(registry, oneAttributes);
					processed = true;
				}
			}
		}
		if (!processed)
			throw new IllegalStateException("no @EnableMotan or @EnableMotans found! pls check!");
	}

	private void register(BeanDefinitionRegistry registry, AnnotationAttributes oneAttributes) {
		String beanNamePrefix = oneAttributes.getString("beanNamePrefix");
		String instance = oneAttributes.getString("instance");
		String project = oneAttributes.getString("project");

		Assert.isTrue(StringUtils.isNotEmpty(beanNamePrefix), "beanNamePrefix must be specified!");
		Assert.isTrue(StringUtils.isNotEmpty(instance), "instance must be specified!");

		// DubboNamespaceHandler.java
		instanceMap.put(beanNamePrefix + ApplicationConfig.class.getSimpleName(), instance);
		instanceMap.put(beanNamePrefix + ModuleConfig.class.getSimpleName(), instance);
		instanceMap.put(beanNamePrefix + RegistryConfig.class.getSimpleName(), instance);
		instanceMap.put(beanNamePrefix + MonitorConfig.class.getSimpleName(), instance);
		instanceMap.put(beanNamePrefix + ProviderConfig.class.getSimpleName(), instance);
		instanceMap.put(beanNamePrefix + ConsumerConfig.class.getSimpleName(), instance);
		instanceMap.put(beanNamePrefix + ProtocolConfig.class.getSimpleName(), instance);
		// instanceMap.put(beanNamePrefix + ServiceBean.class.getSimpleName(),
		// instance);
		// instanceMap.put(beanNamePrefix + ReferenceBean.class.getSimpleName(),
		// instance);

		beanNamePrefixMap.put(beanNamePrefix + ApplicationConfig.class.getSimpleName(), beanNamePrefix);
		beanNamePrefixMap.put(beanNamePrefix + ModuleConfig.class.getSimpleName(), beanNamePrefix);
		beanNamePrefixMap.put(beanNamePrefix + RegistryConfig.class.getSimpleName(), beanNamePrefix);
		beanNamePrefixMap.put(beanNamePrefix + MonitorConfig.class.getSimpleName(), beanNamePrefix);
		beanNamePrefixMap.put(beanNamePrefix + ProviderConfig.class.getSimpleName(), beanNamePrefix);
		beanNamePrefixMap.put(beanNamePrefix + ConsumerConfig.class.getSimpleName(), beanNamePrefix);
		beanNamePrefixMap.put(beanNamePrefix + ProtocolConfig.class.getSimpleName(), beanNamePrefix);
		// beanNamePrefixMap.put(beanNamePrefix +
		// ServiceBean.class.getSimpleName(), beanNamePrefix);
		// beanNamePrefixMap.put(beanNamePrefix +
		// ReferenceBean.class.getSimpleName(), beanNamePrefix);

		projectMap.put(beanNamePrefix + ApplicationConfig.class.getSimpleName(), project);
		projectMap.put(beanNamePrefix + ModuleConfig.class.getSimpleName(), project);
		projectMap.put(beanNamePrefix + RegistryConfig.class.getSimpleName(), project);
		projectMap.put(beanNamePrefix + MonitorConfig.class.getSimpleName(), project);
		projectMap.put(beanNamePrefix + ProviderConfig.class.getSimpleName(), project);
		projectMap.put(beanNamePrefix + ConsumerConfig.class.getSimpleName(), project);
		projectMap.put(beanNamePrefix + ProtocolConfig.class.getSimpleName(), project);
		// projectMap.put(beanNamePrefix + ServiceBean.class.getSimpleName(),
		// project);
		// projectMap.put(beanNamePrefix + ReferenceBean.class.getSimpleName(),
		// project);

		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				beanNamePrefix + SafDubboBeanPostProcessor.class.getSimpleName(), SafDubboBeanPostProcessor.class);

		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				beanNamePrefix + ApplicationConfig.class.getSimpleName(), ApplicationConfig.class);
		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				beanNamePrefix + ModuleConfig.class.getSimpleName(), ModuleConfig.class);
		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				beanNamePrefix + RegistryConfig.class.getSimpleName(), RegistryConfig.class);
		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				beanNamePrefix + MonitorConfig.class.getSimpleName(), MonitorConfig.class);
		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				beanNamePrefix + ProviderConfig.class.getSimpleName(), ProviderConfig.class);
		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				beanNamePrefix + ConsumerConfig.class.getSimpleName(), ConsumerConfig.class);
		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				beanNamePrefix + ProtocolConfig.class.getSimpleName(), ProtocolConfig.class);
		// BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
		// beanNamePrefix + ServiceBean.class.getSimpleName(),
		// ServiceBean.class);
		// BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
		// beanNamePrefix + ReferenceBean.class.getSimpleName(),
		// ReferenceBean.class);
	}

}
