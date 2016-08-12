package com.beetle.framework.persistence.nosql.redis;

import com.beetle.framework.AppRuntimeException;

public class RedisOperatorException extends AppRuntimeException {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RedisOperatorException(int errCode, String message, Throwable cause) {
		super(errCode, message, cause);
	}

	public RedisOperatorException(int errCode, String message) {
		super(errCode, message);
	}

	public RedisOperatorException(int errCode, Throwable cause) {
		super(errCode, cause);
	}

	public RedisOperatorException(String message, Throwable cause) {
		super(message, cause);
	}

	public RedisOperatorException(String message) {
		super(message);
	}

	public RedisOperatorException(Throwable cause) {
		super(cause);
	}

}
