package com.future.saf.sample.allinone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.future.saf.cache.redis.jedis.cluster.EnableJedisClusterClient;
import com.future.saf.configcenter.apollo.EnableApolloConfigAutoChangePrint;
import com.future.saf.db.druid.EnableDBDruid;
import com.future.saf.mq.rocketmq.EnableRocketmqConsumer;
import com.future.saf.mq.rocketmq.EnableRocketmqProducer;
import com.future.saf.rpc.motan.EnableMotan;

@SpringBootApplication

//开启两个redis-cluster实例
@EnableJedisClusterClient(beanName = "mall", instance = "mall")
@EnableJedisClusterClient(beanName = "user", instance = "user")

//开启两个datasource实例
@EnableDBDruid(beanName = "mall", instance = "mall", mapperPackages = "com.future.saf.sample.allinone.mapper.malldb")
@EnableDBDruid(beanName = "user", instance = "user", mapperPackages = "com.future.saf.sample.allinone.mapper.userdb")

//开启两个端口
@EnableMotan(beanNamePrefix = "user", instance = "user")
@EnableMotan(beanNamePrefix = "mall", instance = "mall")

//开启rocketmq
@EnableRocketmqProducer(beanName = "default-producer", instance = "rocketmq-c0")
@EnableRocketmqConsumer(beanName = "default-consumer", instance = "rocketmq-c0")

//开启apollo配置中心
@EnableApolloConfig(value = { "application", "saf.actuator", "saf.base.registry", "saf.log.level", "saf.monitor",
		"saf.rocketmq.rocketmq-c0", "demo.db.mall", "demo.db.user", "demo.redis-cluster.user",
		"demo.redis-cluster.mall", "demo.public-config.pay", "demo.public-config.sms", "demo.public-config.spide" })
//系统启动时会打印@EnableApolloConfig中指定的namespace的初始化值；并且如果运行时会打印发生变化的配置。
@EnableApolloConfigAutoChangePrint

public class SafSampleAllinoneRPCApplication {

	public static void main(String[] args) {
		SpringApplication.run(SafSampleAllinoneRPCApplication.class, args);
	}

//	@Component
//	class Runner implements ApplicationRunner {}
}
