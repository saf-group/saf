package com.future.saf.flowcontrol.sentinel.basic;

import com.alibaba.csp.sentinel.command.vo.NodeVo;
import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;
import io.prometheus.client.Gauge;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class SentinelMetricTimer {
	private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);
	private static volatile AtomicBoolean inited = new AtomicBoolean(false);

	private static final Gauge METRICS = Gauge.build().name("sentinel_metric").help("sentinel_metric")
			.labelNames("name", "id", "parentId", "type").register();

	public void init() {
		if (!inited.get()) {
			synchronized (SentinelMetricTimer.class) {
				if (!inited.get()) {
					EXECUTOR_SERVICE.scheduleAtFixedRate(this::convertMetrics, 30, 30, TimeUnit.SECONDS);
					inited.set(true);
				}
			}
		}
	}

	void convertMetrics() {
		Map<ResourceWrapper, ClusterNode> map = ClusterBuilderSlot.getClusterNodeMap();
		if (map == null) {
			return;
		}
		for (Map.Entry<ResourceWrapper, ClusterNode> entry : map.entrySet()) {
			NodeVo vo = NodeVo.fromClusterNode(entry.getKey(), entry.getValue());
			String resource = vo.getResource();
			String id = StringUtils.defaultIfEmpty(vo.getId(), "");
			String parentId = StringUtils.defaultIfEmpty(vo.getParentId(), "");
			try {
				METRICS.labels(resource, id, parentId, "averageRt").set(vo.getAverageRt());
				METRICS.labels(resource, id, parentId, "threadNum").set(vo.getThreadNum());
				METRICS.labels(resource, id, parentId, "blockQps").set(vo.getBlockQps());
				METRICS.labels(resource, id, parentId, "passQps").set(vo.getPassQps());
				METRICS.labels(resource, id, parentId, "successQps").set(vo.getSuccessQps());
				METRICS.labels(resource, id, parentId, "totalQps").set(vo.getTotalQps());
				METRICS.labels(resource, id, parentId, "exceptionQps").set(vo.getExceptionQps());
				METRICS.labels(resource, id, parentId, "oneMinuteBlock").set(vo.getOneMinuteBlock());
				METRICS.labels(resource, id, parentId, "oneMinuteException").set(vo.getOneMinuteException());
				METRICS.labels(resource, id, parentId, "oneMinutePass").set(vo.getOneMinutePass());
				METRICS.labels(resource, id, parentId, "oneMinuteTotal").set(vo.getOneMinuteTotal());
			} catch (Exception e) {
				log.error(this.getClass().getSimpleName() + " failed with exception: ", e);
			}
		}
	}
}
