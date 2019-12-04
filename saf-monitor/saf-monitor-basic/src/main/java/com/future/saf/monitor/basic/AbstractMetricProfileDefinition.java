package com.future.saf.monitor.basic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.future.saf.monitor.exception.MonitorInitException;

public abstract class AbstractMetricProfileDefinition {

	private static final Map<String, AbstractMetricProfileDefinition> METRIC_PROFILER_STAT_MAP = new ConcurrentHashMap<String, AbstractMetricProfileDefinition>();

	protected void add(String name, AbstractMetricProfileDefinition stat) throws MonitorInitException {
		synchronized (AbstractMetricProfileDefinition.class) {
			if (METRIC_PROFILER_STAT_MAP.containsKey(name)) {
				throw new MonitorInitException(
						"metric name has been init, please change it. invalid metric name:" + name);
			} else {
				METRIC_PROFILER_STAT_MAP.put(name, stat);
			}
		}
	}
}
