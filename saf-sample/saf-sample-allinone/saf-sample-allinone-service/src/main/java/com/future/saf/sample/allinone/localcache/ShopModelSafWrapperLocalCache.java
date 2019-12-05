package com.future.saf.sample.allinone.localcache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.TypeReference;
import com.future.saf.cache.jvm.GuavaLocalCache;
import com.future.saf.cache.redis.jedis.cluster.JedisClusterClient;
import com.future.saf.sample.allinone.constant.KVKeyConstant;
import com.future.saf.sample.allinone.manager.ShopManager;
import com.future.saf.sample.allinone.mapper.malldb.ShopMapper;
import com.future.saf.sample.allinone.model.ShopModel;
import com.google.common.base.Optional;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author hepengyuan
 * @version create time: 2018年10月25日 上午11:04:18
 *
 */
@Slf4j
@Component
@EnableScheduling
public class ShopModelSafWrapperLocalCache {

	@Autowired
	private ShopManager shopManager;

	@Autowired
	private ShopMapper shopMapper;

	@Resource(name = "mall")
	private JedisClusterClient mallJedisClusterClient;

	private GuavaLocalCache<Long, ShopModel> shopModelCache = new GuavaLocalCache<Long, ShopModel>(100, 100, 10) {

		@Override
		protected Optional<ShopModel> loadImpl(Long shopId) {
			ShopModel shopModel = shopManager.findById(shopId);
			if (shopModel == null) {
				return Optional.absent();
			}
			return Optional.of(shopModel);
		}

		@Override
		protected Map<Long, Optional<ShopModel>> loadAllImpl(Iterable<? extends Long> shopIds) {

			Map<Long, Optional<ShopModel>> rtMap = new HashMap<Long, Optional<ShopModel>>();

			// 容错判断
			if (shopIds == null || shopIds.iterator() == null) {
				// 不能返回null.
				return rtMap;
			}

			List<Long> shopIdsNotExistInRedisList = new ArrayList<Long>();
			// 先从redisCluster中取出所有的keys(不要用mget)，没有取到的从DB中取。

			// 先从redis取
			shopIds.forEach(new Consumer<Long>() {

				@Override
				public void accept(Long shopId) {
					ShopModel shopModel = mallJedisClusterClient.getCache(KVKeyConstant.getShopModelKey(shopId),
							new TypeReference<ShopModel>() {
							});
					if (shopModel != null) {
						rtMap.put(shopId, Optional.of(shopModel));
					} else {
						shopIdsNotExistInRedisList.add(shopId);
					}
				}

			});

			// 再从DB取redis中没有取到的.
			if (!CollectionUtils.isEmpty(shopIdsNotExistInRedisList)) {

				List<ShopModel> shopModelList = shopMapper.queryShopModelListByShopIdList(shopIdsNotExistInRedisList);

				if (!CollectionUtils.isEmpty(shopModelList)) {

					shopModelList.forEach(new Consumer<ShopModel>() {
						@Override
						public void accept(ShopModel shopModel) {
							rtMap.put(shopModel.getId(), Optional.of(shopModel));
							mallJedisClusterClient.setCache(KVKeyConstant.getShopModelKey(shopModel.getId()),
									shopModel);
							shopIdsNotExistInRedisList.remove(shopModel.getId());
						}
					});
				}
			}

			// DB中也没有，设置空缺在local jvm cache
			// 把db中也没有的shopId也打印出来.
			if (!CollectionUtils.isEmpty(shopIdsNotExistInRedisList)) {
				shopIdsNotExistInRedisList.forEach(item -> {
					rtMap.put(item, Optional.absent());
				});
				log.error("shopId-not-exist-in-db:" + shopIdsNotExistInRedisList);
			}

			return rtMap;

		}

		@Override
		protected Logger getLog() {
			return log;
		}
	};

	public List<ShopModel> getAll(List<Long> shopIdList) {
		return shopModelCache.getAll(shopIdList, ShopModel.class);
	}

	public ShopModel get(Long shopId) {
		return shopModelCache.get(shopId, ShopModel.class);
	}

	@Scheduled(cron = "0 * * * * ?")
	public void printCacheContainerStats() {
		shopModelCache.printCacheContainerStats();
	}

}
