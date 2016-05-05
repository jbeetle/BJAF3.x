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
package com.beetle.framework.persistence.access.datasource;

import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.persistence.access.IConnPool;
import com.beetle.framework.util.OtherUtil;
import com.beetle.framework.util.UUIDGenerator;
import com.beetle.framework.util.thread.Counter;
import com.beetle.framework.util.thread.RunWrapper;

import java.sql.*;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public final class DriverPool implements IConnPool {
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public void setConURL(String conURL) {
		this.conURL = conURL;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public void setMax(int max) {
		this.max = max;
	}

	private static class PooledConnection {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PooledConnection other = (PooledConnection) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}

		public String getId() {
			return id;
		}

		private Connection con;
		private boolean available;
		private String id;
		private long lasttime;

		PooledConnection(Connection con, boolean available) {
			this.con = con;
			this.available = available;
			this.id = UUIDGenerator.generateUUID();
			this.lasttime = 0;
		}

		boolean isTimeToTest() {
			if (System.currentTimeMillis() - lasttime > 15 * 1000) {
				return true;
			} else {
				return false;
			}
		}

		void setLasttime(long lasttime) {
			this.lasttime = lasttime;
		}

		Connection getConnection() {
			return con;
		}

		boolean isAvailable() {
			return available;
		}

		void setAvailable(boolean available) {
			this.available = available;
		}
	}

	private String driverName;
	private String conURL;
	private String username;
	private String password;
	private int min, max;
	private LinkedBlockingQueue<PooledConnection> conPool;
	private Driver drv;
	private AppLogger logger;
	private PoolMonitor monitor;
	private Counter counter;
	private String testSql;

	public DriverPool() {
		this(0, 0, "", "", "", "", "");
	}

	/**
	 * @param max
	 * @param min
	 * @param driverName
	 * @param conURL
	 * @param username
	 * @param password
	 */
	public DriverPool(int max, int min, String driverName, String conURL,
			String username, String password, String testSql) {
		this.logger = AppLogger.getInstance(this.getClass());
		this.driverName = driverName;
		this.conURL = conURL;
		this.username = username;
		this.password = password;
		this.max = max;
		this.min = min;
		this.testSql = testSql;
		this.counter = new Counter();
	}

	private final ReentrantLock addLock = new ReentrantLock();

	private void addConnectionsToPool(int numPooledCon) {
		addLock.lock();
		try {
			if (drv == null) {
				drv = (Driver) Class.forName(driverName).newInstance();
				DriverManager.registerDriver(drv);
			}
			for (int i = 0; i < numPooledCon; i++) {
				Connection con = DriverManager.getConnection(conURL, username,
						password);
				PooledConnection pc = new PooledConnection(con, true);
				conPool.put(pc);
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			addLock.unlock();
		}
	}

	// synchronized
	private void moniPool() {
		try {
			checkConnAvailable();
			int size = conPool.size();
			int y = max - size;
			long cv = counter.getCurrentValue();
			if (cv > min && cv <= max) {
				this.addConnectionsToPool(OtherUtil.randomInt(1, y));
			} else if (cv > max) {
				this.addConnectionsToPool(y);
			} else {
				if (size < min) {
					this.addConnectionsToPool(min - size);
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("moniPool...cursize:" + size);
				logger.debug("current request:" + counter.getCurrentValue());
			}
		} catch (Throwable e) {
			logger.error("moniPool", e);
		}
	}

	private void testSqlOK(Connection con, String sql) throws SQLException {
		Statement st = con.createStatement();
		boolean f;
		try {
			f = st.execute(sql);
			if (f) {
				st.getResultSet().close();
			}
		} finally {
			st.close();
		}
	}

	private void checkConnAvailable() {
		Iterator<PooledConnection> it = conPool.iterator();
		while (it.hasNext()) {
			PooledConnection pc = it.next();
			if (pc == null) {
				it.remove();
				continue;
			}
			Connection con = pc.getConnection();
			try {
				if (con == null || con.isClosed()) {
					pc.setAvailable(false);
				} else {
					if (testSql != null && testSql.length() > 0
							&& pc.isTimeToTest()) {
						testSqlOK(con, testSql);
						pc.setLasttime(System.currentTimeMillis());
						if (logger.isDebugEnabled()) {
							logger.debug("con[" + pc.getId() + "]test OK");
						}
					}
				}
			} catch (Exception e) {
				pc.setAvailable(false);
				closeConnection(con);
			}
		}
	}

	private final ReentrantLock getLock = new ReentrantLock();

	/**
	 * Gets available connection from the pool
	 * 
	 * @return Connection
	 */
	public Connection getConnection() {
		counter.increase();
		getLock.lock();
		try {
			return getfrompool();
		} catch (InterruptedException e) {
			throw new AppRuntimeException("getConnection err", e);
		} finally {
			getLock.unlock();
		}
	}

	private Connection getfrompool() throws InterruptedException {
		while (true) {
			PooledConnection pc = conPool.take();
			if (pc.isAvailable()) {
				pc.setAvailable(false); // 标注此连接已经被使用
				Connection conn = pc.getConnection();
				try {
					if (conn.isClosed()) {
						if (logger.isDebugEnabled()) {
							logger.debug("1-remove connection:" + pc.getId()
									+ " from pool");
						}
						counter.decrease();
					} else {
						conPool.put(pc);
						return conn;
					}
				} catch (Exception e) {
					counter.decrease();
					closeConnection(conn);
				}
			} else {
				Connection conn = pc.getConnection();
				try {
					if (conn != null && !conn.isClosed()) {
						conPool.put(pc);
					} else {
						if (logger.isDebugEnabled()) {
							logger.debug("2-remove connection:" + pc.getId()
									+ " from pool");
						}
						counter.decrease();
					}
				} catch (SQLException e) {
					counter.decrease();
					closeConnection(conn);
				}
			}
		}
	}

	/**
	 * Closes all connections in the connection pool.
	 */
	public void closeAllConnections() {
		Iterator<PooledConnection> it = conPool.iterator();
		while (it.hasNext()) {
			PooledConnection pc = it.next();
			if (pc != null) {
				closeConnection(pc.getConnection());
			}
		}
		conPool.clear(); // remove all PooledConnections from list
	}

	/**
	 * method closes the given connection
	 * 
	 * @param con
	 *            connection to close
	 * @param dbType
	 *            type of database (used to throw appropriate exception)
	 */
	private void closeConnection(Connection con) {
		try {
			if (con != null) {
				con.close();
				con = null;
			}
		} catch (SQLException e) {
			logger.error(e);
		}
	}

	private class PoolMonitor extends RunWrapper {
		private int time;

		public PoolMonitor() {
			super();
			time = 150;
		}

		protected void stopEvent() {
			closeAllConnections();
		}

		public void run() {
			while (true) {
				moniPool();
				sleep(time);
				// this.resetIdleTime();
			}
		}

	}

	public void shutdown() {
		this.closeAllConnections();
		this.monitor.stop();
	}

	public void start() {
		conPool = new LinkedBlockingQueue<DriverPool.PooledConnection>(this.max);
		addConnectionsToPool(this.min);
		monitor = new PoolMonitor();
		monitor.startAsDaemon();
	}

	@Override
	public void setTestSql(String testSql) {
		this.testSql = testSql;
	}
}
