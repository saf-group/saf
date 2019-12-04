package com.future.saf.db.druid.core;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.future.saf.core.util.BeanRegistrationUtil;
import com.future.saf.db.druid.EnableDBDruid;
import com.future.saf.db.druid.EnableDBDruids;
import com.future.saf.db.druid.exception.DBBeanInitException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.Assert;
import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DBDruidRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

	static Map<String, String> instanceMap = new ConcurrentHashMap<String, String>();

	private ResourceLoader resourceLoader;

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		boolean processed = false;

		{
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(importingClassMetadata.getAnnotationAttributes(EnableDBDruid.class.getName()));
			if (attributes != null) {
				register(registry, attributes);
				processed = true;
			}
		}

		{
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(importingClassMetadata.getAnnotationAttributes(EnableDBDruids.class.getName()));
			if (attributes != null) {
				AnnotationAttributes[] annotationArray = attributes.getAnnotationArray("value");
				for (AnnotationAttributes oneAttributes : annotationArray) {
					register(registry, oneAttributes);
					processed = true;
				}
			}
		}
		if (!processed)
			throw new DBBeanInitException("no @EnableDBDruid or @EnableDBDruids found! please check code!");
	}

	private void register(BeanDefinitionRegistry registry, AnnotationAttributes oneAttributes) {

		String beanName = oneAttributes.getString("beanName");
		String instance = oneAttributes.getString("instance");

		Assert.isTrue(StringUtils.isNotEmpty(beanName), "beanName must be specified!");
		Assert.isTrue(StringUtils.isNotEmpty(instance), "instance must be specified!");

		instanceMap.put(beanName, instance);
		instanceMap.put(beanName + "SqlSessionFactory", instance);
		instanceMap.put(beanName + DataSource.class.getSimpleName(), instance);
		instanceMap.put(beanName + DataSourceTransactionManager.class.getSimpleName(), instance);

		String[] mapperPackages = oneAttributes.getStringArray("mapperPackages");
		if (ArrayUtils.isNotEmpty(mapperPackages)) {
			scanMappers(registry, beanName, mapperPackages);
		}

		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				beanName + DBDruidBeanPostProcessor.class.getSimpleName(), DBDruidBeanPostProcessor.class);
		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				beanName + DataSource.class.getSimpleName(), DruidDataSource.class);
		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				beanName + SqlSessionFactory.class.getSimpleName(), SqlSessionFactoryBean.class);
		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				beanName + DataSourceTransactionManager.class.getSimpleName(), DataSourceTransactionManager.class);
		BeanRegistrationUtil.registerBeanDefinitionIfBeanNameNotExists(registry,
				beanName + StatFilter.class.getSimpleName(), StatFilter.class);

	}

	private void scanMappers(BeanDefinitionRegistry registry, String namespace, String[] mapperPackages) {

		ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);

		if (resourceLoader != null) {
			scanner.setResourceLoader(resourceLoader);
		}

		Class<? extends Annotation> annotationClass = Annotation.class;
		if (!Annotation.class.equals(annotationClass)) {
			scanner.setAnnotationClass(annotationClass);
		}

		Class<?> markerInterface = Class.class;
		if (!Class.class.equals(markerInterface)) {
			scanner.setMarkerInterface(markerInterface);
		}

		Class<? extends BeanNameGenerator> generatorClass = BeanNameGenerator.class;
		if (!BeanNameGenerator.class.equals(generatorClass)) {
			scanner.setBeanNameGenerator(BeanUtils.instantiateClass(generatorClass));
		}

		@SuppressWarnings("rawtypes")
		Class<? extends MapperFactoryBean> mapperFactoryBeanClass = MapperFactoryBean.class;
		if (!MapperFactoryBean.class.equals(mapperFactoryBeanClass)) {
			scanner.setMapperFactoryBean(BeanUtils.instantiateClass(mapperFactoryBeanClass));
		}

		scanner.setSqlSessionTemplateBeanName("");
		scanner.setSqlSessionFactoryBeanName(namespace + SqlSessionFactory.class.getSimpleName());

		List<String> basePackages = new ArrayList<String>();
		for (String pkg : mapperPackages) {
			if (StringUtils.isNotEmpty(pkg)) {
				basePackages.add(pkg);
			}
		}
		scanner.registerFilters();

		scanner.doScan(basePackages.toArray(new String[0]));
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

}