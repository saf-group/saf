package com.future.saf.allinone.web.service;

import com.future.saf.sample.allinone.remote.dto.PayConfigDTO;
import com.future.saf.sample.allinone.remote.dto.SMSConfigDTO;
import com.future.saf.sample.allinone.remote.dto.SpideConfigDTO;

public interface ConfigService {

	public PayConfigDTO getPayConfig();

	public SMSConfigDTO getSMSConfig();

	public SpideConfigDTO getSpideConfig();

}
