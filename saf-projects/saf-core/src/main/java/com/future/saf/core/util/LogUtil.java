package com.future.saf.core.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;

public class LogUtil {
	public static final String CONTEXT_COLUMN_1 = "column1";
	public static final String CONTEXT_COLUMN_2 = "column2";
	public static final String LINE = StringUtils.repeat("-", 150);
	public static final String CONFIG_PREFIX = "** SAF CONFIG - ";

	public static void putContextColumn1(String value) {
		ThreadContext.put(CONTEXT_COLUMN_1, value);
	}

	public static void putContextColumn2(String value) {
		ThreadContext.put(CONTEXT_COLUMN_2, value);
	}

	public static void clearContext() {
		ThreadContext.clearAll();
	}

	public static String line(int repeat) {
		return StringUtils.repeat("-", repeat);
	}
}
