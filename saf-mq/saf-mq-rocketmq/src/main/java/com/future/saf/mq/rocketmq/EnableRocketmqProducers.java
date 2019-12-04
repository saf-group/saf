package com.future.saf.mq.rocketmq;

import org.springframework.context.annotation.Import;
import com.future.saf.mq.rocketmq.producer.RocketMQProducerRegistrar;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(RocketMQProducerRegistrar.class)
public @interface EnableRocketmqProducers {

	EnableRocketmqProducer[] value();

}