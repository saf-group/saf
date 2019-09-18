package com.future.saf.mq.rocketmq;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import com.future.saf.mq.rocketmq.producer.RocketMQProducerRegistrar;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnableRocketmqProducers.class)
@Import(RocketMQProducerRegistrar.class)
public @interface EnableRocketmqProducer {

	// beanName: clusterName.beanNameSuffix
	public String beanName() default "defaultRocketmqProducer";

	public String instance() default "defaultInstance";

	public String producerGroup() default "defaultRocketmqProducerGroup";

	public boolean autoStart() default false;

}