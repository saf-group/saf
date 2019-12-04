package com.future.saf.configcenter.apollo;

import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.future.saf.configcenter.basic.ConfigCenterBasicService;

public class ApolloConfigService implements ConfigCenterBasicService {

	@Override
	public JSONObject getProperties(String namespace) {
		Config apolloConfig = ConfigService.getConfig(namespace);
		Set<String> propertyNames = apolloConfig.getPropertyNames();
		JSONObject rtobj = new JSONObject();
		for (String key : propertyNames) {
			rtobj.put(key, apolloConfig.getProperty(key, null));
		}
		return rtobj;
	}

}
