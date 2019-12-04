package com.future.saf.monitor.prometheus.util;

import io.prometheus.client.Collector;

public class PrometheusTimeUtil {

	public static double convertNanosToSeconds(long startNanos, long endNanos) {
		return convertNanosToSeconds(endNanos - startNanos);
	}

	public static double convertNanosToSeconds(long elapsedNanos) {
		return elapsedNanos / Collector.NANOSECONDS_PER_SECOND;
	}

}
