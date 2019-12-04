package com.future.saf.mq.rocketmq.consumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;

public class RocketMQConsumerConfig extends DefaultMQPushConsumer {

	public static final String DEFAULT_CONSUMER_GROUP = "consumerDefaultGroup";

	private String subExpression = "*";

	public String getSubExpression() {
		return subExpression;
	}

	public void setSubExpression(String subExpression) {
		this.subExpression = subExpression;
	}

}
