package com.beetle.framework;

public class AppRuntimeException extends RuntimeException {
	protected int errCode = -100000;

	public int getErrCode() {
		return errCode;
	}

	public AppRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public AppRuntimeException(int errCode, String message, Throwable cause) {
		super(errCode + ":" + message, cause);
		this.errCode = errCode;
	}

	public AppRuntimeException(String message) {
		super(message);
	}

	public AppRuntimeException(int errCode, String message) {
		super(errCode + ":" + message);
		this.errCode = errCode;
	}

	public AppRuntimeException(Throwable cause) {
		super(cause);
	}

	public AppRuntimeException(int errCode, Throwable cause) {
		super(cause);
		this.errCode = errCode;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
