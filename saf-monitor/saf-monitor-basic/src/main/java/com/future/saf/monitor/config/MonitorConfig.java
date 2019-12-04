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

	@Value("${monitor.mapper.enable:true}")
	private boolean enableMonitorMapper = true;

	@Value("${monitor.custom.enable:true}")
	private boolean enableMonitorCustom = true;

	public static volatile boolean ENABLE_MONITOR_MAPPER = true;
	public static volatile boolean ENABLE_MONITOR_MOTAN = true;

	@Scheduled(fixedRate = 60)
	public void refresh() {
		ENABLE_MONITOR_MAPPER = enableMonitorMapper;
		ENABLE_MONITOR_MOTAN = enableMonitorMotan;
	}

}
