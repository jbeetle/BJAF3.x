/*
 * BJAF - Beetle J2EE Application Framework
 * 甲壳虫J2EE企业应用开发框架
 * 版权所有2003-2015 余浩东 (www.beetlesoft.net)
 * 
 * 这是一个免费开源的软件，您必须在
 *<http://www.apache.org/licenses/LICENSE-2.0>
 *协议下合法使用、修改或重新发布。
 *
 * 感谢您使用、推广本框架，若有建议或问题，欢迎您和我联系。
 * 邮件： <yuhaodong@gmail.com/>.
 */
package com.beetle.framework;

/**
 * Copyright: Copyright (c) 2003 Company: BeetleSoft
 * 
 * @author 余浩东
 * 
 * @version 1.0
 */

public class AppException extends Exception {
	protected int errCode = -100000;

	public int getErrCode() {
		return errCode;
	}

	public AppException(String message, Throwable cause) {
		super(message, cause);
	}

	public AppException(int errCode, String message, Throwable cause) {
		super(errCode + ":" + message, cause);
		this.errCode = errCode;
	}

	public AppException(String message) {
		super(message);
	}

	public AppException(int errCode, String message) {
		super(errCode + ":" + message);
		this.errCode = errCode;
	}

	public AppException(Throwable cause) {
		super(cause);
	}

	public AppException(int errCode, Throwable cause) {
		super(cause);
		this.errCode = errCode;
	}

	private static final long serialVersionUID = 1L;

}
