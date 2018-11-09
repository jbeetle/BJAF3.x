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
		this.errCode=-1100;//如果不设置，默认-1100就是redis的
	}

	public RedisOperatorException(String message) {
		super(message);
		this.errCode=-1100;
	}

	public RedisOperatorException(Throwable cause) {
		super(cause);
		this.errCode=-1100;
	}

}
