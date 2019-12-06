package com.future.saf.monitor.basic;

import java.util.concurrent.TimeUnit;

public abstract class AbstractMetricProfilerProcessor<T, TMax, TMin> {

	public abstract void inc(String... labelValues);

	public abstract void dec(String... labelValues);

	public abstract AbstractTimer<T, TMax, TMin> startTimer(String... labelValues);

	public abstract void observe(long value, TimeUnit timeUnit, String... labelValues);

	public abstract void error(String... labelValues);
}
