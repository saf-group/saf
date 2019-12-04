package com.future.saf.mq.rocketmq.producer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;

import com.future.saf.core.CustomizedConfigurationPropertiesBinder;
import com.future.saf.mq.rocketmq.RocketMQConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RocketMQProducerFactoryBean implements FactoryBean<RocketMQBaseProducer>, BeanNameAware {
	private String beanName;

	@Autowired
	protected CustomizedConfigurationPropertiesBinder binder;

	@Override
	public RocketMQBaseProducer getObject() throws Exception {

		RocketMQProducerConfig producerConfig = new RocketMQProducerConfig();
		Bindable<?> target = Bindable.of(RocketMQProducerConfig.class).withExistingValue(producerConfig);

		String instance = RocketMQProducerRegistrar.instanceMap.get(beanName);

		binder.bind(RocketMQConstant.ROCKETMQ_NAMESRV_CONFIG_PREFIX + "." + instance, target);
		binder.bind(RocketMQConstant.ROCKETMQ_PRODUCER_CONFIG_PREFIX + "." + instance, target);

		String producerGroup = RocketMQProducerRegistrar.producerGroupMap.get(beanName);
		if (StringUtils.isBlank(producerGroup)) {
			producerConfig.setProducerGroup(null);
		} else {
			producerConfig.setProducerGroup(RocketMQProducerRegistrar.producerGroupMap.get(beanName));
		}
		log.info("init producerConfig:" + producerConfig.getProducerGroup());

		RocketMQBaseProducer mqProducer = new RocketMQSimpleProducer(producerConfig, beanName);
		mqProducer.logConfig();

		if (RocketMQProducerRegistrar.autoStartSwitchMap.get(beanName)) {
			mqProducer.start();
		}
		return mqProducer;
	}

	@Override
	public Class<?> getObjectType() {
		return RocketMQBaseProducer.class;
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}
}
