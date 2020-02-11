package com.future.saf.flowcontrol.sentinel.basic.exception;

public class SentinelBeanInitException extends RuntimeException {

	private static final long serialVersionUID = -5661460542693240905L;

	public SentinelBeanInitException() {
		super();
	}

	public SentinelBeanInitException(String message) {
		super(message);
	}

	public SentinelBeanInitException(String message, Throwable t) {
		super(message, t);
	}
}
