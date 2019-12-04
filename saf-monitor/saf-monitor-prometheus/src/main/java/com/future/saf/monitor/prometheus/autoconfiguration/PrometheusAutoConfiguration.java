package com.future.saf.monitor.prometheus.autoconfiguration;

import org.springframework.context.annotation.Bean;

import com.future.saf.monitor.basic.AbstractMetricProfilerProcessor;
import com.future.saf.monitor.basic.MetricProfilerAspect;
import com.future.saf.monitor.config.MonitorConfig;
import com.future.saf.monitor.prometheus.metric.profile.LatencyMetricProfilerProcessor;

public class PrometheusAutoConfiguration {

	@Bean
	public MonitorConfig monitorConfig() {
		return new MonitorConfig();
	}

	@Bean
	public MetricProfilerAspect customProfilerAspect() {
		return new MetricProfilerAspect();
	}

	@Bean(name = "customMetricProfileProcessor")
	public AbstractMetricProfilerProcessor customMetricProfileProcessor() {
		LatencyMetricProfilerProcessor processor = new LatencyMetricProfilerProcessor("custom", "custom", "custom",
				new String[] { "class", "method" });
		return processor;
	}

}
