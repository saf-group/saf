package com.future.saf.monitor.basic;

public abstract class AbstractMetricProfilerProcessor {

	public abstract void begin(String... labelValues);

	public abstract void error(String... labelValues);

	public abstract void end(String... labelValues);
}
