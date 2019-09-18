package com.future.saf.cache.redis.jedis.cluster;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

@ConditionalOnClass({ JedisCluster.class, JedisPoolConfig.class })
public class JedisClusterAutoConfiguration {

	@Bean
	public JedisClusterClientAspect jedisClusterClientAspect() {
		return new JedisClusterClientAspect();
	}

}
