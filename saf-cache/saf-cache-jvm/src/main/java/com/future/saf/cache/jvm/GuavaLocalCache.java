package com.future.saf.cache.jvm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
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

/**
 * (1).二次封装guava的LoadingCache，方便使用。主要封装操作：
 * 
 * 1.封装reload异步操作，所有的localCache统一使用一个线程池，节约资源，防止一个guava cache一个线程池。
 * 同时也提供覆盖机会，但一般不要覆盖重写。
 * 
 * 2.封装LoadingCache的getAll, get方法，返回的直接是内存的深拷贝对象，防止出现内存缓存被篡改发生的风险。
 * 
 * 3.提供统一的统计打印格式.
 * 
 * (2).需要注意:
 * 
 * 必须实现loadImpl，loadAllImpl，分别对应get,
 * getAll;且每个key必须对应一个对象(guava强制要求，否则会报错一个也不会返回),如果对应的key没有查到对象，
 * 用guava的Optional.absent()占位对象替换。
 * 
 * 
 * @author hepengyuan
 * @version create time: 2018年10月25日 上午11:04:18
 *
 */
@Slf4j
public abstract class GuavaLocalCache<K, V> {

	private static ExecutorService RELOAD_EXECUTOR = Executors.newFixedThreadPool(10);

	private LoadingCache<K, Optional<V>> localCache;

	/**
	 * 设定getAll允许获取的最大元素个数.
	 */
	private int getAllUpLimit = 1000;

	/**
	 * @param initialCapacity        localCache初始容量
	 * @param maximumSize            localCache最大容量
	 * @param refreshDurationSeconds 元素在local中失效时间；失效后自动load，load完成前返回oldValue，完成后返回newValue.
	 */
	public GuavaLocalCache(int initialCapacity, int maximumSize, int refreshDurationSeconds) {
		localCache = CacheBuilder.newBuilder().recordStats().initialCapacity(initialCapacity).maximumSize(maximumSize)
				.refreshAfterWrite(refreshDurationSeconds, TimeUnit.SECONDS).build(new CacheLoader<K, Optional<V>>() {

					@Override
					public Optional<V> load(K key) throws Exception {
						return loadImpl(key);
					}

					@Override
					public Map<K, Optional<V>> loadAll(Iterable<? extends K> keys) throws Exception {
						return loadAllImpl(keys);
					}

					@Override
					public ListenableFuture<Optional<V>> reload(K key, Optional<V> oldValue) throws Exception {
						return reloadImpl(key, oldValue);
					}

				});
	}

	/**
	 * @param initialCapacity        localCache初始容量
	 * @param maximumSize            localCache最大容量
	 * @param refreshDurationSeconds 元素在local中失效时间；失效后自动load，load完成前返回oldValue，完成后返回newValue.
	 * @param getAllUpLimit          设定getAll允许获取的最大元素个数.默认1000.
	 */
	public GuavaLocalCache(int initialCapacity, int maximumSize, int refreshDurationSeconds, int getAllUpLimit) {
		this(initialCapacity, maximumSize, refreshDurationSeconds);
		this.getAllUpLimit = getAllUpLimit;
	}

	/**
	 * get会触发。
	 * 
	 * 当内存中没有对应缓存时，调用此方法加载，如果没有对应对象，用Optional.absent()占位。
	 * 
	 * @param key
	 * @return
	 */
	protected abstract Optional<V> loadImpl(K key);

	/**
	 * getAll会触发。
	 * 
	 * 当内存中没有对应的批量缓存时，调用此方法加载，如果没有对应对象，用Optional.absent()占位。
	 * 
	 * @param keys
	 * @return
	 */
	protected abstract Map<K, Optional<V>> loadAllImpl(Iterable<? extends K> keys);

	protected ListenableFuture<Optional<V>> reloadImpl(K key, Optional<V> oldValue) {
		// asynchronous!
		ListenableFutureTask<Optional<V>> task = ListenableFutureTask.create(() -> loadImpl(key));
		RELOAD_EXECUTOR.execute(task);
		return task;
	}

	/**
	 * 打印准确的触发类.
	 * 
	 * @return
	 */
	protected abstract Logger getLog();

	public void printCacheContainerStats() {
		try {
			getLog().info("cache container current size is:{}", localCache.size());
			if (RELOAD_EXECUTOR != null && RELOAD_EXECUTOR instanceof ThreadPoolExecutor) {
				ThreadPoolExecutor tpe = (ThreadPoolExecutor) RELOAD_EXECUTOR;

				getLog().info("cache container: threadPool.queueInfo: size:{}, remainingCapacity:{}",
						tpe.getQueue().size(), tpe.getQueue().remainingCapacity());

				getLog().info(
						"cache container: threadPool.info: activeCount:{}, corePoolSize:{}, maximumPoolSize:{}, poolSize:{}, completedTaskCount:{}, largestPoolSize:{}, taskCount:{}",
						tpe.getActiveCount(), tpe.getCorePoolSize(), tpe.getMaximumPoolSize(), tpe.getPoolSize(),
						tpe.getCompletedTaskCount(), tpe.getLargestPoolSize(), tpe.getTaskCount());

				CacheStats cacheStats = localCache.stats();
				getLog().info(
						"cache container: cache.stats: averageLoadPenalty:{}, evictionCount:{}, hitCount:{}, hitRate:{}, loadCount:{}, loadExceptionCount:{}, loadExceptionRate:{}, loadSuccessCount:{}, missCount:{}, missRate:{}, requestCount:{}, totalLoadTime:{},",
						cacheStats.averageLoadPenalty(), cacheStats.evictionCount(), cacheStats.hitCount(),
						cacheStats.hitRate(), cacheStats.loadCount(), cacheStats.loadExceptionCount(),
						cacheStats.loadExceptionRate(), cacheStats.loadSuccessCount(), cacheStats.missCount(),
						cacheStats.missRate(), cacheStats.requestCount(), cacheStats.totalLoadTime());

			} else {
				getLog().error("cache container info print failed, please check!");
			}
		} catch (Exception e) {
			getLog().error(e.getMessage(), e);
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
	private ImmutableMap<K, Optional<V>> getAllWrapper(List<K> keyList) {
		if (CollectionUtils.isEmpty(keyList)) {
			return null;
		}
		try {
			ImmutableMap<K, Optional<V>> values = null;
			// TODO 1000这个阈值放到apollo里
			if (getAllUpLimit != -1 && getAllUpLimit > 0 && keyList.size() > getAllUpLimit) {
				values = localCache.getAll(keyList.subList(0, getAllUpLimit));
			} else {
				values = localCache.getAll(keyList);
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
	 * @param keyList
	 * @param clazz
	 * @return
	 */
	public List<V> getAll(List<K> keyList, Class<V> clazz) {
		ImmutableMap<K, Optional<V>> wrapperMap = this.getAllWrapper(keyList);
		if (wrapperMap == null || wrapperMap.size() == 0) {
			return null;
		}
		ImmutableCollection<Optional<V>> collection = wrapperMap.values();
		if (collection != null && collection.size() > 0) {
			List<V> rtList = new ArrayList<V>();
			ImmutableList<Optional<V>> wrapperList = collection.asList();
			int size = wrapperList.size();
			Optional<V> osm = null;
			for (int i = 0; i < size; i++) {
				osm = wrapperList.get(i);
				if (osm.isPresent()) {
					rtList.add(JSON.parseObject(JSON.toJSONString(osm.get()), clazz));
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
	 * @param key
	 * @param clazz
	 * @return
	 */
	public V get(K key, Class<V> clazz) {
		if (key == null) {
			return null;
		}
		try {
			Optional<V> osm = localCache.get(key);
			if (osm.isPresent()) {
				return JSON.parseObject(JSON.toJSONString(osm.get()), clazz);
			} else {
				return null;
			}
		} catch (ExecutionException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	public CacheStats getCacheStats() {
		return localCache.stats();
	}

}
