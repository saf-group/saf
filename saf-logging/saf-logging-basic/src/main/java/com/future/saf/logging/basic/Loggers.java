package com.future.saf.logging.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Loggers {
	private static final Logger frameworkLogger = LoggerFactory.getLogger("framework");
	private static final Logger traceLogger = LoggerFactory.getLogger("trace");
	private static final Logger errorLogger = LoggerFactory.getLogger("error");
	private static final Logger accessLogger = LoggerFactory.getLogger("access");
	private static final Logger performanceLogger = LoggerFactory.getLogger("performance");

	public static Logger getFrameworkLogger() {
		return frameworkLogger;
	}

	public static Logger getTraceLogger() {
		return traceLogger;
	}

	public static Logger getErrorLogger() {
		return errorLogger;
	}

	public static Logger getAccessLogger() {
		return accessLogger;
	}

	public static Logger getPerformanceLogger() {
		return performanceLogger;
	}
}
