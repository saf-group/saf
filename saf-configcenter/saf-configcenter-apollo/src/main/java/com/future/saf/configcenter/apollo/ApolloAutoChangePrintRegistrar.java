package com.future.saf.configcenter.apollo;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.future.saf.core.util.BeanRegistrationUtil;

public final class ApolloAutoChangePrintRegistrar implements ImportBeanDefinitionRegistrar {

	static Set<String> namespaceSet = new HashSet<String>();

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		AnnotationAttributes enableApolloConfigAttributes = AnnotationAttributes
				.fromMap(importingClassMetadata.getAnnotationAttributes(EnableApolloConfig.class.getName()));

		if (enableApolloConfigAttributes == null) {
			return;
		}

		String[] namespaces = enableApolloConfigAttributes.getStringArray("value");
		if (namespaces != null) {
			for (String ns : namespaces) {
				namespaceSet.add(ns);
			}
		}

		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				ApolloConfigAutoChangePrintProcessor.class.getSimpleName(), ApolloConfigAutoChangePrintProcessor.class);
	}

}
