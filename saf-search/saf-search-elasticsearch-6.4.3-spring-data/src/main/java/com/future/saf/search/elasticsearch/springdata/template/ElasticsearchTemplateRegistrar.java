package com.future.saf.search.elasticsearch.springdata.template;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import com.future.saf.core.util.BeanRegistrationUtil;
import com.future.saf.search.exception.SearchBeanInitException;

public class ElasticsearchTemplateRegistrar implements ImportBeanDefinitionRegistrar {

	static Map<String, String> templateBeanNameToClusterNameMap = new HashMap<String, String>();

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		boolean registResult = false;

		// 处理EnableSearches注解
		{
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(metadata.getAnnotationAttributes(EnableElasticsearchTemplates.class.getName()));
			if (attributes != null) {
				AnnotationAttributes[] annotationArray = attributes.getAnnotationArray("value");
				if (annotationArray != null && annotationArray.length > 0) {
					for (AnnotationAttributes e : annotationArray) {
						register(registry, e);
						registResult = true;
					}
				}
			}
		}

		// 处理EnableSearch注解
		{
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(metadata.getAnnotationAttributes(EnableElasticsearchTemplate.class.getName()));
			if (attributes != null) {
				register(registry, attributes);
				registResult = true;
			}
		}

		if (!registResult)
			throw new SearchBeanInitException(
					"Both @EnableElasticsearchTemplate and @EnableElasticsearchTemplates were not found. check your search annotation.");
	}

	private void register(BeanDefinitionRegistry registry, AnnotationAttributes attributes) {

		try {
			String clusterName = attributes.getString("clusterName");

			Assert.isTrue(StringUtils.isNotEmpty(clusterName), "clusterName must be specified!");

			// 不同的search客户端实现方式不一样。
			boolean registerResult = registerBeanDefinitionIfNotExists(registry, clusterName);

			Assert.isTrue(registerResult,
					"@EnableElasticsearchTemplate or @EnableElasticsearchTemplates regist failed. check your search annotation.");
		} catch (Exception e) {
			throw new SearchBeanInitException(e.getMessage(), e);
		}
	}

	private boolean registerBeanDefinitionIfNotExists(BeanDefinitionRegistry registry, String clusterName) {
		String realBeanName = "elasticsearchTemplate";
		templateBeanNameToClusterNameMap.put(realBeanName, clusterName);
		return BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, realBeanName,
				ElasticsearchTemplateFactoryBean.class);
	}

}