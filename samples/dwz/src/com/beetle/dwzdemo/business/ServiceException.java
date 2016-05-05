package com.beetle.dwzdemo.business;

import com.beetle.framework.persistence.access.operator.DBOperatorException;

public class ServiceException extends DBOperatorException {
	private static final long serialVersionUID = 1L;

	public ServiceException(String p0, Throwable p1) {
		super(p0, p1);
	}

	public ServiceException(String p0) {
		super(p0);
	}

	public ServiceException(Throwable p0) {
		super(p0);
	}

}
