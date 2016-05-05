package demo.business.validate;

import com.beetle.framework.AppException;

public class ValidateException extends AppException {

	public ValidateException(int errCode, String message, Throwable cause) {
		super(errCode, message, cause);
	}

	public ValidateException(int errCode, String message) {
		super(errCode, message);
	}

	public ValidateException(int errCode, Throwable cause) {
		super(errCode, cause);
	}

	public ValidateException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidateException(String message) {
		super(message);
	}

	public ValidateException(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
