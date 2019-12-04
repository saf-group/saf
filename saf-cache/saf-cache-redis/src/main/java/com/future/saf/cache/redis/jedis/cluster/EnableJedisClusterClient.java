package com.future.saf.cache.redis.jedis.cluster;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableJedisClusterClients.class)
@Import(JedisClusterClientRegistrar.class)
public @interface EnableJedisClusterClient {

	public String beanName() default "defaultJedisCluster";

	public String instance() default "defaultInstance";

}