package com.beetle.framework.appsrv.plugin;

import com.beetle.framework.AppRuntimeException;

public class PluginException extends AppRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3930978471877690987L;

	public PluginException(int errCode, String message, Throwable cause) {
		super(errCode, message, cause);
	}

	public PluginException(int errCode, String message) {
		super(errCode, message);
	}

	public PluginException(int errCode, Throwable cause) {
		super(errCode, cause);
	}

	public PluginException(String message, Throwable cause) {
		super(message, cause);
	}

	public PluginException(String message) {
		super(message);
	}

	public PluginException(Throwable cause) {
		super(cause);
	}

}
