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
package com.beetle.framework.persistence.access;

import com.beetle.framework.persistence.access.base.DBAccessException;

/**
 * <p>
 * Title: 框架设计
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: 甲壳虫科技
 * </p>
 * 
 * @author 余浩东
 * @version 1.0
 */

public class ConnectionException extends DBAccessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4628515754515336723L;

	public ConnectionException(String p0, Throwable p1) {
		super(p0, p1);
		setplus(p1);
	}

	public ConnectionException(Throwable p0) {
		super(p0);
		setplus(p0);
	}

	public ConnectionException(String p0) {
		super(p0);
	}

	private void setplus(Throwable p1) {
		if (p1 != null) {
			if (p1 instanceof DBAccessException) {
				DBAccessException qe = (DBAccessException) p1;
				this.sqlState = qe.sqlState;
				this.errCode = qe.getErrCode();
				;
			}
		}
	}
}
