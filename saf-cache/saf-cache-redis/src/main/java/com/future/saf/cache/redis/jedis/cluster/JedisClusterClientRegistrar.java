package com.future.saf.cache.redis.jedis.cluster;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import com.future.saf.cache.redis.exception.RedisClientBeanInitException;
import com.future.saf.core.util.BeanRegistrationUtil;

public class JedisClusterClientRegistrar implements ImportBeanDefinitionRegistrar {

	static Map<String, String> instanceMap = new ConcurrentHashMap<String, String>();

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		boolean redisClientRegistResult = false;

		// 处理EnableRedisClusterClients注解
		{
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(metadata.getAnnotationAttributes(EnableJedisClusterClients.class.getName()));
			if (attributes != null) {
				AnnotationAttributes[] annotationArray = attributes.getAnnotationArray("value");
				if (annotationArray != null && annotationArray.length > 0) {
					for (AnnotationAttributes e : annotationArray) {
						register(registry, e);
						redisClientRegistResult = true;
					}
				}
			}
		}

		// 处理EnableRedisClusterClient注解
		{
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(metadata.getAnnotationAttributes(EnableJedisClusterClient.class.getName()));
			if (attributes != null) {
				register(registry, attributes);
				redisClientRegistResult = true;
			}
		}

		if (!redisClientRegistResult)
			throw new RedisClientBeanInitException(
					"Both @EnableJedisClusterClient and @EnableJedisClusterClients were not found. check your redis annotation.");
	}

	protected void register(BeanDefinitionRegistry registry, AnnotationAttributes attributes) {

		try {
			String beanName = attributes.getString("beanName");
			String instance = attributes.getString("instance");

			Assert.isTrue(StringUtils.isNotEmpty(beanName), "beanName must be specified!");
			Assert.isTrue(StringUtils.isNotEmpty(instance), "instance must be specified!");

			instanceMap.put(beanName, instance);

			// 不checkClassExist，因为有可能有多个jedisCluster实例
			boolean registerResult = BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, beanName,
					JedisClusterClientFactoryBean.class, false);

			if (!registerResult)
				throw new RedisClientBeanInitException(
						"Both @EnableJedisClusterClient and @EnableJedisClusterClients were not found. check your redis annotation.");
		} catch (Exception e) {
			throw new RedisClientBeanInitException(e.getMessage(), e);
		}
	}

}