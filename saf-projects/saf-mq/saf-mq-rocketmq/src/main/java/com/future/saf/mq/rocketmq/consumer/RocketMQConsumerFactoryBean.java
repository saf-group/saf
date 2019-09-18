package com.future.saf.mq.rocketmq.consumer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;

import com.future.saf.core.CustomizedConfigurationPropertiesBinder;
import com.future.saf.mq.rocketmq.RocketMQConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RocketMQConsumerFactoryBean implements FactoryBean<RocketMQBaseConsumer>, BeanNameAware {

	private String beanName;

	@Autowired
	protected CustomizedConfigurationPropertiesBinder binder;

	@Override
	public RocketMQBaseConsumer getObject() throws Exception {

		RocketMQConsumerConfig consumerConfig = new RocketMQConsumerConfig();
		Bindable<?> target = Bindable.of(RocketMQConsumerConfig.class).withExistingValue(consumerConfig);

		String instance = RocketMQConsumerRegistrar.instanceMap.get(beanName);

		binder.bind(RocketMQConstant.ROCKETMQ_NAMESRV_CONFIG_PREFIX + "." + instance, target);
		binder.bind(RocketMQConstant.ROCKETMQ_CONSUMER_CONFIG_PREFIX + "." + instance, target);

		String consumerGroup = RocketMQConsumerRegistrar.consumerGroupMap.get(beanName);
		if (StringUtils.isBlank(consumerGroup)) {
			consumerConfig.setConsumerGroup(null);
		} else {
			consumerConfig.setConsumerGroup(RocketMQConsumerRegistrar.consumerGroupMap.get(beanName));
		}
		log.info("init consumerConfig:" + consumerConfig.getConsumerGroup());

		RocketMQSimpleConsumer mqConsumer = new RocketMQSimpleConsumer(consumerConfig);
		mqConsumer.logConfig();

		return mqConsumer;
	}

	@Override
	public Class<?> getObjectType() {
		return RocketMQConsumerFactoryBean.class;
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

}
