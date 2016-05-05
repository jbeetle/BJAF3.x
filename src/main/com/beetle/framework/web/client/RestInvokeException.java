package com.beetle.framework.web.client;

import com.beetle.framework.AppException;

public class RestInvokeException extends AppException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RestInvokeException(int errCode, String message, Throwable cause) {
		super(errCode, message, cause);
	}

	public RestInvokeException(int errCode, String message) {
		super(errCode, message);
	}

	public RestInvokeException(int errCode, Throwable cause) {
		super(errCode, cause);
	}

	public RestInvokeException(String message, Throwable cause) {
		super(message, cause);
	}

	public RestInvokeException(String message) {
		super(message);
	}

	public RestInvokeException(Throwable cause) {
		super(cause);
	}

}
