package com.future.saf.sample.allinone.listener;

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
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.future.saf.mq.rocketmq.consumer.RocketMQBaseConsumer;
import com.future.saf.mq.rocketmq.producer.RocketMQBaseProducer;
import com.future.saf.sample.allinone.config.PayConfigBean;
import com.future.saf.sample.allinone.config.SMSConfigBean;
import com.future.saf.sample.allinone.config.SpideConfigBean;
import com.future.saf.sample.allinone.localcache.ShopModelGuavaLocalCache;
import com.future.saf.sample.allinone.localcache.ShopModelSafWrapperLocalCache;
import com.future.saf.sample.allinone.mapper.malldb.ShopMapper;
import com.future.saf.sample.allinone.mapper.userdb.UserMapper;
import com.future.saf.sample.allinone.model.ShopModel;
import com.future.saf.sample.allinone.model.UserModel;
import com.future.saf.sample.allinone.remote.dto.ShopDetailDTO;
import com.future.saf.sample.allinone.remote.dto.UserDetailDTO;
import com.future.saf.sample.allinone.service.ShopService;
import com.future.saf.sample.allinone.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AllinoneListener implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private ShopService shopService;

	@Autowired
	private UserService userService;

	@Autowired
	private PayConfigBean payConfigBean;

	@Autowired
	private SMSConfigBean smsConfigBean;

	@Autowired
	private SpideConfigBean spideConfigBean;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private ShopMapper shopMapper;

	@Autowired
	private ShopModelGuavaLocalCache shopModelLocalCache;

	@Autowired
	private ShopModelSafWrapperLocalCache shopModelsafWrapperLocalCache;

	@Resource(name = "default-producer")
	private RocketMQBaseProducer producer;

	@Resource(name = "default-consumer")
	private RocketMQBaseConsumer consumer;

	private static final String TOPIC = "topic";

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		if (contextRefreshedEvent.getApplicationContext().getParent() == null) {// 保证只执行一次
			try {
				startConsumer();
				startProducer();
				startTestRocketmq();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	public void startTestRocketmq() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {
					try {
						// (1).rocketmq
						SendResult sendResult1 = producer.send(TOPIC, "producer1-haha:" + System.currentTimeMillis());
						log.info("------(1).rocketmq------");
						log.info("sendResult1:" + sendResult1);

						// (2).redisCluster
						log.info("------(2).redisCluster------");
						ShopDetailDTO shopDTO = shopService.getShopDetail(1L);
						log.info("shopDetail:" + shopDTO.toString());

						UserDetailDTO userDTO = userService.getUserDetail(1L);
						log.info("userDetail:" + userDTO.toString());

						// (3).apollo config bean
						log.info("------(3).apollo config bean------");
						log.info("payConfigBean:" + payConfigBean.toString());
						log.info("smsConfigBean:" + smsConfigBean.toString());
						log.info("spideConfigBean:" + spideConfigBean.toString());

						// (4).jvm guava cache
						log.info("------(4).jvm guava cache------");
						ShopModel shopModel = shopModelLocalCache.get(1L);
						log.info(shopModel.toString());

						List<Long> shopIdList = new ArrayList<Long>();
						shopIdList.add(2L);
						shopIdList.add(3L);
						shopIdList.add(4L);
						shopIdList.add(1L);

						List<ShopModel> shopModelList = shopModelsafWrapperLocalCache.getAll(shopIdList);
						log.info(JSONObject.toJSONString(shopModelList));
						log.info(JSONObject.toJSONString("hpy:" + shopModelsafWrapperLocalCache.get(243242352L)));

						// (5).mysql mapper
						log.info("------(5).mysql mapper------");
						UserModel userModel = userMapper.findById(1L);
						shopModel = shopMapper.findById(1L);

						log.info("userModel from mysql:" + userModel);
						log.info("shopModel from mysql:" + shopModel);

						// break;
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						try {
							TimeUnit.SECONDS.sleep(1);
						} catch (InterruptedException e1) {
							log.error(e1.getMessage(), e1);
						}
					} finally {
						try {
							TimeUnit.SECONDS.sleep(1);
						} catch (Exception e) {
							log.error(e.getMessage(), e);
						}
					}
				}
			}
		}).start();
	}

	private void startConsumer() throws Exception {
		consumer.subscribe(TOPIC);
		consumer.setConsumerGroup("consumerGroup");
		consumer.registerMessageListener(new MessageListenerConcurrently() {

			@Override
			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

				System.out.printf("Consuemr1 %s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
				for (MessageExt ext : msgs) {
					try {
						System.out.printf("%s Receive New Message: %s %n", Thread.currentThread().getName(),
								new String(ext.getBody(), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
		});
		consumer.start();
	}

	private void startProducer() throws Exception {
		producer.setProducerGroup("producerGroup");
		producer.start();
	}

}
