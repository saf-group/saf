package com.future.saf.http.apache.httpcomponents.syncimpl;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.Assert;

import com.future.saf.core.thread.CThreadFactory;
import com.future.saf.core.util.LogUtil;
import com.future.saf.http.apache.httpcomponents.exception.BlockingQueueThresholdSizeExceedException;
import com.future.saf.http.apache.httpcomponents.util.HttpClientUtil;
import com.future.saf.logging.basic.Loggers;
import com.future.saf.monitor.basic.AbstractTimer;
import com.future.saf.monitor.prometheus.metric.profile.PrometheusMetricProfilerProcessor;
import com.future.saf.web.basic.util.HttpUtil;

import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram.Timer;

public class HttpBioClient implements DisposableBean {

	private static final Logger ACCESS_LOGGER = Loggers.getAccessLogger();
	private static final Logger PERFORMANCE_LOGGER = Loggers.getPerformanceLogger();

	private static String PREFIX = HttpBioClient.class.getSimpleName();

	// 当前HttpBioClient的标示，同时也是metric
	// name的一部分，也是HttpBioClientBean的一部分，也是apollo配置key中的一部分
	private String instance;

	// 发起http请求的Executor
	private ThreadPoolExecutor HTTP_EXECUTOR;
	private BlockingQueue<Runnable> workQueue;
	private int queueCapacity = Integer.MAX_VALUE;

	private CloseableHttpClient internalClient;
	private PoolingHttpClientConnectionManager cm;
	private final HttpBioClientProps props;

	// (1).度量与统计http连接
	// 度量与统计一个http connection pool中不同url对连接的占用情况
	private final Gauge HTTP_CONNECTION_STAT;
	// 度量与统计http连接的request
	private final PrometheusMetricProfilerProcessor HTTP_CONNECTION_REQUEST_STAT;
	// 当blockingqueue中的认为超过指定的阈值时，进行reject，度量和统计这个reject
	private final PrometheusMetricProfilerProcessor HTTP_CONNECTION_REQUEST_REJECT_STAT; // 度量与统计http连接的response
	private final PrometheusMetricProfilerProcessor HTTP_CONNECTION_RESPONSE_STAT;

	// (2).度量与统计blockingqueue
	// 实时度量queue中元素个数
	private Gauge BLOCKING_QUEUE_USED_CAPACITY_STAT;
	// 实时度量queue中元素个数占队列总容量百分比
	private Gauge BLOCKING_QUEUE_USED_CAPACITY_PERCENTAGE_STAT;

	// 定时统计度量http connection, 释放idle http connection
	private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1,
			new CThreadFactory("chttpclient-bio-"));

	private static final ResponseHandler<HttpResponse> DEFAULT_RESPONSE_HANDLER = new ResponseHandler<HttpResponse>() {
		@Override
		public HttpResponse handle(HttpResponse httpResponse) {
			return httpResponse;
		}
	};

	public HttpBioClient(String instance, HttpBioClientProps cHttpBioClientProps) {

		this.instance = instance;
		this.props = cHttpBioClientProps;

		HTTP_CONNECTION_STAT = Gauge.build().name("http_bio_client_outgoing_connection" + "_" + instance)
				.help("app_http_bio_outgoing_conn status").labelNames("route", "state").register();

		HTTP_CONNECTION_REQUEST_STAT = new PrometheusMetricProfilerProcessor(
				"http_bio_client_outgoing_request" + "_" + instance, "http_bio_client_outgoing_request",
				"http_bio_client_outgoing_request", new String[] { "method", "host" });

		HTTP_CONNECTION_REQUEST_REJECT_STAT = new PrometheusMetricProfilerProcessor(
				"http_bio_client_outgoing_request_reject" + "_" + instance, "http_bio_client_outgoing_request_reject",
				"http_bio_client_outgoing_request_reject", new String[] { "method", "host" });

		HTTP_CONNECTION_RESPONSE_STAT = new PrometheusMetricProfilerProcessor(
				"http_bio_client_outgoing_response" + "_" + instance, "http_bio_client_outgoing_response",
				"http_bio_client_outgoing_response", new String[] { "method", "host" });

		BLOCKING_QUEUE_USED_CAPACITY_STAT = Gauge.build()
				.name("http_bio_client_outgoing_blocking_queue_used_capacity" + "_" + instance)
				.help("http_bio_client_outgoing_blocking_queue_used_capacity").labelNames("instance").register();

		BLOCKING_QUEUE_USED_CAPACITY_PERCENTAGE_STAT = Gauge.build()
				.name("http_bio_client_outgoing_blocking_queue_used_capacity_percentage" + "_" + instance)
				.help("http_bio_client_outgoing_blocking_queue_used_capacity").labelNames("instance").register();

		init();
	}

	private void init() {

		// ArrayBlockingQueue:
		// 底层是使用一个数组实现队列的，并且在构造ArrayBlockingQueue时需要指定容量，也就意味着底层数组一旦创建了，容量就不能改变了，因此ArrayBlockingQueue是一个容量限制的阻塞队列。因此，在队列全满时执行入队将会阻塞，在队列为空时出队同样将会阻塞。
		// SynchronousQueue:
		// 是一个内部只能包含一个元素的队列。插入元素到队列的线程被阻塞，直到另一个线程从队列中获取了队列中存储的元素。同样，如果线程尝试获取元素并且当前不存在任何元素，则该线程将被阻塞，直到线程将元素插入队列。
		// LinkedBlockingQueue:
		// 是一个线程安全的阻塞队列，基于链表实现，一般用于生产者与消费者模型的开发中。采用锁机制来实现多线程同步，提供了一个构造方法用来指定队列的大小，如果不指定大小，队列采用默认大小（Integer.MAX_VALUE，即整型最大值）。
		// workQueue = props.getCapacity() > 0 ? new
		// ArrayBlockingQueue<>(props.getCapacity()) : new SynchronousQueue<>();
		workQueue = props.getCapacity() > 0 ? new LinkedBlockingQueue<Runnable>(props.getCapacity())
				: new LinkedBlockingQueue<Runnable>();
		queueCapacity = props.getCapacity() > 0 ? props.getCapacity() : queueCapacity;

		// corePoolSize: 最小线程数，即使都是idle maximumPoolSize： 线程池允许的最大线程数
		// keepAliveTime： corePoolSize以外的线程空闲后保留存活的最大时间 unit： keepAliveTime时间
		// workQueue： 存放http reqeust runable. threadFactory: 创建线程池中的线程的线程工厂
		// threadFactory： 线程池创造线程的线程工厂
		// RejectedExecutionHandler：
		// 拒绝策略，当线程池的任务缓存队列已满并且线程池中的线程数目达到maximumPoolSize，如果还有任务到来就会采取任务拒绝策略，通常有以下四种策略：
		// ThreadPoolExecutor.AbortPolicy:丢弃任务并抛出RejectedExecutionException异常。
		// ThreadPoolExecutor.DiscardPolicy：也是丢弃任务，但是不抛出异常。
		// ThreadPoolExecutor.DiscardOldestPolicy：丢弃队列最前面的任务，然后重新尝试执行任务（重复此过程）
		// ThreadPoolExecutor.CallerRunsPolicy：由调用线程处理该任务
		HTTP_EXECUTOR = new ThreadPoolExecutor(props.getCorePoolSize(), props.getMaximumPoolSize(),
				props.getKeepAliveTime(), TimeUnit.SECONDS, workQueue, new CThreadFactory("chttpclient-bio-"),
				new ThreadPoolExecutor.AbortPolicy());
		// 1.allowCoreThreadTimeOut作用：
		// allowCoreThreadTimeOut为true：
		// 该值为true，则线程池数量最后销毁到0个。corePoolSize线程空闲时间达到keepAliveTime也将关闭。
		// allowCoreThreadTimeOut为false： 销毁机制：超过核心线程数时，而且（超过最大值或者timeout过），就会销毁。
		// 设置为True的原因：
		// 在一些业务平台中，比如广告中台，可能要对接多个广告平台，这就涉及到访问多个域名，这样就会有多个HttpBioClient的Bean实例，这样才可以做到资源隔离，防止某个广告平台响应慢拖死全部；
		// 但是这样带来另外一个问题，就是当不繁忙时，空闲的coreThreads会很多，这个是非常费的。所以这里allowCoreThreadTimeOut设置为True.
		// 注意：
		// 上述广告中台这里还涉及到另外一个问题：就是调度的问题，当公司业务规模上升，接入的广告平台会变得很多，在这种情况下不可能每个服务都接所有的广告平台，资源都不够创建线程的，必然要有一个调度系统让不同的实例对接不同的广告平台；
		// 这又会衍生出一个经典的注册发现问题。本工程目前暂时不涉及这个调度部分。
		HTTP_EXECUTOR.allowCoreThreadTimeOut(true);
		// prestartAllCoreThreads：
		// 可以在线程池创建，但还没有接收到任何任务的情况下，先行创建符合corePoolSize参数值的线程数。
		// 这样设置也很好理解，可以理解为一种预热。
		HTTP_EXECUTOR.prestartAllCoreThreads();

		// 1.setTcpNoDelay(true): 非常关键
		// TCP/IP协议中针对TCP默认开启了Nagle算法。Nagle算法通过减少需要传输的数据包，来优化网络。在内核实现中，数据包的发送和接受会先做缓存，分别对应于写缓存和读缓存。
		// 启动TCP_NODELAY，就意味着禁用了Nagle算法，允许小包的发送。对于延时敏感型，同时数据传输量比较小的应用，开启TCP_NODELAY选项无疑是一个正确的选择。
		// 如果设置为false，会造成延时增加。
		// 2.setSoTimeout
		// 这个参数通过socket.setSoTimeout(int
		// timeout)方法设置，可以看出它的意思是，socket关联的InputStream的read()方法会阻塞，直到超过设置的so
		// timeout，就会抛出SocketTimeoutException。当不设置这个参数时，默认值为无穷大，即InputStream的read方法会一直阻塞下去，除非连接断开。
		// 所以显然必须设置一个合理的值，否则系统可用性不值得信赖。
		final SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(props.getSoTimeout()).setTcpNoDelay(true)
				.build();

		// PoolingHttpClientConnectionManager: http连接池管理
		// defaultMaxPerRoute:
		// 默认的每个路由的最大连接数,比如www.baidu.com和www.sohu.com是两个路由。这个参数很重要。
		// setMaxTotal：
		// 设置最大连接数，默认值是：defaultMaxPerRoute*2
		// validateAfterInactivity：
		// 可用空闲连接过期时间,重用空闲连接时会先检查是否空闲时间超过这个时间，如果超过，释放socket重新建立
		cm = new PoolingHttpClientConnectionManager();
		cm.setDefaultMaxPerRoute(props.getDefaultMaxPerRoute());
		cm.setMaxTotal(props.getMaxTotal());
		cm.setValidateAfterInactivity(props.getValidateAfterInactivity());
		cm.setDefaultSocketConfig(socketConfig);

		// disableAutomaticRetries：禁用自动重试
		internalClient = HttpClients.custom().setConnectionManager(cm).setConnectionManagerShared(false)
				// .setUserAgent(this.config.getUserAgent())
				// .disableContentCompression()
				// .evictExpiredConnections()
				.disableAutomaticRetries().build();

		scheduledConnStatsAndRelease();
	}

	private void scheduledConnStatsAndRelease() {

		scheduledExecutorService.scheduleAtFixedRate(() -> {

			// 1.http connection stat
			for (HttpRoute route : cm.getRoutes()) {
				PoolStats stats = cm.getStats(route);
				String hostName = route.getTargetHost().getHostName();
				HTTP_CONNECTION_STAT.labels(hostName, "Available").set(stats.getAvailable());
				HTTP_CONNECTION_STAT.labels(hostName, "Leased").set(stats.getLeased());
				HTTP_CONNECTION_STAT.labels(hostName, "Max").set(stats.getMax());
				HTTP_CONNECTION_STAT.labels(hostName, "Pending").set(stats.getPending());
			}

			BLOCKING_QUEUE_USED_CAPACITY_STAT.labels(instance).set(workQueue.size());
			double remainingCapacityPercentage = ((double) workQueue.remainingCapacity()) / ((double) (queueCapacity));
			BLOCKING_QUEUE_USED_CAPACITY_PERCENTAGE_STAT.labels(instance).set(remainingCapacityPercentage);

			// 2.打印HTTP_EXECUTOR状态值

			LogUtil.putContextColumn1("health");
			LogUtil.putContextColumn2("chttpclient" + ":" + instance + ":" + DateTime.now().toString("yyyyMMddHHmmss"));
			PERFORMANCE_LOGGER.info(LogUtil.LINE);
			PERFORMANCE_LOGGER.info("{} | {} | {}", "Available", "Leased", "Pending");
			PERFORMANCE_LOGGER.info("{} | {} | {}", cm.getTotalStats().getAvailable(), cm.getTotalStats().getLeased(),
					cm.getTotalStats().getPending());
			PERFORMANCE_LOGGER.info("{} | {} | {} | {}", "CorePoolSize", "MaximumPoolSize", "PoolSize",
					"LargestPoolSize");
			PERFORMANCE_LOGGER.info("{} | {} | {} | {}", HTTP_EXECUTOR.getCorePoolSize(),
					HTTP_EXECUTOR.getMaximumPoolSize(), HTTP_EXECUTOR.getPoolSize(),
					HTTP_EXECUTOR.getLargestPoolSize());
			PERFORMANCE_LOGGER.info("{} | {} | {} | {}", "ActiveCount", "CompletedTaskCount", "TaskCount", "QueueSize");
			PERFORMANCE_LOGGER.info("{} | {} | {} | {}", HTTP_EXECUTOR.getActiveCount(),
					HTTP_EXECUTOR.getCompletedTaskCount(), HTTP_EXECUTOR.getTaskCount(),
					HTTP_EXECUTOR.getQueue().size());
			PERFORMANCE_LOGGER.info(LogUtil.LINE);
			LogUtil.clearContext();

			// 3.定时释放过期和空闲的资源
			try {
				PERFORMANCE_LOGGER.info(LogUtil.LINE);
				LogUtil.putContextColumn1("pool");
				LogUtil.putContextColumn2(
						"chttpclient" + ":" + instance + ":" + DateTime.now().toString("yyyyMMddHHmmss"));
				PERFORMANCE_LOGGER.info("closeExpiredConnections");
				// 定时关闭过期连接并释放资源
				cm.closeExpiredConnections();
				PERFORMANCE_LOGGER.info("closeIdleConnections");
				// 定时关闭空闲连接并释放资源
				cm.closeIdleConnections(props.getConnIdleTimeout(), TimeUnit.SECONDS);
			} catch (Exception e) {
				PERFORMANCE_LOGGER.error("", e);
			} finally {
				PERFORMANCE_LOGGER.info(LogUtil.LINE);
				LogUtil.clearContext();
			}
		}, 30, 30, TimeUnit.SECONDS);
	}

	public static abstract class ResponseHandler<T> {
		abstract public T handle(HttpResponse httpResponse);
	}

	class HttpExecutionTask<V> implements Callable<V> {
		private ResponseHandler<V> responseHandler;
		private HttpUriRequest request;
		private final long begin;
		private final String reqId;
		private final String url;
		private final String route;

		public HttpExecutionTask(HttpUriRequest request, long begin, String reqId, String url, String route,
				ResponseHandler<V> responseHandler) {
			this.responseHandler = responseHandler;
			this.request = request;
			this.begin = begin;
			this.reqId = reqId;
			this.url = url;
			this.route = route;
		}

		@Override
		public V call() throws IOException {
			HTTP_CONNECTION_RESPONSE_STAT.inc(url, route);
			AbstractTimer<Timer, Gauge, Gauge> httpTimer = HTTP_CONNECTION_RESPONSE_STAT.startTimer(url, route);
			// LatencyStat.Timer httpTimer = HTTP_STAT.startTimer(url, route);
			ACCESS_LOGGER.info("{}|{}|{}|before", PREFIX, reqId, System.currentTimeMillis() - begin);
			CloseableHttpResponse httpResponse = null;
			V response = null;
			try {
				httpResponse = internalClient.execute(request);
				ACCESS_LOGGER.info("{}|{}|{}|after|{}", PREFIX, reqId, System.currentTimeMillis() - begin,
						httpResponse != null ? httpResponse.getStatusLine().getStatusCode() : -1);
				response = responseHandler.handle(httpResponse);
				ACCESS_LOGGER.info("{}|{}|{}|handle", PREFIX, reqId, System.currentTimeMillis() - begin);
			} finally {
				if (httpResponse == null) {
					ACCESS_LOGGER.info("{}|{}|{}|abort", PREFIX, reqId, System.currentTimeMillis() - begin);
					HTTP_CONNECTION_RESPONSE_STAT.error(url, route);
				} else {
					if (!(response instanceof HttpResponse)) {
						EntityUtils.consumeQuietly(httpResponse.getEntity());
					}
				}
				httpTimer.observeDuration(url, route);
				HTTP_CONNECTION_RESPONSE_STAT.dec(url, route);
			}
			return response;
		}
	}

	public HttpResponse execute(String reqId, HttpUriRequest request, int timeout, TimeUnit timeUnit)
			throws InterruptedException, ExecutionException, TimeoutException,
			BlockingQueueThresholdSizeExceedException {
		return this.execute(reqId, request, timeout, timeUnit, DEFAULT_RESPONSE_HANDLER);
	}

	private <T> T execute(String reqId, HttpUriRequest request, int timeout, TimeUnit timeUnit,
			ResponseHandler<T> handler) throws InterruptedException, ExecutionException, TimeoutException,
			BlockingQueueThresholdSizeExceedException {

		String route = HttpClientUtil.determineTarget(request);
		String url = request.getMethod() + " " + HttpUtil.getPatternUrl(request.getURI().getPath());

		if (workQueue.size() > props.getBlockingQueueMaxSize()) {
			HTTP_CONNECTION_REQUEST_REJECT_STAT.inc(url, route);
			throw new BlockingQueueThresholdSizeExceedException(
					"current blocking queue's size exceed Threshold:" + props.getBlockingQueueMaxSize());
		}

		Assert.isTrue(StringUtils.isNotEmpty(reqId), "must specify reqId!!!");
		long begin = System.currentTimeMillis();
		ACCESS_LOGGER.info("{}|{}|{}|start", PREFIX, reqId, 0);
		T httpResponse;
		HTTP_CONNECTION_REQUEST_STAT.inc(url, route);
		AbstractTimer<Timer, Gauge, Gauge> requestTimer = HTTP_CONNECTION_REQUEST_STAT.startTimer(url, route);
		try {
			Future<T> future = HTTP_EXECUTOR
					.submit(new HttpExecutionTask<>(request, begin, reqId, url, route, handler));
			httpResponse = future.get(timeout, timeUnit);
		} catch (InterruptedException e) {
			request.abort();
			HTTP_CONNECTION_REQUEST_STAT.error(url, route);
			ACCESS_LOGGER.error("{}|{}|{}|fail, with exception: {}", PREFIX, reqId, System.currentTimeMillis() - begin,
					e.getMessage());
			throw e;
		} catch (ExecutionException e) {
			request.abort();
			HTTP_CONNECTION_REQUEST_STAT.error(url, route);
			ACCESS_LOGGER.error("{}|{}|{}|fail, with exception {} - {}", PREFIX, reqId,
					System.currentTimeMillis() - begin, ExecutionException.class.getSimpleName(),
					e.getCause().getMessage());
			throw e;
		} catch (TimeoutException e) {
			request.abort();
			HTTP_CONNECTION_REQUEST_STAT.error(url, route);
			throw e;
		} finally {
			ACCESS_LOGGER.info("{}|{}|{}|return", PREFIX, reqId, System.currentTimeMillis() - begin);
			requestTimer.observeDuration(url, route);
			HTTP_CONNECTION_REQUEST_STAT.dec(url, route);
		}
		return httpResponse;
	}

	public void closeResponseSilently(HttpResponse httpResponse) {
		if (httpResponse == null)
			return;
		if (httpResponse instanceof CloseableHttpResponse) {
			try {
				((CloseableHttpResponse) httpResponse).close();
			} catch (IOException ioe) {
				/* silently close the response. */
			}
		}
	}

	@Override
	public void destroy() throws Exception {
		internalClient.close();
	}

	public static boolean allRunning() {
		return true;
	}

	public static boolean readyForRequest() {
		return allRunning();
	}
}
