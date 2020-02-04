package com.future.saf.http.apache.httpcomponents.syncimpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import com.future.saf.core.util.BeanRegistrationUtil;

public class HttpBioClientRegistrar implements ImportBeanDefinitionRegistrar {

	static Map<String, String> instanceMap = new ConcurrentHashMap<String, String>();

	static Map<String, String> beanNamePrefixMap = new ConcurrentHashMap<String, String>();

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		boolean processed = false;
		{
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(importingClassMetadata.getAnnotationAttributes(EnableHttpBioClient.class.getName()));
			if (attributes != null) {
				dealOne(registry, attributes);
				processed = true;
			}
		}
		{
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(importingClassMetadata.getAnnotationAttributes(EnableHttpBioClients.class.getName()));
			if (attributes != null) {
				AnnotationAttributes[] annotationArray = attributes.getAnnotationArray("value");
				for (AnnotationAttributes oneAttributes : annotationArray) {
					dealOne(registry, oneAttributes);
					processed = true;
				}
			}
		}
		if (!processed)
			throw new IllegalStateException("no @EnableCHttpClient or @EnableCHttpClients found! pls check!");
	}

	private void dealOne(BeanDefinitionRegistry registry, AnnotationAttributes oneAttributes) {
		String beanNamePrefix = oneAttributes.getString("beanNamePrefix");
		String instance = oneAttributes.getString("instance");

		instanceMap.put(beanNamePrefix + HttpBioClient.class.getSimpleName(), instance);
		beanNamePrefixMap.put(beanNamePrefix + HttpBioClient.class.getSimpleName(), beanNamePrefix);

		Assert.isTrue(StringUtils.isNotEmpty(beanNamePrefix), "beanNamePrefix must be specified!");

		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				beanNamePrefix + HttpBioClient.class.getSimpleName(), HttpBioClientFactoryBean.class);

	}

}
