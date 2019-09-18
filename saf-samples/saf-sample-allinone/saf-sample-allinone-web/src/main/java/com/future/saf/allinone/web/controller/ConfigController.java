package com.future.saf.allinone.web.controller;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.future.saf.allinone.web.service.ConfigService;
import com.future.saf.core.web.WebResult;
import com.future.saf.sample.allinone.remote.dto.PayConfigDTO;
import com.future.saf.sample.allinone.remote.dto.SMSConfigDTO;
import com.future.saf.sample.allinone.remote.dto.SpideConfigDTO;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/config")
public class ConfigController {

	@Autowired
	private ConfigService configService;

	@RequestMapping("/getPayConfig")
	public WebResult<PayConfigDTO> getPayConfig() {
		if (RandomUtils.nextBoolean()) {
			throw new IllegalArgumentException("lalala");
		}
		try {
			TimeUnit.MILLISECONDS.sleep(RandomUtils.nextInt(40, 50));
		} catch (InterruptedException ignored) {
		}
		return new WebResult<>(WebResult.CODE_SUCCESS, "getPayConfig success.", configService.getPayConfig());
	}

	@RequestMapping("/getSMSConfig")
	public WebResult<SMSConfigDTO> getSMSConfig() {
		return new WebResult<>(WebResult.CODE_SUCCESS, "getSMSConfig success.", configService.getSMSConfig());
	}

	@RequestMapping("/getSpideConfig")
	public WebResult<SpideConfigDTO> getSpideConfig() {
		return new WebResult<>(WebResult.CODE_SUCCESS, "getSpideConfig success.", configService.getSpideConfig());
	}
}
