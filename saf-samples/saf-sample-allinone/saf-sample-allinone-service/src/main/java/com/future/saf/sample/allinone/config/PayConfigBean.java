package com.future.saf.sample.allinone.config;

import org.springframework.beans.factory.annotation.Value;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PayConfigBean {

	@Value("${biz.pay.payUrl}")
	private String payUrl;

}
