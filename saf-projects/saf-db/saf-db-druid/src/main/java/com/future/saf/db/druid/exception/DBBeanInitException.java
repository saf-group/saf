package com.future.saf.db.druid.exception;

public class DBBeanInitException extends RuntimeException {

	private static final long serialVersionUID = -5661460542693240905L;

	public DBBeanInitException() {
		super();
	}

	public DBBeanInitException(String message) {
		super(message);
	}

	public DBBeanInitException(String message, Throwable t) {
		super(message, t);
	}
}
