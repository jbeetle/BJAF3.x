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
import com.beetle.framework.persistence.access.ConnectionException;
import com.beetle.framework.persistence.access.DBHelper;
import com.beetle.framework.persistence.access.IConnPool;

import javax.sql.ConnectionPoolDataSource;
import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 通用连接池，依赖与相应的数据库驱动是否有pool(ConnectionPoolDataSource接口)的实现，<br>
 * 此连接池 只是一个面板<br>
 * 
 * @author HenryYu
 * 
 */
public class CommonPool implements IConnPool {

	private MiniConnectionPoolManager pool;
	private String driverName;
	private String conURL;
	private String username;
	private String password;
	private int min, max;
	private String testSql;

	public int getMax() {
		return max;
	}

	public int getMin() {
		return min;
	}

	public String getTestSql() {
		return testSql;
	}

	@Override
	public Connection getConnection() throws ConnectionException {
		try {
			return this.pool.getConnection();
		} catch (SQLException e) {
			throw new ConnectionException(e);
		}
	}

	@Override
	public void closeAllConnections() {
		throw new AppRuntimeException("not supported yet!");
	}

	@Override
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	@Override
	public void setConURL(String conURL) {
		this.conURL = conURL;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void setMin(int min) {
		this.min = min;

	}

	@Override
	public void setTestSql(String testSql) {
		this.testSql = testSql;

	}

	@Override
	public void setMax(int max) {
		this.max = max;

	}

	@Override
	public void start() {
		if (this.pool == null) {
			if (driverName == null || driverName.trim().length() == 0) {
				throw new AppRuntimeException("driverName can't be null!");
			}
			try {
				ConnectionPoolDataSource cpds;
				driverName = driverName.trim();
				if (driverName.startsWith("oracle.jdbc")) {
					oracle.jdbc.pool.OracleConnectionPoolDataSource cpdstmp = new oracle.jdbc.pool.OracleConnectionPoolDataSource();
					cpdstmp.setURL(conURL);
					cpdstmp.setUser(username);
					cpdstmp.setPassword(password);
					cpdstmp.setDriverType("thin");
					cpds = cpdstmp;
				} else if (driverName.startsWith("net.sourceforge.jtds")) {
					net.sourceforge.jtds.jdbcx.JtdsDataSource tmp = new net.sourceforge.jtds.jdbcx.JtdsDataSource();
					tmp.setUser(username);
					tmp.setPassword(password);
					//
					net.sourceforge.jtds.jdbc.Driver dr = new net.sourceforge.jtds.jdbc.Driver();
					DriverPropertyInfo di[] = dr.getPropertyInfo(conURL, null);
					for (int i = 0; i < di.length; i++) {
						DriverPropertyInfo d = di[i];
						if (d.name.equals("SERVERTYPE")) {
							tmp.setServerType(Integer.parseInt(d.value));
						} else if (d.name.equals("DATABASENAME")) {
							tmp.setDatabaseName(d.value);
						} else if (d.name.equals("SERVERNAME")) {
							tmp.setServerName(d.value);
						} else if (d.name.equals("PORTNUMBER")) {
							tmp.setPortNumber(Integer.parseInt(d.value));
						}
					}
					cpds = tmp;
					//
				} else if (driverName.startsWith("com.microsoft.sqlserver")) {
					// The sqljdbc 1.1 documentation, chapter
					// "Using Connection Pooling", recommends to use
					// SQLServerXADataSource
					// instead of SQLServerConnectionPoolDataSource.
					com.microsoft.sqlserver.jdbc.SQLServerXADataSource tmp = new com.microsoft.sqlserver.jdbc.SQLServerXADataSource();
					tmp.setURL(conURL);
					tmp.setUser(username);
					tmp.setPassword(password);
					cpds = tmp;
				} else if (driverName.startsWith("com.mysql.jdbc")) {
					com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource tmp = new com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource();
					tmp.setUrl(conURL);
					tmp.setUser(username);
					tmp.setPassword(password);
					cpds = tmp;
				} else if (driverName.startsWith("org.postgresql")) {
					// String url =
					// "jdbc:postgresql://localhost/test?user=fred&password=secret&ssl=true";
					org.postgresql.ds.PGConnectionPoolDataSource tmp = new org.postgresql.ds.PGConnectionPoolDataSource();
					tmp.setUser(username);
					tmp.setPassword(password);
					// org.postgresql.Driver dr = new org.postgresql.Driver();
					Properties pp = DBHelper.parsePostgreSqlURL(conURL);
					tmp.setDatabaseName(pp.getProperty("PGDBNAME"));
					tmp.setPortNumber(Integer.parseInt(pp.getProperty("PGPORT")));
					tmp.setServerName(pp.getProperty("PGHOST"));
					/*
					 * DriverPropertyInfo di[] = dr.getPropertyInfo(conURL,
					 * null); for (int i = 0; i < di.length; i++) {
					 * DriverPropertyInfo d = di[i]; System.out.println(d.name);
					 * System.out.println(d.value); if
					 * (d.name.equals("PGDBNAME")) {
					 * tmp.setDatabaseName(d.value); } else if
					 * (d.name.equals("PGHOST")) { tmp.setServerName(d.value); }
					 * else if (d.name.equals("PGPORT")) {
					 * tmp.setPortNumber(Integer.parseInt(d.value)); } }
					 */
					cpds = tmp;
				} else if (driverName.startsWith("org.h2")) {
					org.h2.jdbcx.JdbcDataSource tmp = new org.h2.jdbcx.JdbcDataSource();
					tmp.setUser(username);
					tmp.setPassword(password);
					tmp.setURL(conURL);
					cpds = tmp;
				} else {
					throw new AppRuntimeException("sorry,not support ["
							+ driverName + "]yet!");
				}
				this.pool = new MiniConnectionPoolManager(cpds, max, 60);
			} catch (Exception e) {
				throw new AppRuntimeException(e);
			}
		}
	}

	@Override
	public void shutdown() {
		if (pool != null) {
			try {
				pool.dispose();
			} catch (SQLException e) {
				throw new AppRuntimeException(e);
			}
		}
	}

}
