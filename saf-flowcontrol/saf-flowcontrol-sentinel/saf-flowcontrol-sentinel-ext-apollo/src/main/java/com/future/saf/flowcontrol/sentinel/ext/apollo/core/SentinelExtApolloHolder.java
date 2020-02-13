package com.future.saf.flowcontrol.sentinel.ext.apollo.core;

import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.apollo.ApolloDataSource;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.future.saf.flowcontrol.sentinel.basic.AbstractSentinelHolder;

import java.util.List;

public class SentinelExtApolloHolder extends AbstractSentinelHolder {

	private ReadableDataSource<String, List<FlowRule>> flowRuleDataSource;

	@Override
	public void load() {
		this.flowRuleDataSource = new ApolloDataSource<>(super.getNamespace(), super.getFlowRuleKey(),
				super.getDefaultFlowRules(), source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
				}));
		FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
	}

}
