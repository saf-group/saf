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
//import org.apache.dubbo.config.spring.ReferenceBean;
//import org.apache.dubbo.config.spring.ServiceBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import com.future.saf.core.util.BeanRegistrationUtil;
import com.future.saf.rpc.dubbo.EnableSafDubbo;
import com.future.saf.rpc.dubbo.EnableSafDubbos;

public class SafDubboRegistrar implements ImportBeanDefinitionRegistrar {

	static String project;

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
		project = oneAttributes.getString("project");

		Assert.isTrue(StringUtils.isNotEmpty(beanNamePrefix), "beanNamePrefix must be specified!");
		Assert.isTrue(StringUtils.isNotEmpty(instance), "instance must be specified!");
		Assert.isTrue(StringUtils.isNotEmpty(project), "project must be specified!");

		// DubboNamespaceHandler.java
		instanceMap.put(beanNamePrefix + ProtocolConfig.class.getSimpleName(), instance);
		beanNamePrefixMap.put(beanNamePrefix + ProtocolConfig.class.getSimpleName(), beanNamePrefix);

		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				beanNamePrefix + SafDubboBeanValueBindingPostProcessor.class.getSimpleName(),
				SafDubboBeanValueBindingPostProcessor.class);
		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				beanNamePrefix + ProtocolConfig.class.getSimpleName(), ProtocolConfig.class);

	}

}
