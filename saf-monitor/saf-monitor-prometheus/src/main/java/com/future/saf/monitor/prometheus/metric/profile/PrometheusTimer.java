package com.future.saf.monitor.prometheus.metric.profile;

import com.future.saf.monitor.basic.AbstractTimer;

import io.prometheus.client.Histogram.Timer;
import lombok.extern.slf4j.Slf4j;
import io.prometheus.client.Gauge;

@Slf4j
public class PrometheusTimer extends AbstractTimer<Timer, Gauge, Gauge> {

	private Timer timer;
	private Gauge max;
	private Gauge min;

	public PrometheusTimer(Timer timer, Gauge max, Gauge min) {
		super(timer, max, min);
		this.timer = timer;
		this.max = max;
		this.min = min;
	}

	@Override
	public double observeDuration(String... labelValues) {
		if (timer == null) {
			log.error("LatencyMetricProfileStat.observeDuration failed, timer is null.");
			return -1;
		}
		final double seconds = timer.observeDuration();
		setMin(seconds, labelValues);
		setMax(seconds, labelValues);
		return seconds;
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
