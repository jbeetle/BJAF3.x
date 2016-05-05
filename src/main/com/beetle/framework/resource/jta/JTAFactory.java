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
import com.beetle.framework.persistence.access.ConnectionFactory;
import com.beetle.framework.persistence.access.DBConfig;
import com.beetle.framework.resource.container.ContainerUtil;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;
import java.sql.Connection;

/**
 * <p>
 * Title: BeetleSoft Framework
 * </p>
 * 
 * <p>
 * Description: JTA工厂
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: 甲壳虫软件
 * 
 * </p>
 * 
 * @author 余浩东
 * 
 * @version 1.0
 */
public class JTAFactory {
	private static AppLogger logger = AppLogger.getInstance(JTAFactory.class);

	private static boolean f = true;

	public static ITransaction getTransactionFromMock() {
		return MockTransaction.getInstance();
	}

	/**
	 * 返回一个系统事务
	 * 
	 * 
	 * @throws JTAException
	 * @return ITransaction
	 */
	public static ITransaction getTransactionFromContainer()
			throws JTAException {
		UserTransaction ut = null;
		try {
			if (f) {
				ut = (UserTransaction) ContainerUtil.getLocalEJBContainerContext()
						.lookup("java:comp/UserTransaction");
				if (logger.isDebugEnabled()) {
					logger.debug("get 'UserTransaction' by jndi[java:comp/UserTransaction]");
				}
			} else {
				ut = (UserTransaction) ContainerUtil.getLocalEJBContainerContext()
						.lookup("javax.transaction.UserTransaction");
				if (logger.isDebugEnabled()) {
					logger.debug("get 'UserTransaction' by jndi[javax.transaction.UserTransaction]");
				}
			}
		} catch (NamingException e) {
			if (ut == null) {
				try {
					ut = (UserTransaction) ContainerUtil
							.getLocalEJBContainerContext().lookup(
									"javax.transaction.UserTransaction"); // for
					// weblogic
					if (logger.isDebugEnabled()) {
						logger.debug("get 'UserTransaction' by jndi[javax.transaction.UserTransaction]");
					}
					f = false;
				} catch (NamingException ex) {
					logger.error(ex);
					throw new JTAException("获取系统JTA失败", ex);
				}
			}
		}
		JTATransaction jta = new JTATransaction(ut);
		return jta;
	}

	/**
	 * 返回一个系统事务
	 * 
	 * 
	 * @param context
	 *            Context
	 * @throws JTAException
	 * @return ITransaction
	 */
	public static ITransaction getTransaction(Context context)
			throws JTAException {
		UserTransaction ut;
		try {
			ut = (UserTransaction) context.lookup("java:comp/UserTransaction");
			JTATransaction jta = new JTATransaction(ut);
			return jta;
		} catch (NamingException e) {
			throw new JTAException("获取系统JTA失败", e);
		}
	}

	public static ITransaction getTransactionFromFramework() {
		UserTransaction ut = JTAService.getUserTransaction();
		ITransaction it = new JTATransaction(ut);
		if (logger.isDebugEnabled()) {
			logger.debug("get 'ITransaction' by JTATransaction");
		}
		return it;
	}

	public static ITransaction getTransactionFromConnection(Connection conn) {
		if (logger.isDebugEnabled()) {
			logger.debug("get 'ITransaction' by JDBCTransaction");
		}
		return new JDBCTransaction(conn);
	}

	/**
	 * 返回框架事务对象 如果框架默认数据源的类型为非xa的话，采取jdbc事务，而且此事务的作用范围只在默认数据源本身；
	 * 如果是xa数据源则采取，jotm事务。
	 * 
	 * 
	 * @return
	 */
	public static ITransaction getTransactionFromFramework(String dsname) {
		ITransaction it;
		boolean xaf = DBConfig.isAtomikosSupport(dsname);
		if (!xaf) {// 非XA数据源，而且只有一个数据源的时候采取jdbc事务
			it = new JDBCTransaction(ConnectionFactory.getConncetion(dsname));
			if (logger.isDebugEnabled()) {
				logger.debug("get 'ITransaction' by JDBCTransaction");
			}
		} else {
			UserTransaction ut = JTAService.getUserTransaction();
			it = new JTATransaction(ut);
			if (logger.isDebugEnabled()) {
				logger.debug("get 'ITransaction' by JTATransaction");
			}
		}
		return it;
	}

}
