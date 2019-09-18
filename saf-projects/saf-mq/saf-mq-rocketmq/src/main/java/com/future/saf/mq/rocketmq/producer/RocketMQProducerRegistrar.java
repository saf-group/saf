package com.future.saf.mq.rocketmq.producer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import com.future.saf.core.util.BeanRegistrationUtil;
import com.future.saf.mq.exception.MQBeanInitException;
import com.future.saf.mq.rocketmq.EnableRocketmqProducer;
import com.future.saf.mq.rocketmq.EnableRocketmqProducers;

public class RocketMQProducerRegistrar implements ImportBeanDefinitionRegistrar {

	static Map<String, String> producerGroupMap = new ConcurrentHashMap<String, String>();

	static Map<String, Boolean> autoStartSwitchMap = new ConcurrentHashMap<String, Boolean>();

	static Map<String, String> instanceMap = new ConcurrentHashMap<String, String>();

	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		boolean registResult = false;

		// 处理EnableMQProducers注解
		{
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(metadata.getAnnotationAttributes(EnableRocketmqProducers.class.getName()));
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

		// 处理EnableMQProducer注解
		{
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(metadata.getAnnotationAttributes(EnableRocketmqProducer.class.getName()));
			if (attributes != null) {
				register(registry, attributes);
				registResult = true;
			}
		}

		if (!registResult)
			throw new MQBeanInitException(
					"Both @EnableRocketmqMQProducer and @EnableRocketmqMQProducers were not found. check your mq annotation.");
	}

	protected void register(BeanDefinitionRegistry registry, AnnotationAttributes attributes) {

		try {
			String beanName = attributes.getString("beanName");
			String instance = attributes.getString("instance");

			Assert.isTrue(StringUtils.isNotEmpty(beanName), "beanName must be specified!");
			Assert.isTrue(StringUtils.isNotEmpty(instance), "instance must be specified!");

			instanceMap.put(beanName, instance);

			String producerGroup = attributes.getString("producerGroup");
			boolean autoStart = attributes.getBoolean("autoStart");

			producerGroupMap.put(beanName, producerGroup);
			autoStartSwitchMap.put(beanName, autoStart);

			// 不同的mq客户端实现方式不一样。
			boolean registerResult = registerBeanDefinitionIfNotExists(registry, beanName);

			Assert.isTrue(registerResult,
					"@EnableRocketmqMQProducer or @EnableRocketmqMQProducers regist failed. check your mq annotation.");
		} catch (Exception e) {
			throw new MQBeanInitException(e.getMessage(), e);
		}
	}

	protected boolean registerBeanDefinitionIfNotExists(BeanDefinitionRegistry registry, String beanName) {
		return BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, beanName,
				RocketMQProducerFactoryBean.class);
	}

}
