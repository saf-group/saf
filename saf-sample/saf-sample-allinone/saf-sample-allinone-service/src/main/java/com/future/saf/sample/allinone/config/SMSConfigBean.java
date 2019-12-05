package com.future.saf.sample.allinone.config;

import org.springframework.beans.factory.annotation.Value;

import lombok.*;

@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SMSConfigBean {

	@Value("${biz.sms.aliyunSMSUrl}")
	private String aliyunSMSUrl;

	@Value("${biz.sms.mobileSMSUrl}")
	private String mobileSMSUrl;

	@Value("${biz.sms.unicomSMSUrl}")
	private String unicomSMSUrl;

}
