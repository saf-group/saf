package com.future.saf.sample.allinone.manager;

import com.alibaba.fastjson.TypeReference;
import com.future.saf.cache.redis.jedis.cluster.JedisClusterClient;
import com.future.saf.sample.allinone.constant.KVKeyConstant;
import com.future.saf.sample.allinone.mapper.malldb.ShopMapper;
import com.future.saf.sample.allinone.model.ShopModel;
import com.future.saf.sample.allinone.remote.dto.ShopDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import javax.annotation.Resource;

@Component
public class ShopManager {

	@Resource(name = "mall")
	private JedisClusterClient mallJedisClusterClient;

	@Autowired
	private ShopMapper shopMapper;

	public ShopModel findById(Long shopId) {

		if (shopId == null) {
			return null;
		}

		String key = KVKeyConstant.getShopModelKey(shopId);
		ShopModel shopModel = mallJedisClusterClient.getCache(key, new TypeReference<ShopModel>() {
		});
		if (shopModel == null) {
			shopModel = shopMapper.findById(shopId);
			if (shopModel != null) {
				// timeout默认1小时
				mallJedisClusterClient.setCache(key, shopModel);
			}
		}
		return shopModel;
	}

	public List<ShopModel> findShopListByOwnerId(Long userId) {

		if (userId == null) {
			return null;
		}

		String key = String.format(KVKeyConstant.KEY_USER_SHOPLIST, userId);
		List<ShopModel> shopModelList = mallJedisClusterClient.getCache(key, new TypeReference<List<ShopModel>>() {
		});

		List<ShopDTO> shopDTOList = null;
		if (shopModelList == null) {
			shopModelList = shopMapper.queryShopModelListByOwnerId(userId);

			if (!CollectionUtils.isEmpty(shopModelList)) {
				mallJedisClusterClient.setCache(key, shopDTOList);
			}
		}

		return shopModelList;
	}
}
