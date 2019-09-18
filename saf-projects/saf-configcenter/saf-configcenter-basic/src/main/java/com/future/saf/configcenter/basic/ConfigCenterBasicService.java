package com.future.saf.configcenter.basic;

import com.alibaba.fastjson.JSONObject;

public interface ConfigCenterBasicService {

	/**
	 * @param namespace
	 * @return json format
	 */
	public JSONObject getProperties(String namespace);

}
