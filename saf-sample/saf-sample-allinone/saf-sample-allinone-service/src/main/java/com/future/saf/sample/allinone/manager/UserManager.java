package com.future.saf.sample.allinone.manager;

import com.alibaba.fastjson.TypeReference;
import com.future.saf.cache.redis.jedis.cluster.JedisClusterClient;
import com.future.saf.sample.allinone.constant.KVKeyConstant;
import com.future.saf.sample.allinone.mapper.userdb.UserMapper;
import com.future.saf.sample.allinone.model.UserModel;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserManager {

	@Autowired
	private UserMapper userMapper;

	@Resource(name = "user")
	private JedisClusterClient userJedisClusterClient;

	public UserModel findById(Long userId) {
		if (userId == null) {
			return null;
		}

		String key = String.format(KVKeyConstant.KEY_USER, userId);
		UserModel userModel = userJedisClusterClient.getCache(key, new TypeReference<UserModel>() {
		});

		if (userModel == null) {
			userModel = userMapper.findById(userId);
			if (userModel != null) {
				userJedisClusterClient.setCache(key, userModel);
			}
		}
		return userModel;
	}
}
