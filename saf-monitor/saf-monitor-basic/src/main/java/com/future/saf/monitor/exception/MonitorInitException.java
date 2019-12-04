package com.future.saf.monitor.exception;

public class MonitorInitException extends RuntimeException {

	private static final long serialVersionUID = -4053822561603030766L;

	public MonitorInitException() {
		super();
	}

	public MonitorInitException(String message) {
		super(message);
	}

	public MonitorInitException(String message, Throwable t) {
		super(message, t);
	}

}
