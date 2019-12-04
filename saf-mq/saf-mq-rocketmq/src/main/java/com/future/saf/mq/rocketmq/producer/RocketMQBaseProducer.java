package com.future.saf.mq.rocketmq.producer;

import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class RocketMQBaseProducer {

	private RocketMQProducerConfig producerConfig;

	public RocketMQBaseProducer(RocketMQProducerConfig producerConfig) {
		this.producerConfig = producerConfig;
	}

	protected RocketMQProducerConfig getRocketProducerConfig() {
		return producerConfig;
	}

	public abstract void logConfig();

	SendResult send(MQProducer producer, Message message, String producerGroup, String clazzName) {
		try {
			SendResult sendResult = producer.send(message);
			return sendResult;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public void setProducerGroup(String producerGroup) {
		producerConfig.setProducerGroup(producerGroup);
	}

	public abstract void start() throws Exception;

	public abstract SendResult send(String topic, String tags, String message);

	public abstract SendResult send(String topic, String message);

}
