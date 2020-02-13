package com.future.saf.flowcontrol.sentinel.basic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import com.future.saf.core.util.BeanRegistrationUtil;
import com.future.saf.flowcontrol.sentinel.basic.exception.SentinelBeanInitException;

public class SentinelRegistrar implements ImportBeanDefinitionRegistrar {

	static Map<String, String> datasourceMap = new ConcurrentHashMap<String, String>();

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		boolean processed = false;

		{
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(importingClassMetadata.getAnnotationAttributes(EnableSentinel.class.getName()));
			if (attributes != null) {
				register(registry, attributes);
				processed = true;
			}
		}

		if (!processed)
			throw new SentinelBeanInitException("no @EnableSentinel found! please check code!");
	}

	private void register(BeanDefinitionRegistry registry, AnnotationAttributes oneAttributes) {

		String beanNamePrefix = oneAttributes.getString("beanNamePrefix");
		String datasource = oneAttributes.getString("datasource");

		Assert.isTrue(StringUtils.isNotEmpty(beanNamePrefix), "beanNamePrefix must be specified!");
		Assert.isTrue(StringUtils.isNotEmpty(datasource), "datasource must be specified!");

		datasourceMap.put(beanNamePrefix + AbstractSentinelHolder.class.getSimpleName(), datasource);

		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				beanNamePrefix + AbstractSentinelHolder.class.getSimpleName(), SentinelFactoryBean.class);
	}

}