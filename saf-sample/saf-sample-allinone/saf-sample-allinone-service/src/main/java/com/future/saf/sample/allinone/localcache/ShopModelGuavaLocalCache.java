package com.future.saf.sample.allinone.localcache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.future.saf.cache.redis.jedis.cluster.JedisClusterClient;
import com.future.saf.sample.allinone.constant.KVKeyConstant;
import com.future.saf.sample.allinone.manager.ShopManager;
import com.future.saf.sample.allinone.mapper.malldb.ShopMapper;
import com.future.saf.sample.allinone.model.ShopModel;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableScheduling
public class ShopModelGuavaLocalCache {

	@Autowired
	private ShopManager shopManager;

	@Autowired
	private ShopMapper shopMapper;

	@Resource(name = "mall")
	private JedisClusterClient mallJedisClusterClient;

	// 先设1吧，不用多，没有实时要求。
	private ExecutorService executor = Executors.newFixedThreadPool(10);

	// TODO initialCapacity和maximumSize以后放到apollo,可以动态配置。
	private LoadingCache<Long, Optional<ShopModel>> shopModelCache = CacheBuilder.newBuilder().recordStats()
			.initialCapacity(100).maximumSize(100).refreshAfterWrite(10, TimeUnit.SECONDS)
			.build(new CacheLoader<Long, Optional<ShopModel>>() {

				@Override
				public Optional<ShopModel> load(Long shopId) throws Exception {
					ShopModel shopModel = shopManager.findById(shopId);
					if (shopModel == null) {
						return Optional.absent();
					}
					return Optional.of(shopModel);
				}

				// 必须实现，否则cache.getAll会报错，默认没实现。
				@Override
				public Map<Long, Optional<ShopModel>> loadAll(Iterable<? extends Long> shopIds) throws Exception {
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

						List<ShopModel> shopModelList = shopMapper
								.queryShopModelListByShopIdList(shopIdsNotExistInRedisList);

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
				public ListenableFuture<Optional<ShopModel>> reload(Long key, Optional<ShopModel> oldValue)
						throws Exception {
					// asynchronous!
					ListenableFutureTask<Optional<ShopModel>> task = ListenableFutureTask.create(() -> load(key));
					executor.execute(task);
					return task;
				}

			});

	/**
	 * 批量获取shop model,如果存在没有加载到内存的keys，内部会调用loadAll去加载.
	 * 
	 * Notice:shopIdList元素个数上限有阈值，超过阈值的部分丢弃.
	 * 
	 * @param shopIdList
	 * @return
	 */
	private ImmutableMap<Long, Optional<ShopModel>> getAllWrapper(List<Long> shopIdList) {
		if (CollectionUtils.isEmpty(shopIdList)) {
			return null;
		}
		try {
			ImmutableMap<Long, Optional<ShopModel>> values = null;
			// TODO 1000这个阈值放到apollo里
			if (shopIdList.size() > 1000) {
				values = shopModelCache.getAll(shopIdList.subList(0, 1000));
			} else {
				values = shopModelCache.getAll(shopIdList);
			}

			if (values != null) {
				return values;
			}

			return null;
		} catch (ExecutionException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * 批量获取shop model,如果存在没有加载到内存的keys，内部会调用loadAll去加载.
	 * 
	 * Notice:shopIdList元素个数上限有阈值，超过阈值的部分丢弃.
	 * 
	 * @param shopIdList
	 * @return
	 */
	public List<ShopModel> getAll(List<Long> shopIdList) {
		ImmutableMap<Long, Optional<ShopModel>> wrapperMap = this.getAllWrapper(shopIdList);
		if (wrapperMap == null || wrapperMap.size() == 0) {
			return null;
		}
		ImmutableCollection<Optional<ShopModel>> collection = wrapperMap.values();
		if (collection != null && collection.size() > 0) {
			List<ShopModel> rtList = new ArrayList<ShopModel>();
			ImmutableList<Optional<ShopModel>> wrapperList = collection.asList();
			int size = wrapperList.size();
			Optional<ShopModel> osm = null;
			for (int i = 0; i < size; i++) {
				osm = wrapperList.get(i);
				if (osm.isPresent()) {
					rtList.add(JSON.parseObject(JSON.toJSONString(osm.get()), ShopModel.class));
				}
			}
			return rtList;
		} else {
			return null;
		}
	}

	/**
	 * 获取单个article info.
	 * 
	 * @param shopId
	 * @return
	 */
	public ShopModel get(Long shopId) {
		if (shopId == null) {
			return null;
		}
		try {
			Optional<ShopModel> osm = shopModelCache.get(shopId);
			if (osm.isPresent()) {
				return JSON.parseObject(JSON.toJSONString(osm.get()), ShopModel.class);
			} else {
				return null;
			}
		} catch (ExecutionException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	@Scheduled(cron = "0 * * * * ?")
	public void showCacheContainerSize() {
		try {
			log.info("cache container current size is:{}", shopModelCache.size());
			if (executor != null && executor instanceof ThreadPoolExecutor) {
				ThreadPoolExecutor tpe = (ThreadPoolExecutor) executor;

				log.info("cache container: threadPool.queueInfo: size:{}, remainingCapacity:{}", tpe.getQueue().size(),
						tpe.getQueue().remainingCapacity());

				log.info(
						"cache container: threadPool.info: activeCount:{}, corePoolSize:{}, maximumPoolSize:{}, poolSize:{}, completedTaskCount:{}, largestPoolSize:{}, taskCount:{}",
						tpe.getActiveCount(), tpe.getCorePoolSize(), tpe.getMaximumPoolSize(), tpe.getPoolSize(),
						tpe.getCompletedTaskCount(), tpe.getLargestPoolSize(), tpe.getTaskCount());

				CacheStats cacheStats = shopModelCache.stats();
				log.info(
						"cache container: cache.stats: averageLoadPenalty:{}, evictionCount:{}, hitCount:{}, hitRate:{}, loadCount:{}, loadExceptionCount:{}, loadExceptionRate:{}, loadSuccessCount:{}, missCount:{}, missRate:{}, requestCount:{}, totalLoadTime:{},",
						cacheStats.averageLoadPenalty(), cacheStats.evictionCount(), cacheStats.hitCount(),
						cacheStats.hitRate(), cacheStats.loadCount(), cacheStats.loadExceptionCount(),
						cacheStats.loadExceptionRate(), cacheStats.loadSuccessCount(), cacheStats.missCount(),
						cacheStats.missRate(), cacheStats.requestCount(), cacheStats.totalLoadTime());

			} else {
				log.error("cache container info print failed, please check!");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
