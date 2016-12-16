package com.beetle.framework;

public class AppRuntimeException extends RuntimeException {
	protected int errCode = -100000;
	protected String errMsg = "err";

	public int getErrCode() {
		return errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public AppRuntimeException(String message, Throwable cause) {
		super(message, cause);
		this.errMsg = message;
	}

	public AppRuntimeException(int errCode, String message, Throwable cause) {
		super(message, cause);
		this.errCode = errCode;
		this.errMsg = message;
	}

	public AppRuntimeException(String message) {
		super(message);
		this.errMsg = message;
	}

	public AppRuntimeException(int errCode, String message) {
		super(message);
		this.errCode = errCode;
		this.errMsg = message;
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
