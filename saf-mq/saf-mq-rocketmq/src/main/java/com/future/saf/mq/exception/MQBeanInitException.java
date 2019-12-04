package com.future.saf.mq.exception;

public class MQBeanInitException extends RuntimeException {

	private static final long serialVersionUID = -5508343692780706283L;

	public MQBeanInitException() {
		super();
	}

	public MQBeanInitException(String message) {
		super(message);
	}

	public MQBeanInitException(String message, Throwable t) {
		super(message, t);
	}

}
