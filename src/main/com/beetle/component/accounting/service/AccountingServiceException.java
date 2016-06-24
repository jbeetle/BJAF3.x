package com.beetle.component.accounting.service;

import com.beetle.framework.AppException;

public class AccountingServiceException extends AppException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AccountingServiceException(int errCode, String message, Throwable cause) {
		super(errCode, message, cause);
	}

	public AccountingServiceException(int errCode, String message) {
		super(errCode, message);
	}

	public AccountingServiceException(int errCode, Throwable cause) {
		super(errCode, cause);
	}

	public AccountingServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public AccountingServiceException(String message) {
		super(message);
	}

	public AccountingServiceException(Throwable cause) {
		super(cause);
	}

}
