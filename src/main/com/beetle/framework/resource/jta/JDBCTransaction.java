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
package com.beetle.framework.resource.jta;

import com.beetle.framework.log.AppLogger;

import javax.transaction.Status;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * <p>
 * Title: Beetle业务逻辑框架
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company: 甲壳虫软件
 * </p>
 * 
 * @author 余浩东（yuhaodong@gmail.com）
 * 
 * @version 1.0
 */
public class JDBCTransaction implements ITransaction {
	private Connection conn;
	private int status = Status.STATUS_NO_TRANSACTION;
	private static AppLogger logger = AppLogger
			.getInstance(JDBCTransaction.class);

	private boolean isTranFlag;

	public JDBCTransaction(Connection conn) {
		this.conn = conn;
		this.isTranFlag = getSupport();
	}

	private boolean getSupport() {
		try {
			int i = conn.getTransactionIsolation();
			if (i == Connection.TRANSACTION_NONE) {
				return false;
			} else {
				return true;
			}
		} catch (SQLException e) {
			throw new RuntimeException("getTransactionIsolation err", e);
		}
	}

	/**
	 * 开始一个事务
	 * 
	 * 
	 * @throws JTAException
	 * @todo Implement this
	 *       com.beetle.framework.resource.jta.ITransaction method
	 */
	public void begin() throws JTAException {
		status = Status.STATUS_ACTIVE;
		try {
			this.conn.setAutoCommit(false);
			if (logger.isDebugEnabled()) {
				logger.debug("JDBCTransaction:begin");
			}
		} catch (SQLException se) {
			throw new JTAException(se);
		}
	}

	/**
	 * 提交事务
	 * 
	 * @throws JTAException
	 * @todo Implement this
	 *       com.beetle.framework.resource.jta.ITransaction method
	 */
	public void commit() throws JTAException {
		try {
			if (conn != null) {
				conn.commit();
				if (logger.isDebugEnabled()) {
					logger.debug("JDBCTransaction:commit");
				}
			}
			status = Status.STATUS_COMMITTED;
		} catch (SQLException e) {
			throw new JTAException(e);
		}
	}

	/**
	 * 返回事务目前的状态
	 * 
	 * 
	 * @throws JTAException
	 * @return int
	 * @todo Implement this
	 *       com.beetle.framework.resource.jta.ITransaction method
	 */
	public int getStatus() throws JTAException {
		return status;
	}

	public boolean isIsTranFlag() {
		return isTranFlag;
	}

	/**
	 * 回滚事务
	 * 
	 * @throws JTAException
	 * @todo Implement this
	 *       com.beetle.framework.resource.jta.ITransaction method
	 */
	public void rollback() throws JTAException {
		try {
			if (conn != null) {
				conn.rollback();
				if (logger.isDebugEnabled()) {
					logger.debug("JDBCTransaction:rollback");
				}
			}
			status = Status.STATUS_ROLLEDBACK;
		} catch (SQLException e) {
			throw new JTAException(e);
		}
	}

	/**
	 * 设置回滚只读
	 * 
	 * @throws JTAException
	 * @todo Implement this
	 *       com.beetle.framework.resource.jta.ITransaction method
	 */
	public void setRollbackOnly() throws JTAException {
	}

	/**
	 * 设置事务超时时间
	 * 
	 * @param timeout
	 *            秒
	 * 
	 * @throws JTAException
	 * @todo Implement this
	 *       com.beetle.framework.resource.jta.ITransaction method
	 */
	public void setTransactionTimeout(int timeout) throws JTAException {

	}
}
