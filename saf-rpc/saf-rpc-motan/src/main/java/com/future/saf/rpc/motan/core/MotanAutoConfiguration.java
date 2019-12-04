package com.future.saf.rpc.motan.core;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;

import com.future.saf.logging.basic.Loggers;
import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.config.springsupport.ProtocolConfigBean;
import com.weibo.api.motan.registry.zookeeper.ZookeeperRegistryFactory;
import com.weibo.api.motan.transport.netty.NettyChannelFactory;
import com.weibo.api.motan.util.MotanSwitcherUtil;

@ConditionalOnClass({ MotanSwitcherUtil.class, ZookeeperRegistryFactory.class, ProtocolConfigBean.class,
		NettyChannelFactory.class })
public class MotanAutoConfiguration {

	private static final Logger logger = Loggers.getFrameworkLogger();

	@Bean
	public ApplicationListener<ApplicationEvent> motanSwitcherListener() {
		// 官方优雅关闭示例
		return event -> {
			if (event instanceof ApplicationReadyEvent) {
				// 只有打开开关，rpc才处于avaliable状态
				MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
				logger.info("motan service has started!");

				Runtime.getRuntime().addShutdownHook(new Thread(() -> {
					try {
						MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, false);
						TimeUnit.SECONDS.sleep(1);
						logger.info("motan service has shutdown gracefully!");
					} catch (Exception e) {
						logger.error("error occurred during motan service shutdown! pls check! ", e);
					}
				}));
				logger.info("motan service shutdown hook added!");
			} else if (event instanceof ContextClosedEvent) {
				logger.info("ContextClosedEvent triggered, will start shutdown motan service...");
			}
		};
	}

}
