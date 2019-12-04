package com.future.saf.rpc.motan.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import com.future.saf.core.util.BeanRegistrationUtil;
import com.future.saf.rpc.motan.EnableMotan;
import com.future.saf.rpc.motan.EnableMotans;
import com.weibo.api.motan.config.springsupport.AnnotationBean;
import com.weibo.api.motan.config.springsupport.BasicRefererConfigBean;
import com.weibo.api.motan.config.springsupport.BasicServiceConfigBean;
import com.weibo.api.motan.config.springsupport.ProtocolConfigBean;
import com.weibo.api.motan.config.springsupport.RegistryConfigBean;

public class MotanRegistrar implements ImportBeanDefinitionRegistrar {

	static Map<String, String> instanceMap = new ConcurrentHashMap<String, String>();

	static Map<String, String> beanNamePrefixMap = new ConcurrentHashMap<String, String>();

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		boolean processed = false;
		{
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(importingClassMetadata.getAnnotationAttributes(EnableMotan.class.getName()));
			if (attributes != null) {
				register(registry, attributes);
				processed = true;
			}
		}
		{
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(importingClassMetadata.getAnnotationAttributes(EnableMotans.class.getName()));
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

		Assert.isTrue(StringUtils.isNotEmpty(beanNamePrefix), "beanNamePrefix must be specified!");
		Assert.isTrue(StringUtils.isNotEmpty(instance), "instance must be specified!");

		instanceMap.put(beanNamePrefix + BasicServiceConfigBean.class.getSimpleName(), instance);
		instanceMap.put(beanNamePrefix + BasicRefererConfigBean.class.getSimpleName(), instance);
		instanceMap.put(beanNamePrefix + RegistryConfigBean.class.getSimpleName(), instance);
		instanceMap.put(beanNamePrefix + ProtocolConfigBean.class.getSimpleName(), instance);

		beanNamePrefixMap.put(beanNamePrefix + BasicServiceConfigBean.class.getSimpleName(), beanNamePrefix);
		beanNamePrefixMap.put(beanNamePrefix + BasicRefererConfigBean.class.getSimpleName(), beanNamePrefix);
		beanNamePrefixMap.put(beanNamePrefix + RegistryConfigBean.class.getSimpleName(), beanNamePrefix);
		beanNamePrefixMap.put(beanNamePrefix + ProtocolConfigBean.class.getSimpleName(), beanNamePrefix);

		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				beanNamePrefix + MotanBeanPostProcessor.class.getSimpleName(), MotanBeanPostProcessor.class);
		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry, AnnotationBean.class.getSimpleName(),
				AnnotationBean.class);

		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				beanNamePrefix + RegistryConfigBean.class.getSimpleName(), RegistryConfigBean.class);
		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				beanNamePrefix + ProtocolConfigBean.class.getSimpleName(), ProtocolConfigBean.class);
		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				beanNamePrefix + BasicServiceConfigBean.class.getSimpleName(), BasicServiceConfigBean.class);
		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				beanNamePrefix + BasicRefererConfigBean.class.getSimpleName(), BasicRefererConfigBean.class);
	}

}
