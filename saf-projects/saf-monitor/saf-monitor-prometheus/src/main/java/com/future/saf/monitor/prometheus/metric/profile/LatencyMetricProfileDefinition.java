package com.future.saf.monitor.prometheus.metric.profile;

import com.future.saf.monitor.basic.AbstractMetricProfileDefinition;
import com.future.saf.monitor.exception.MonitorInitException;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.Histogram.Timer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class LatencyMetricProfileDefinition extends AbstractMetricProfileDefinition {

	private Counter error;

	private Histogram histogram;

	private Timer timer;

	private Gauge min;

	private Gauge max;

	private Gauge concurrent;

	LatencyMetricProfileDefinition(String name, String tag, String help, String[] labelNames, double[] buckets)
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

//	void observeDurationInSeconds(long seconds, TimeUnit timeUnit, String... labelNames) {
//		// final double seconds =
//		// PrometheusTimeUtil.convertNanosToSeconds(timeUnit.toNanos(value));
//		setMax(seconds, labelNames);
//		setMin(seconds, labelNames);
//		histogram.labels(labelNames).observe(seconds);
//	}
//
//	double observeDurationInSeconds(Timer timer) {
//		final double seconds = timer.observeDuration();
//		setMin(seconds, labelNames);
//		setMax(seconds, labelNames);
//		return seconds;
//	}

	Timer startTimer(String... labelValues) {
		this.timer = this.histogram.labels(labelValues).startTimer();
		return this.timer;
	}

	double observeDuration(String... labelValues) {
		if (timer == null) {
			log.error("LatencyMetricProfileStat.observeDuration failed, timer is null.");
			return -1;
		}
		final double seconds = timer.observeDuration();
		LatencyMetricProfileDefinition.this.setMin(seconds, labelValues);
		LatencyMetricProfileDefinition.this.setMax(seconds, labelValues);
		return seconds;
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

	private void setMax(double seconds, String... labelValues) {
		final Gauge.Child maxWithlabelNames = max.labels(labelValues);
		if (seconds > maxWithlabelNames.get()) {
			maxWithlabelNames.set(seconds);
		}
	}

	private void setMin(double seconds, String... labelValues) {
		final Gauge.Child minWithlabelNames = min.labels(labelValues);
		if (seconds < minWithlabelNames.get() || minWithlabelNames.get() == 0D) {
			minWithlabelNames.set(seconds);
		}
	}
}
