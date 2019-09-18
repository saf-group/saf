package com.future.saf.mq.rocketmq.consumer;

import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

public abstract class RocketMQBaseConsumer {

	private RocketMQConsumerConfig consumerConfig;

	public RocketMQBaseConsumer(RocketMQConsumerConfig consumerConfig) throws Exception {
		this.consumerConfig = consumerConfig;
	}

	protected RocketMQConsumerConfig getRocketConsumerConfig() {
		return consumerConfig;
	}

	public abstract void start() throws Exception;

	public abstract void registerMessageListener(MessageListenerConcurrently messageListener);

	public abstract void subscribe(String topic, String subExpression) throws Exception;

	public abstract void subscribe(String topic) throws Exception;

	public abstract void setMessageModel(MessageModel msgModel);

	public abstract void logConfig();

	public void setConsumerGroup(String consumerGroup) {
		consumerConfig.setConsumerGroup(consumerGroup);
	}

}
