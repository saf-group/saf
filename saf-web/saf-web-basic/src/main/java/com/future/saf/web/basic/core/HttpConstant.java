package com.future.saf.web.basic.core;

public class HttpConstant {

	// http入方向bio的度量汇总
	public static final String METRIC_HTTP_IN = "Http::In";

	public static final String METRIC_HTTP_IN_PREFIX = "http:in:";

	// http出方向bio的度量汇总
	public static final String METRIC_HTTP_OUT_BIO = "Http::Out::Bio";

	// http出方向根据uri进行度量汇总的前缀
	public static final String METRIC_HTTP_OUT_BIO_URI_PREFIX = "http:out:bio:";

}
