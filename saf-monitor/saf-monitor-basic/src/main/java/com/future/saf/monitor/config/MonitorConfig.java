package com.future.saf.monitor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class MonitorConfig {

	@Value("${monitor.prometheus.port:9145}")
	private int port = 9145;

	@Value("${monitor.log.delay:30}")
	private int logDelay = 30;

	@Value("${monitor.log.period:60}")
	private int logPeriod = 60;

	@Value("${monitor.redis.enable:true}")
	private boolean enableMonitorRedis = true;

	@Value("${monitor.motan.enable:true}")
	private boolean enableMonitorMotan = true;

	@Value("${monitor.dubbo.enable:true}")
	private boolean enableMonitorDubbo = true;

	@Value("${monitor.mapper.enable:true}")
	private boolean enableMonitorMapper = true;

	@Value("${monitor.custom.enable:true}")
	private boolean enableMonitorCustom = true;

	@Value("${monitor.http.bio.client.flowcontrol.enable:true}")
	private boolean enableHttpBioClientFlowControl = true;

	@Value("${monitor.profile.http.enable:true}")
	private boolean enableHttpProfiler;

	@Value("${monitor.trace.http.enable:true}")
	private boolean enableHttpTracingInterceptor;

	public static volatile boolean ENABLE_MONITOR_MAPPER = true;
	public static volatile boolean ENABLE_MONITOR_MOTAN = true;
	public static volatile boolean ENABLE_MONITOR_DUBBO = true;
	public static volatile boolean ENABLE_HTTP_BIO_CLIENT_FLOWCONTROL = true;
	public static volatile boolean ENABLE_HTTP_PROFILE = true;
	public static volatile boolean ENABLE_HTTP_TRACEING_INTERCEPTOR = true;

	@Scheduled(fixedRate = 60)
	public void refresh() {
		ENABLE_MONITOR_MAPPER = enableMonitorMapper;
		ENABLE_MONITOR_MOTAN = enableMonitorMotan;
		ENABLE_MONITOR_DUBBO = enableMonitorDubbo;
		ENABLE_HTTP_BIO_CLIENT_FLOWCONTROL = enableHttpBioClientFlowControl;
		ENABLE_HTTP_PROFILE = enableHttpProfiler;
		ENABLE_HTTP_TRACEING_INTERCEPTOR = enableHttpTracingInterceptor;
	}

}
