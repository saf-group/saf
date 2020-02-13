package com.future.saf.flowcontrol.sentinel.basic;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AbstractSentinelHolder {

	private String namespace;

	private String flowRuleKey;

	private String defaultFlowRules;

	public AbstractSentinelHolder() {
	}

	public void load() {
	}
}
