package com.future.saf.flowcontrol.sentinel.basic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import com.future.saf.core.util.BeanRegistrationUtil;
import com.future.saf.flowcontrol.sentinel.basic.exception.SentinelBeanInitException;

public class SentinelRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

	static Map<String, String> projectMap = new ConcurrentHashMap<String, String>();
	static Map<String, String> instanceMap = new ConcurrentHashMap<String, String>();
	static Map<String, String> datasourceMap = new ConcurrentHashMap<String, String>();

	@SuppressWarnings("unused")
	private ResourceLoader resourceLoader;

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

		{
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(importingClassMetadata.getAnnotationAttributes(EnableSentinels.class.getName()));
			if (attributes != null) {
				AnnotationAttributes[] annotationArray = attributes.getAnnotationArray("value");
				for (AnnotationAttributes oneAttributes : annotationArray) {
					register(registry, oneAttributes);
					processed = true;
				}
			}
		}
		if (!processed)
			throw new SentinelBeanInitException("no @EnableSentinel or @EnableSentinels found! please check code!");
	}

	private void register(BeanDefinitionRegistry registry, AnnotationAttributes oneAttributes) {

		String sentinelFactoryBeanName = oneAttributes.getString("sentinelFactoryBeanName");
		String project = oneAttributes.getString("project");
		String instance = oneAttributes.getString("instance");
		String datasource = oneAttributes.getString("datasource");

		Assert.isTrue(StringUtils.isNotEmpty(sentinelFactoryBeanName), "sentinelFactoryBeanName must be specified!");
		Assert.isTrue(StringUtils.isNotEmpty(project), "project must be specified!");
		Assert.isTrue(StringUtils.isNotEmpty(instance), "instance must be specified!");
		Assert.isTrue(StringUtils.isNotEmpty(datasource), "datasource must be specified!");

		projectMap.put(sentinelFactoryBeanName, project);
		instanceMap.put(sentinelFactoryBeanName, instance);
		datasourceMap.put(sentinelFactoryBeanName, datasource);

		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry, sentinelFactoryBeanName,
				SentinelFactoryBean.class);
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

}