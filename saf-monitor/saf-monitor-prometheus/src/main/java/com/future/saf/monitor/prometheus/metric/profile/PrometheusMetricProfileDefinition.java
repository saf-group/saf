package com.future.saf.monitor.prometheus.metric.profile;

import com.future.saf.monitor.basic.AbstractMetricProfileDefinition;
import com.future.saf.monitor.basic.AbstractTimer;
import com.future.saf.monitor.exception.MonitorInitException;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.Histogram.Timer;
import lombok.Getter;

@Getter
public class PrometheusMetricProfileDefinition extends AbstractMetricProfileDefinition {

	private Counter error;

	private Histogram histogram;

	private Gauge min;

	private Gauge max;

	private Gauge concurrent;

	PrometheusMetricProfileDefinition(String name, String tag, String help, String[] labelNames, double[] buckets)
			throws MonitorInitException {
		this.error = Counter.build().name(name + "_err_counter").help(help).labelNames(labelNames).register();

		final Histogram.Builder histogramBuilder = Histogram.build().name(name + "_latency_seconds_histogram")
				.help(help).labelNames(labelNames);
		if (buckets != null) {
			histogramBuilder.buckets(buckets);
		}
		this.histogram = histogramBuilder.register();

		this.min = Gauge.build().name(name + "_min_gauge").help(help).labelNames(labelNames).register();

		this.max = Gauge.build().name(name + "_max_gauge").help(help).labelNames(labelNames).register();

		this.concurrent = Gauge.build().name(name + "_concurrent_gauge").help(help).labelNames(labelNames).register();

		super.add(name, this);
	}

	void error(String... labelValues) {
		if (error == null) {
			return;
		}
		error.labels(labelValues).inc();
	}

	AbstractTimer<Timer, Gauge, Gauge> startTimer(String... labelValues) {
		Timer timer = this.histogram.labels(labelValues).startTimer();
		return new PrometheusTimer(timer, max, min);
	}

	public void inc(String... labelValues) {
		concurrent.labels(labelValues).inc();
	}

	public void dec(String... labelValues) {
		concurrent.labels(labelValues).dec();
	}

	public void reset() {
		min.clear();
		max.clear();
	}

}
