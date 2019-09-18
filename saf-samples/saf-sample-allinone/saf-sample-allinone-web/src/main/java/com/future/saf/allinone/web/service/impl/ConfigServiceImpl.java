package com.future.saf.allinone.web.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.future.saf.allinone.web.config.PayConfigBean;
import com.future.saf.allinone.web.config.SMSConfigBean;
import com.future.saf.allinone.web.config.SpideConfigBean;
import com.future.saf.allinone.web.service.ConfigService;
import com.future.saf.sample.allinone.remote.dto.PayConfigDTO;
import com.future.saf.sample.allinone.remote.dto.SMSConfigDTO;
import com.future.saf.sample.allinone.remote.dto.SpideConfigDTO;

@Service
public class ConfigServiceImpl implements ConfigService {

	@Autowired
	private PayConfigBean payConfigBean;

	@Autowired
	private SMSConfigBean smsConfigBean;

	@Autowired
	private SpideConfigBean spideConfigBean;

	@Override
	public PayConfigDTO getPayConfig() {
		PayConfigDTO dto = new PayConfigDTO();
		BeanUtils.copyProperties(payConfigBean, dto);
		return dto;
	}

	@Override
	public SMSConfigDTO getSMSConfig() {
		SMSConfigDTO dto = new SMSConfigDTO();
		BeanUtils.copyProperties(smsConfigBean, dto);
		return dto;
	}

	@Override
	public SpideConfigDTO getSpideConfig() {
		SpideConfigDTO dto = new SpideConfigDTO();
		BeanUtils.copyProperties(spideConfigBean, dto);
		return dto;
	}

}
