package com.future.saf.cache.redis.jedis.cluster;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import com.future.saf.cache.redis.RedisConstant;
import com.future.saf.core.CustomizedConfigurationPropertiesBinder;

import redis.clients.jedis.JedisPoolConfig;

public class JedisClusterClientFactoryBean implements FactoryBean<JedisClusterClient>, EnvironmentAware, BeanNameAware {

	private Environment environment;

	private String beanName;

	@Autowired
	protected CustomizedConfigurationPropertiesBinder binder;

	@Override
	public JedisClusterClient getObject() throws NoSuchFieldException {
		String instanceName = JedisClusterClientRegistrar.instanceMap.get(beanName);

		String addressKey = RedisConstant.REDIS_CLUSTER_CONFIG_PREFIX + "." + instanceName + ".address";
		String address = environment.getProperty(addressKey);
		Assert.isTrue(StringUtils.isNotEmpty(address),
				String.format("%s=%s must be not configed! ", addressKey, address));

		JedisPoolConfig jedisPoolConfig = createJedisPoolConfig();
		jedisPoolConfig.setTestOnReturn(false);
		Bindable<?> target = Bindable.of(JedisPoolConfig.class).withExistingValue(jedisPoolConfig);
		binder.bind(RedisConstant.REDIS_CLUSTER_CONFIG_PREFIX + "." + instanceName + ".pool", target);

		JedisClusterClient jedisClusterClient = new JedisClusterClient(jedisPoolConfig, beanName, address);

		return jedisClusterClient;
	}

	private JedisPoolConfig createJedisPoolConfig() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(300);
		config.setMaxIdle(10);
		config.setMinIdle(5);
		config.setMaxWaitMillis(6000);
		config.setTestOnBorrow(false);
		config.setTestOnReturn(false);
		config.setTestWhileIdle(true);
		config.setTestOnCreate(false);
		return config;
	}

	@Override
	public Class<?> getObjectType() {
		return JedisClusterClient.class;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}
}
