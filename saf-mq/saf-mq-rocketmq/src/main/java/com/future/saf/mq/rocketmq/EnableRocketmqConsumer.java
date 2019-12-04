package com.future.saf.mq.rocketmq;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import com.future.saf.mq.rocketmq.consumer.RocketMQConsumerRegistrar;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableRocketmqConsumers.class)
@Import(RocketMQConsumerRegistrar.class)
public @interface EnableRocketmqConsumer {

	public String beanName() default "defaultRocketmqConsumer";

	public String instance() default "defaultInstance";

	public String consumerGroup() default "defaultRocketmqConsumerGroup";

}