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
package com.beetle.framework.resource.dic;

import com.beetle.framework.AppRuntimeException;

public class DependencyInjectionException extends AppRuntimeException {

	private static final long serialVersionUID = 1L;

	public DependencyInjectionException(int errCode, String message, Throwable cause) {
		super(errCode, message, cause);
	}

	public DependencyInjectionException(int errCode, String message) {
		super(errCode, message);
	}

	public DependencyInjectionException(int errCode, Throwable cause) {
		super(errCode, cause);
	}

	public DependencyInjectionException(String message, Throwable cause) {
		super(message, cause);
		this.errCode = -3100;
	}

	public DependencyInjectionException(String message) {
		super(message);
		this.errCode = -3100;
	}

	public DependencyInjectionException(Throwable cause) {
		super(cause);
		this.errCode = -3100;
	}

}
