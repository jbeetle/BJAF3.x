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
package com.beetle.framework.persistence.pagination;

import com.beetle.framework.persistence.access.operator.DBOperatorException;

/**
 * <p>
 * Title: Beetle Persistence Framework
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: BeetleSoft
 * </p>
 * 
 * @author HenryYu (yuhaodong@gmail.com)
 * @version 1.0
 */
public class PaginationException extends DBOperatorException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6328494157591017677L;

	public PaginationException(String message) {
		super(message);
		this.errCode=-1060;
	}

	public PaginationException(Throwable cause) {
		super(cause);
		this.errCode=-1060;
	}

	public PaginationException(String message, Throwable cause) {
		super(message, cause);
		this.errCode=-1060;
	}

	public PaginationException(int errCode, String message, Throwable cause) {
		super(errCode, message, cause);
	}

	public PaginationException(int errCode, String message) {
		super(errCode, message);
	}

	public PaginationException(int errCode, Throwable cause) {
		super(errCode, cause);
	}

}
