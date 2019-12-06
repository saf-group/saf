package com.future.saf.monitor.prometheus.metric.profile;

import com.future.saf.monitor.basic.AbstractMetricProfilerProcessor;
import com.future.saf.monitor.basic.AbstractTimer;
import com.future.saf.monitor.exception.MonitorInitException;

import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram.Timer;

public class PrometheusMetricProfilerProcessor extends AbstractMetricProfilerProcessor<Timer, Gauge, Gauge> {

	private PrometheusMetricProfileDefinition stat;

	public PrometheusMetricProfilerProcessor(String name, String tag, String help, String[] labelNames)
			throws MonitorInitException {
		this.stat = new PrometheusMetricProfileDefinition(name, tag, help, labelNames, null);
	}

	public PrometheusMetricProfilerProcessor(String name, String tag, String help, String[] labelNames,
			double[] buckets) throws MonitorInitException {
		if (buckets == null || buckets.length == 0) {
			buckets = new double[] { 0.0001, 0.0005, 0.0009, 0.001, 0.003, 0.005, 0.007, 0.009, 0.01, 0.03, 0.05, 0.07,
					0.09, 0.1, 0.3, 0.5, 0.7, 0.9, 1, 3, 5 };
		}
		this.stat = new PrometheusMetricProfileDefinition(name, tag, help, labelNames, buckets);
	}

	@Override
	public void inc(String... labelValues) {
		stat.inc(labelValues);
	}

	@Override
	public void error(String... labelValues) {
		stat.error(labelValues);
	}

	@Override
	public void dec(String... labelValues) {
		stat.dec(labelValues);
	}

	@Override
	public AbstractTimer<Timer, Gauge, Gauge> startTimer(String... labelValues) {
		return stat.startTimer(labelValues);
	}

}
