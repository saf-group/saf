package com.future.saf.logging.basic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.event.Level;

public class LogConstant {

	// **********(1).logger defined**********//

	public static final String LOGGER_COM_FUTURE = "com.future";

	public static final String LOGGER_COM_FUTURE_SAF = "com.future.saf";

	public static final String LOGGER_ACCESS = "access";

	public static final String LOGGER_PERFORMANCE = "performance";

	public static final String LOGGER_ROOT = "root";

	public static final List<String> LOGGER_COMMON_LIST;

	static {
		List<String> list = new ArrayList<String>();
		list.add(LOGGER_COM_FUTURE);
		list.add(LOGGER_COM_FUTURE_SAF);
		list.add(LOGGER_ACCESS);
		list.add(LOGGER_PERFORMANCE);
		LOGGER_COMMON_LIST = list;
	}

	// **********(2).logger level defined**********//

	public static final List<String> LOGGER_LEVEL_LIST;

	static {
		List<String> list = new ArrayList<String>();
		list.add(Level.DEBUG.toString());
		list.add(Level.ERROR.toString());
		list.add(Level.INFO.toString());
		list.add(Level.TRACE.toString());
		list.add(Level.WARN.toString());

		list.add(Level.DEBUG.toString().toLowerCase());
		list.add(Level.ERROR.toString().toLowerCase());
		list.add(Level.INFO.toString().toLowerCase());
		list.add(Level.TRACE.toString().toLowerCase());
		list.add(Level.WARN.toString().toLowerCase());

		LOGGER_LEVEL_LIST = Collections.unmodifiableList(list);
	}
}
