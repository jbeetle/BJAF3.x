package com.beetle.component.security.service;

import com.beetle.framework.AppException;

public class SecurityServiceException extends AppException {

	private static final long serialVersionUID = 1L;

	public SecurityServiceException(int errCode, String message, Throwable cause) {
		super(errCode, message, cause);

	}

	public SecurityServiceException(int errCode, String message) {
		super(errCode, message);

	}

	public SecurityServiceException(int errCode, Throwable cause) {
		super(errCode, cause);

	}

	public SecurityServiceException(String message, Throwable cause) {
		super(message, cause);

	}

	public SecurityServiceException(String message) {
		super(message);

	}

	public SecurityServiceException(Throwable cause) {
		super(cause);

	}

}
