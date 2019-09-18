package com.future.saf.allinone.web;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.future.saf.allinone.web.service.ConfigService;
import com.future.saf.configcenter.apollo.EnableApolloConfigAutoChangePrint;
import com.future.saf.rpc.motan.EnableMotan;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by hepengyuan on 2018/09/27.
 */
@SpringBootApplication
@EnableApolloConfig(value = { "application", "saf.rpc.referer.user", "saf.rpc.referer.mall", "saf.biz.sms",
		"saf.biz.pay", "saf.biz.spide" })
@EnableApolloConfigAutoChangePrint
@EnableMotan(beanNamePrefix = "user", instance = "user")
@EnableMotan(beanNamePrefix = "mall", instance = "mall")
@Slf4j
public class SafSampleAllinoneWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(SafSampleAllinoneWebApplication.class, args);
	}

	@Component
	class Runner implements ApplicationRunner {

		@Autowired
		private ConfigService configService;

		@Override
		public void run(ApplicationArguments args) throws Exception {

			new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
						try {
							System.out.println(configService.getPayConfig());
						} catch (Exception e) {
							log.error(e.getMessage(), e);
						} finally {
							try {
								TimeUnit.SECONDS.sleep(300);
							} catch (InterruptedException e1) {
								log.error(e1.getMessage(), e1);
							}
						}
					}
				}
			}).start();

		}

	}

}
