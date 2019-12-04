package com.future.saf.monitor.prometheus.listener;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import com.future.saf.core.preloader.Preloader;
import com.future.saf.core.preloader.PreloaderRegistry;
import com.future.saf.core.preloader.PreloaderResult;
import com.future.saf.logging.basic.Loggers;
import com.future.saf.monitor.config.MonitorConfig;

import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnClass({ ApplicationListener.class, ApplicationEvent.class, ApplicationReadyEvent.class })
@Slf4j
public class PrometheusMetricEventListener implements ApplicationListener<ApplicationEvent> {

	private static final Logger logger = Loggers.getFrameworkLogger();

	@Autowired
	private MonitorConfig monitorConfig;
	@Autowired
	private PreloaderRegistry preloaderRegistry;

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ApplicationReadyEvent) {

			logger.info(StringUtils.rightPad(
					"** begin to start prometheus server and metrics  " + event.getClass().getSimpleName() + " ", 150,
					'*'));
			startPrometheusHttpServer();
			preload();

		} else if (event instanceof ApplicationFailedEvent) {

			logger.info(StringUtils.rightPad(
					"** prometheus server and metrics start failed " + event.getClass().getSimpleName() + " ", 150,
					'*'));

		} else if (event instanceof ContextClosedEvent) {

			logger.info(StringUtils.rightPad(
					"** prometheus server and metrics has stopped " + event.getClass().getSimpleName() + " ", 150,
					'*'));

		} else if (StringUtils.containsAny(event.getClass().getSimpleName(), "Application", "Context")) {

			logger.info("{} - {}", "** prometheus server and metrics " + ApplicationListener.class.getSimpleName(),
					event.getClass().getSimpleName());

		} else {
			logger.info("{}", "** other event " + ApplicationListener.class.getSimpleName(),
					event.getClass().getSimpleName());
		}
	}

	private void startPrometheusHttpServer() {
		try {
			DefaultExports.initialize();
			new HTTPServer(monitorConfig.getPort(), true);
			log.info("{} started on port {}", "Prometheus HttpServer", monitorConfig.getPort());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private void preload() {
		Set<Preloader> preloaderSet = preloaderRegistry.preloaders();
		int count = (preloaderSet == null || preloaderSet.isEmpty()) ? 0 : preloaderSet.size();

		CountDownLatch countDownLatch = new CountDownLatch(count);

		// 线程池执行，加超时时间保证时效
		ExecutorService threadPool = Executors.newFixedThreadPool(10);
		for (Preloader preloader : preloaderSet) {
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					try {

						long begin = System.currentTimeMillis();
						final PreloaderResult preloadResult = preloader.preload();
						Loggers.getFrameworkLogger().info("preload -> {}, cost: {}ms", preloadResult,
								System.currentTimeMillis() - begin);

					} catch (Exception e) {
						log.error(e.getMessage(), e);
					} finally {
						countDownLatch.countDown();
					}
				}
			});
		}

		try {
			// 防止时间过长
			countDownLatch.await(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		} finally {
			PreloaderResult.complete();
		}
	}
}
