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
package com.beetle.framework.persistence.access.operator;

import com.beetle.framework.persistence.access.base.DBAccessException;

public class DBOperatorException extends DBAccessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8057507129183499565L;

	public DBOperatorException(String p0, Throwable p1) {
		super(p0, p1);
		setplus(p1);
	}

	private void setplus(Throwable p1) {
		if (p1 != null) {
			if (p1 instanceof DBAccessException) {
				DBAccessException qe = (DBAccessException) p1;
				this.errCode = qe.getErrCode();
				this.sqlState = qe.sqlState;
			}
		}
	}

	public DBOperatorException(Throwable p0) {
		super(p0);
		setplus(p0);
	}

	public DBOperatorException(String p0) {
		super(p0);
	}

	public DBOperatorException(int errCode, String message, Throwable cause) {
		super(errCode, message, cause);
		setplus(cause);
	}

	public DBOperatorException(int errCode, String message) {
		super(errCode, message);
	}

	public DBOperatorException(int errCode, Throwable cause) {
		super(errCode, cause);
		setplus(cause);
	}
}
