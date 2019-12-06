package com.future.saf.monitor.basic;

public abstract class AbstractTimer<T, TMax, TMin> {

	public AbstractTimer(T timer, TMax max, TMin min) {
	}

	public abstract double observeDuration(String... labelValues);
}
