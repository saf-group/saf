package com.future.saf.mq.rocketmq.consumer;

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
import com.future.saf.mq.rocketmq.EnableRocketmqConsumer;
import com.future.saf.mq.rocketmq.EnableRocketmqConsumers;

public class RocketMQConsumerRegistrar implements ImportBeanDefinitionRegistrar {

	static Map<String, String> consumerGroupMap = new ConcurrentHashMap<String, String>();

	static Map<String, String> instanceMap = new ConcurrentHashMap<String, String>();

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		boolean registResult = false;

		// 处理EnableMQConsumers注解
		{
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(metadata.getAnnotationAttributes(EnableRocketmqConsumers.class.getName()));
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

		// 处理EnableMQConsumer注解
		{
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(metadata.getAnnotationAttributes(EnableRocketmqConsumer.class.getName()));
			if (attributes != null) {
				register(registry, attributes);
				registResult = true;
			}
		}

		if (!registResult)
			throw new MQBeanInitException(
					"Both @EnableRocketmqMQConsumer and @EnableRocketmqMQConsumers were not found. check your mq annotation.");
	}

	protected void register(BeanDefinitionRegistry registry, AnnotationAttributes attributes) {

		try {
			String beanName = attributes.getString("beanName");
			String instance = attributes.getString("instance");

			Assert.isTrue(StringUtils.isNotEmpty(beanName), "beanName must be specified!");
			Assert.isTrue(StringUtils.isNotEmpty(instance), "instance must be specified!");

			instanceMap.put(beanName, instance);

			String consumerGroup = attributes.getString("consumerGroup");
			consumerGroupMap.put(beanName, consumerGroup);

			// 不同的mq客户端实现方式不一样。
			boolean registerResult = registerBeanDefinitionIfNotExists(registry, beanName);

			Assert.isTrue(registerResult,
					"@EnableRocketmqMQConsumer or @EnableRocketmqMQConsumers regist failed. check your mq annotation.");
		} catch (Exception e) {
			throw new MQBeanInitException(e.getMessage(), e);
		}
	}

	protected boolean registerBeanDefinitionIfNotExists(BeanDefinitionRegistry registry, String beanName) {
		return BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, beanName,
				RocketMQConsumerFactoryBean.class);
	}

}
