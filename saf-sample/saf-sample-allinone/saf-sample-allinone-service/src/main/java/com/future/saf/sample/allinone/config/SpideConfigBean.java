package com.future.saf.sample.allinone.config;

import org.springframework.beans.factory.annotation.Value;

import lombok.*;

@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SpideConfigBean {

	@Value("${biz.spide.toutiaoSpideUrl}")
	private String toutiaoSpideUrl;

	@Value("${biz.spide.qutoutiaoSpideUrl}")
	private String qutoutiaoSpideUrl;

}
