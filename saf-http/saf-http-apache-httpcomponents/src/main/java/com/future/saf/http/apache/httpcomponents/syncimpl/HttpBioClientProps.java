package com.future.saf.http.apache.httpcomponents.syncimpl;

import lombok.Data;

@Data
public class HttpBioClientProps {

	// 连接池中每个路由（目标host）的连接数
	private int defaultMaxPerRoute = 1000;

	// 连接池总的连接数
	private int maxTotal = defaultMaxPerRoute * 2;

	// 设置通过打开的连接传输数据的超时时间（单位：毫秒）
	private int soTimeout = 50 * 1000;

	/**
	 * 单位是毫秒
	 * 
	 * 可用空闲连接过期时间,重用空闲连接时会先检查是否空闲时间超过这个时间，如果超过，释放socket重新建立
	 * 
	 * Defines period of inactivity in milliseconds after which persistent
	 * connections must be re-validated prior to being
	 * {@link #leaseConnection(java.util.concurrent.Future, long, java.util.concurrent.TimeUnit)
	 * leased} to the consumer. Non-positive value passed to this method
	 * disables connection validation. This check helps detect connections that
	 * have become stale (half-closed) while kept inactive in the pool.
	 */
	private int validateAfterInactivity = 2 * 1000;

	/**
	 * thread pool params
	 */
	private int corePoolSize = 50;
	private int maximumPoolSize = 200;
	private long keepAliveTime = 60;
	private int capacity = 0;

	// 定时关闭空闲连接时，过期连接的选择阈值
	private int connIdleTimeout = 60;

	// 指定blockingqueue中允许存放的最大任务数，后续要支持动态调整，可以认为是限流的范畴
	private int blockingQueueMaxSize = (maxTotal * 10) > Integer.MAX_VALUE ? Integer.MAX_VALUE : maxTotal * 10;
}
