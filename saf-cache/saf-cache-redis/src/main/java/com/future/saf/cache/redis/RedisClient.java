package com.future.saf.cache.redis;

import com.alibaba.fastjson.TypeReference;

public interface RedisClient {

	public void warmUp() throws Exception;

	public boolean setCache(String key, Object obj);

	public boolean setCache(String key, Object obj, int timeout);

	public <T> T getCache(String key, TypeReference<T> typeReference);

}
