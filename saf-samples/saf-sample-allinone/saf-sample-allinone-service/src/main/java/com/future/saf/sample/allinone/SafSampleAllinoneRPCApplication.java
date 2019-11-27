package com.future.saf.sample.allinone;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSONObject;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.future.saf.cache.redis.jedis.cluster.EnableJedisClusterClient;
import com.future.saf.configcenter.apollo.EnableApolloConfigAutoChangePrint;
import com.future.saf.db.druid.EnableDBDruid;
import com.future.saf.mq.rocketmq.EnableRocketmqConsumer;
import com.future.saf.mq.rocketmq.EnableRocketmqProducer;
import com.future.saf.mq.rocketmq.consumer.RocketMQBaseConsumer;
import com.future.saf.mq.rocketmq.producer.RocketMQBaseProducer;
import com.future.saf.rpc.motan.EnableMotan;
import com.future.saf.sample.allinone.config.PayConfigBean;
import com.future.saf.sample.allinone.config.SMSConfigBean;
import com.future.saf.sample.allinone.config.SpideConfigBean;
import com.future.saf.sample.allinone.localcache.ShopModelGuavaLocalCache;
import com.future.saf.sample.allinone.localcache.ShopModelSafWrapperLocalCache;
import com.future.saf.sample.allinone.model.ShopModel;
import com.future.saf.sample.allinone.remote.dto.ShopDetailDTO;
import com.future.saf.sample.allinone.remote.dto.UserDetailDTO;
import com.future.saf.sample.allinone.service.ShopService;
import com.future.saf.sample.allinone.service.UserService;
import lombok.extern.slf4j.Slf4j;

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
@EnableApolloConfig(value = { "application", "saf.db.user", "saf.db.mall", "saf.redis.cluster.rpc.user",
		"saf.redis.cluster.rpc.mall", "saf.biz.sms", "saf.biz.pay", "saf.biz.spide", "saf.mq.rocketmq.namesrv" })
//系统启动时会打印@EnableApolloConfig中指定的namespace的初始化值；并且如果运行时会打印发生变化的配置。
@EnableApolloConfigAutoChangePrint

public class SafSampleAllinoneRPCApplication {

	public static void main(String[] args) {
		SpringApplication.run(SafSampleAllinoneRPCApplication.class, args);
	}

//	@Component
//	class Runner implements ApplicationRunner {}
}
