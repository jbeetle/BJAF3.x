package com.beetle.framework.web.cache.imp;

import com.beetle.framework.AppException;

public class NeedsRefreshException extends AppException {

	public NeedsRefreshException(int errCode, String message, Throwable cause) {
		super(errCode, message, cause);
	}

	public NeedsRefreshException(int errCode, String message) {
		super(errCode, message);
	}

	public NeedsRefreshException(int errCode, Throwable cause) {
		super(errCode, cause);
	}

	public NeedsRefreshException(String message, Throwable cause) {
		super(message, cause);
	}

	public NeedsRefreshException(String message) {
		super(message);
	}

	public NeedsRefreshException(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -9101770127266412200L;

}
