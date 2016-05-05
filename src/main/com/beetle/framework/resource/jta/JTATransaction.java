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

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 * <p>
 * Title: BeetleSoft Framework
 * </p>
 * <p>
 * Description: JTA事务接口实现类
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: 甲壳虫软件
 * </p>
 * 
 * @author 余浩东
 * 
 * @version 1.0
 */

public class JTATransaction implements ITransaction {
	private UserTransaction utx;
	private static AppLogger logger = AppLogger
			.getInstance(JTATransaction.class);

	public JTATransaction(UserTransaction uts) {
		this.utx = uts;
	}

	public void begin() throws JTAException {
		try {
			utx.begin();
			if (logger.isDebugEnabled()) {
				logger.debug("JTATransaction:begin");
			}
		} catch (Exception e) {
			logger.error(e);
			throw new JTAException(e);
		}
	}

	public void commit() throws JTAException {
		try {
			utx.commit();
			if (logger.isDebugEnabled()) {
				logger.debug("JTATransaction:commit");
			}

		} catch (Exception e) {
			logger.error(e);
			throw new JTAException(e);
		}
	}

	public void rollback() throws JTAException {
		try {
			utx.rollback();
			if (logger.isDebugEnabled()) {
				logger.debug("JTATransaction:rollback");
			}
		} catch (Exception e) {
			logger.error(e);
			throw new JTAException(e);
		}
	}

	public void setRollbackOnly() throws JTAException {
		try {
			utx.setRollbackOnly();
		} catch (Exception e) {
			logger.error(e);
			throw new JTAException(e);
		}
	}

	public int getStatus() throws JTAException {
		try {
			return utx.getStatus();
		} catch (SystemException e) {
			throw new JTAException(e);
		}
	}

	public void setTransactionTimeout(int timeout) throws JTAException {
		try {
			utx.setTransactionTimeout(timeout);
		} catch (SystemException e) {
			throw new JTAException(e);
		}
	}

}
