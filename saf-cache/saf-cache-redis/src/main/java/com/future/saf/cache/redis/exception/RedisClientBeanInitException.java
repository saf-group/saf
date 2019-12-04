package com.future.saf.cache.redis.exception;

public class RedisClientBeanInitException extends RuntimeException {

	private static final long serialVersionUID = -1839463196349457418L;

	public RedisClientBeanInitException() {
		super();
	}

	public RedisClientBeanInitException(String message) {
		super(message);
	}

	public RedisClientBeanInitException(String message, Throwable t) {
		super(message, t);
	}

}
