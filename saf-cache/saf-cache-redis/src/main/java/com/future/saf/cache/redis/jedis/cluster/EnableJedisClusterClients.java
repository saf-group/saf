package com.future.saf.cache.redis.jedis.cluster;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(JedisClusterClientRegistrar.class)
public @interface EnableJedisClusterClients {
	EnableJedisClusterClient[] value();
}