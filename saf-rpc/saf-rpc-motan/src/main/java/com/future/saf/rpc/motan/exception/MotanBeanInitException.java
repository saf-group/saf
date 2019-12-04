package com.future.saf.rpc.motan.exception;

public class MotanBeanInitException extends RuntimeException {

	private static final long serialVersionUID = -1228476107854454975L;

	public MotanBeanInitException() {
		super();
	}

	public MotanBeanInitException(String message) {
		super(message);
	}

	public MotanBeanInitException(String message, Throwable t) {
		super(message, t);
	}

}
