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
package com.beetle.framework.persistence.access.base;

import com.beetle.framework.AppRuntimeException;

import java.sql.SQLException;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 余浩东
 * @version 1.0
 */

public class DBAccessException
// extends AppException {
		extends AppRuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5939212693118157507L;
	public String sqlState;

	public String getSqlState() {
		return sqlState;
	}

	public DBAccessException(String p0, Throwable p1) {
		super(p0, p1);
		setplus(p1);
	}

	private void setplus(Throwable p1) {
		if (p1 != null) {
			if (p1 instanceof SQLException) {
				SQLException qe = (SQLException) p1;
				this.errCode = qe.getErrorCode();
				this.sqlState = qe.getSQLState();
			}
		}
	}

	public DBAccessException(Throwable p0) {
		super(p0);
		setplus(p0);
	}

	public DBAccessException(String p0) {
		super(p0);
	}

	public DBAccessException(int errCode, String message, Throwable cause) {
		super(errCode, message, cause);
		setplus(cause);
	}

	public DBAccessException(int errCode, String message) {
		super(errCode, message);
	}

	public DBAccessException(int errCode, Throwable cause) {
		super(errCode, cause);
		setplus(cause);
	}
}
