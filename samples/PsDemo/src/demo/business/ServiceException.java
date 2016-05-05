package demo.business;

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

	public ServiceException(int errCode, String message, Throwable cause) {
		super(errCode, message, cause);
	}

	public ServiceException(int errCode, String message) {
		super(errCode, message);
	}

	public ServiceException(int errCode, Throwable cause) {
		super(errCode, cause);
	}
}
