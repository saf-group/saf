package com.future.saf.configcenter.nacos;

//import java.util.Set;

import com.alibaba.fastjson.JSONObject;
//import com.alibaba.nacos.api.NacosFactory;
//import com.alibaba.nacos.api.config.ConfigService;
import com.future.saf.configcenter.basic.ConfigCenterBasicService;

public class NacosConfigService implements ConfigCenterBasicService {

	@Override
	public JSONObject getProperties(String namespace) {
		// TODO
		// 这里需要斟酌，nacos和apollo数据结构的模型差异极大
		// ConfigService configService =
		// NacosFactory.createConfigService(properties);
		// Config apolloConfig = ConfigService.getConfig(namespace);
		// Set<String> propertyNames = apolloConfig.getPropertyNames();
		// JSONObject rtobj = new JSONObject();
		// for (String key : propertyNames) {
		// rtobj.put(key, apolloConfig.getProperty(key, null));
		// }
		// return rtobj;
		return null;
	}

}
