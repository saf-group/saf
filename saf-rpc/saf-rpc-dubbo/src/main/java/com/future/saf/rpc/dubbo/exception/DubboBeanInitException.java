package com.future.saf.rpc.dubbo.exception;

public class DubboBeanInitException extends RuntimeException {

	private static final long serialVersionUID = -6314945162740179096L;

	public DubboBeanInitException() {
		super();
	}

	public DubboBeanInitException(String message) {
		super(message);
	}

	public DubboBeanInitException(String message, Throwable t) {
		super(message, t);
	}

}
