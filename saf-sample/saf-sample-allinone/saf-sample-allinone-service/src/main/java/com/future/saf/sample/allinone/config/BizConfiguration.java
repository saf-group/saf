package com.future.saf.sample.allinone.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class BizConfiguration implements InitializingBean {

	@Bean
	public PayConfigBean getPayConfigBean() {
		return new PayConfigBean();
	}

	@Bean
	public SpideConfigBean getSpideConfigBean() {
		return new SpideConfigBean();
	}

	@Bean
	public SMSConfigBean getSMSConfigBean() {
		return new SMSConfigBean();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info(JSON.toJSONString(getPayConfigBean(), SerializerFeature.PrettyFormat,
				SerializerFeature.WriteClassName));
		log.info(JSON.toJSONString(getSpideConfigBean(), SerializerFeature.PrettyFormat,
				SerializerFeature.WriteClassName));
		log.info(JSON.toJSONString(getSMSConfigBean(), SerializerFeature.PrettyFormat,
				SerializerFeature.WriteClassName));
	}

}
