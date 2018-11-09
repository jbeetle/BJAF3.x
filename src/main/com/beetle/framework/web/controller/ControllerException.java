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
package com.beetle.framework.web.controller;

import com.beetle.framework.AppException;

public class ControllerException extends AppException {

	public ControllerException(int errCode, String message, Throwable cause) {
		super(errCode, message, cause);
	}

	public ControllerException(int errCode, String message) {
		super(errCode, message);
	}

	public ControllerException(int errCode, Throwable cause) {
		super(errCode, cause);
	}

	private static final long serialVersionUID = 1L;

	public ControllerException(String message) {
		super(message);
		this.errCode=-4100;
	}

	public ControllerException(Throwable cause) {
		super(cause);
		this.errCode=-4100;
	}

	public ControllerException(String message, Throwable cause) {
		super(message, cause);
		this.errCode=-4100;
	}
}
