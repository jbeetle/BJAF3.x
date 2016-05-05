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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.resource.container.ContainerUtil;
import com.beetle.framework.resource.define.Constant;
import com.beetle.framework.resource.jta.ITransaction;
import com.beetle.framework.resource.jta.JTAFactory;
import com.beetle.framework.resource.watch.WatchHelper;
import com.beetle.framework.resource.watch.WatchInfo;
import com.beetle.framework.util.cache.ICache;
import com.beetle.framework.util.cache.StrongCache;

/**
 * <p>
 * Title: ConnectionFactory
 * </p>
 * <p>
 * Description: 数据库连接工厂
 * 
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: beetlesoft
 * </p>
 * 
 * @author HenryYu
 * @version 1.0
 */
public final class ConnectionFactory {
	private static AppLogger logger = AppLogger
			.getInstance(ConnectionFactory.class);

	private final static ICache dsCache = new StrongCache();

	/**
	 * 根据数据源（参见DBConfig.xml配置）名称获取 一个数据库连接 （此方法会自动初始化相对应的数据源连接池，从池中返回连接）
	 * 
	 * 
	 * @param dataSourceName
	 * @return
	 * @throws ConnectionException
	 */
	public static Connection getConncetion(String dataSourceName)
			throws ConnectionException {
		Connection conn;
		if (WatchHelper.isNeedWatch()) {
			WatchInfo wi = WatchHelper.currentWatch();
			if (wi == null) {
				conn = getConncetion_(dataSourceName);
				return conn;
			}
			conn = (Connection) wi.getResourceByName(dataSourceName);
			if (conn != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("==get connention from cache");
				}
				return conn;
			} else {
				synchronized (driverCache) {
					conn = (Connection) wi.getResourceByName(dataSourceName);
					if (conn != null) {
						if (logger.isDebugEnabled()) {
							logger.debug("==get connention from cache2");
						}
						return conn;
					}
					conn = getConncetion_(dataSourceName);
					if (logger.isDebugEnabled()) {
						logger.debug("==create new connection");
					}
					wi.addResource(dataSourceName, conn, 1);
					if (wi.checkResourceExist(Constant.BUSINESS_CMD_TRANS)) {
						if (wi.getResourceSizeByType(1) > 1) {
							if (DBConfig.isAtomikosSupport(dataSourceName)) {
								ITransaction trans = JTAFactory
										.getTransactionFromFramework();
								wi.addResource(
										Constant.BUSINESS_CMD_TRANS,
										trans,
										Constant.COMMON_WATCHINFO_OBJECT_TYPE_TRANS);
								trans.begin();
							} else {
								try {
									throw new ConnectionException(
											"In a transaction cited more than one datasource, and the datasource is a non-xa, but this framework  transaction's manager does not support the situation, please use XA datasources");
								} finally {
									closeconn(conn);
								}
							}
						} else {// 单个数据源，使用jdbctransation
							ITransaction trans = JTAFactory
									.getTransactionFromConnection(conn);
							wi.addResource(Constant.BUSINESS_CMD_TRANS, trans,
									Constant.COMMON_WATCHINFO_OBJECT_TYPE_TRANS);
							trans.begin();
						}
					}
					return conn;
				}
			}
		} else {
			conn = getConncetion_(dataSourceName);
		}
		return conn;
	}

	/**
	 * 与getConnection方法类似，支持从此方法返回的数据库连接不参与 beetle的Command业务框架的共享连接池的优化处理
	 * 
	 * 
	 * @param dataSourceName
	 * @return
	 */
	public static Connection newDsConncetion(String dataSourceName) {
		return getConncetion_(dataSourceName);
	}

	private static Connection getConncetion_(String dataSourceName)
			throws ConnectionException {
		int flag = Integer.parseInt(DBConfig.getFrameworkDS(dataSourceName,
				DBConfig.use_mode));
		if (logger.isDebugEnabled()) {
			logger.debug(dataSourceName + "'s[use-mode]:" + flag);
			logger.debug("==create new connention:" + dataSourceName);
		}
		Connection conn;
		switch (flag) {
		case 2:
			conn = fromFramework(dataSourceName);
			break;
		case 3:
			conn = fromDebug(dataSourceName);
			break;
		case 1:
			conn = fromContain(dataSourceName);
			break;
		default: // 2
			conn = fromFramework(dataSourceName);
		}
		return conn;
	}

	private static Connection fromDebug(String dataSourceName) {
		return newDriverConn(dataSourceName);
	}

	private static Connection fromFramework(String dataSourceName)
			throws ConnectionException {
		IConnPool pool;
		if (dsCache.containsKey(dataSourceName)) {
			pool = (IConnPool) dsCache.get(dataSourceName);
			return pool.getConnection();
		} else {
			pool = getOtherPool(dataSourceName);
			return pool.getConnection();
		}
	}

	private static Connection fromContain(String dataSourceName)
			throws ConnectionException {
		DataSource ds = (DataSource) dsCache.get(dataSourceName);
		if (ds == null) {
			synchronized (dsCache) {
				if (!dsCache.containsKey(dataSourceName)) {
					ds = initDatasource(dataSourceName);
				}
			}
		}
		Connection conn;
		try {
			if (ds == null) {// deal findbugs nullpoint alert!
				throw new ConnectionException("");
			}
			conn = ds.getConnection();
		} catch (Exception ex) {
			synchronized (dsCache) {
				ds = initDatasource(dataSourceName);
			}
			try {
				conn = ds.getConnection();
			} catch (SQLException e) {
				logger.error(e);
				throw new ConnectionException(e);
			}
		}
		return conn;
	}

	public static void initializeAllDataSources() {
		List<String> dss = DBConfig.getAllDatasourcename();
		if (dss != null && !dss.isEmpty()) {
			for (String ds : dss) {
				Connection conn = null;
				try {
					conn = getConncetion(ds);
				} finally {
					if (conn != null) {
						closeconn(conn);
					}
				}
			}
		}
	}

	private static DataSource initDatasource(String dataSourceName) {
		String jndiname = DBConfig.getFrameworkDS(dataSourceName,
				DBConfig.jndi_name);
		if (jndiname == null || jndiname.trim().length() == 0) {
			throw new AppRuntimeException(dataSourceName
					+ "'s [jndi-name] can not be null,must be setted!");
		}
		Context ctx = null;
		try {
			ctx = ContainerUtil.getLocalEJBContainerContext();
			DataSource ds = (DataSource) ctx.lookup(jndiname);
			dsCache.put(dataSourceName, ds);
			return ds;
		} catch (NamingException ename) {
			if (ctx != null) {// �Ա����쳣���³�ʼ��context
				try {
					ctx.close();
				} catch (NamingException e) {
					logger.error(e);
				} finally {
					ctx = null;
				}
			}
			ContainerUtil.clearLocalEJBContexts();// ����context����
			throw new ConnectionException(ename);
		}
	}

	private static IConnPool getOtherPool(String dataSourceName) {
		IConnPool GeneConnPool = (IConnPool) dsCache.get(dataSourceName);
		if (GeneConnPool == null) {
			synchronized (dsCache) {
				if (!dsCache.containsKey(dataSourceName)) {
					int minsize = Integer.parseInt(DBConfig.getFrameworkDS(
							dataSourceName, "pool-minsize"));
					int maxsize = Integer.parseInt(DBConfig.getFrameworkDS(
							dataSourceName, "pool-maxsize"));
					String drvName = DBConfig.getFrameworkDS(dataSourceName,
							"driver-class");
					String url = DBConfig.getFrameworkDS(dataSourceName,
							"connection-url");
					String user = DBConfig.getFrameworkDS(dataSourceName,
							"user-name");
					String passwd = DBConfig
							.decodeDatasourcePassword(dataSourceName);
					final String impStr;
					try {
						impStr = DBConfig.getFrameworkDS(dataSourceName,
								DBConfig.pool_imp);
						GeneConnPool = (IConnPool) Class.forName(impStr)
								.newInstance();
						GeneConnPool.setConURL(url);
						GeneConnPool.setDriverName(drvName);
						GeneConnPool.setMax(maxsize);
						GeneConnPool.setMin(minsize);
						GeneConnPool.setUsername(user);
						GeneConnPool.setPassword(passwd);
						GeneConnPool.setTestSql(DBConfig.getFrameworkDS(
								dataSourceName, "test-sql"));
						GeneConnPool.start();
						dsCache.put(dataSourceName, GeneConnPool);
						logger.info(dataSourceName + "'s " + impStr
								+ " Started!");
					} catch (Exception e) {
						throw new ConnectionException(e);
					}
				} else {
					GeneConnPool = (IConnPool) dsCache.get(dataSourceName);
				}
			}
		}
		return GeneConnPool;
	}

	private static final ICache driverCache = new StrongCache();

	/**
	 * 根据数据源（参见DBConfig.xml配置）名称获取 一个数据库连接
	 * (此方法不会初始化对应的连接池，每次都是通过driver创建一个新的数据库连接返回)
	 * 
	 * @param dataSourceName
	 * @return
	 */
	public static Connection newDriverConn(String dataSourceName) {
		try {
			/*
			 * Driver drv = (Driver) Class.forName(
			 * DBConfig.getFrameworkDS(dataSourceName, "driver-class"))
			 * .newInstance(); DriverManager.registerDriver(drv);
			 */
			if (!driverCache.containsKey(dataSourceName)) {
				Class.forName(DBConfig.getFrameworkDS(dataSourceName,
						"driver-class"));
				driverCache
						.put(dataSourceName, DBConfig.getFrameworkDS(
								dataSourceName, "driver-class"));
			}
			return DriverManager.getConnection(
					DBConfig.getFrameworkDS(dataSourceName, "connection-url"),
					DBConfig.getFrameworkDS(dataSourceName, "user-name"),
					DBConfig.decodeDatasourcePassword(dataSourceName));
		} catch (Exception e) {
			driverCache.remove(dataSourceName);
			throw new ConnectionException(e.getMessage(), e);
		}
	}

	/**
	 * 回收相关资源
	 * 
	 * @param conn
	 *            Connection
	 * @param stm
	 *            Statement
	 * @param res
	 *            ResultSet
	 * @throws ConnectionException
	 */
	public static void closeAll(Connection conn, Statement stm, ResultSet res)
			throws ConnectionException {
		try {
			if (res != null) {
				res.close();
			}
			if (stm != null) {
				stm.close();
			}
		} catch (SQLException ex) {
			throw new ConnectionException(ex);
		} finally {
			if (conn != null) {
				if (!WatchHelper.isNeedWatch()) {
					try {
						conn.close();
					} catch (SQLException e) {
						throw new ConnectionException(e);
					}
				} else {
					WatchInfo wi = WatchHelper.currentWatch();
					if (wi == null) {
						try {
							conn.close();
						} catch (SQLException e) {
							throw new ConnectionException(e);
						}
					}
				}
			}
		}
	}

	private static void closeconn(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
		} finally {
			conn = null;
		}
	}

	/**
	 * 关闭数据库连接
	 * 
	 * 
	 * @param conn
	 *            Connection
	 * @throws ConnectionException
	 */
	public static void closeConnection(Connection conn)
			throws ConnectionException {
		closeAll(conn, null, null);
	}

}
