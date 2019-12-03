package com.future.saf.allinone.web.listener;

import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.future.saf.sample.allinone.remote.api.ShopRPC;
import com.future.saf.sample.allinone.remote.api.UserRPC;
import com.future.saf.sample.allinone.remote.dto.ShopDTO;
import com.future.saf.sample.allinone.remote.dto.UserDTO;
import com.weibo.api.motan.config.springsupport.annotation.MotanReferer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AllinoneListener implements ApplicationListener<ContextRefreshedEvent> {

	@MotanReferer(basicReferer = "userBasicRefererConfigBean")
	private UserRPC userrpc;

	@MotanReferer(basicReferer = "mallBasicRefererConfigBean")
	private ShopRPC shoprpc;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		if (contextRefreshedEvent.getApplicationContext().getParent() == null) {// 保证只执行一次
			try {
				startTestSaf();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	public void startTestSaf() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {
					try {
						// (1).userrpc
						log.info("------(1).userrpc------");
						UserDTO userDTO = userrpc.getUser(1L);
						log.info("userDTO:" + userDTO);

						// (2).shoprpc
						log.info("------(1).shoprpc------");
						ShopDTO shopDTO = shoprpc.getShop(1L);
						log.info("shopDTO:" + shopDTO);

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

}
