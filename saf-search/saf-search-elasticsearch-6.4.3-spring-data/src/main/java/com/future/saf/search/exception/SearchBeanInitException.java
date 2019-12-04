package com.future.saf.search.exception;

public class SearchBeanInitException extends RuntimeException {

	private static final long serialVersionUID = -9052732024863611062L;

	public SearchBeanInitException() {
		super();
	}

	public SearchBeanInitException(String message) {
		super(message);
	}

	public SearchBeanInitException(String message, Throwable t) {
		super(message, t);
	}

}
