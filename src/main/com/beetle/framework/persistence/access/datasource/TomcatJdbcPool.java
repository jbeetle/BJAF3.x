package com.beetle.framework.persistence.access.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import com.beetle.framework.AppProperties;
import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.persistence.access.ConnectionException;
import com.beetle.framework.persistence.access.IConnPool;

public class TomcatJdbcPool implements IConnPool {
	private String driverName;
	private String conURL;
	private String username;
	private String password;
	private int min, max;
	private String testSql;
	private DataSource datasource;

	@Override
	public Connection getConnection() throws ConnectionException {
		try {
			return datasource.getConnection();
		} catch (SQLException e) {
			throw new ConnectionException(e);
		}
	}

	@Override
	public void closeAllConnections() {
		datasource.close(true);
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
		PoolProperties p = new PoolProperties();
		p.setUrl(this.conURL);
		p.setDriverClassName(this.driverName);
		p.setUsername(this.username);
		p.setPassword(this.password);
		p.setJmxEnabled(false);
		if (this.testSql != null && this.testSql.trim().length() > 0) {
			p.setValidationQuery(this.testSql);
			p.setTestWhileIdle(true);
			p.setTimeBetweenEvictionRunsMillis(15 * 1000);
			boolean tbf = AppProperties.getAsBoolean(
					"TomcatJdbcPool_TestOnBorrow", false);
			p.setTestOnBorrow(tbf);// 获取之前是否校验
			p.setTestOnReturn(false);
			p.setValidationInterval(30000);
		}
		p.setMaxActive(this.max);
		p.setMaxIdle(this.max);
		p.setInitialSize(this.min);
		p.setMinIdle(min);
		int maxwaitfornoconn = AppProperties.getAsInt("TomcatJdbcPool_MaxWait",
				30 * 1000);
		p.setMaxWait(maxwaitfornoconn);
		boolean removeAbandonedFlag = AppProperties.getAsBoolean(
				"TomcatJdbcPool_RemoveAbandoned", false);
		if (removeAbandonedFlag) {
			p.setLogAbandoned(true);
			p.setRemoveAbandoned(true);
			p.setRemoveAbandonedTimeout(AppProperties.getAsInt(
					"TomcatJdbcPool_RemoveAbandonedTimeout", 60));
		}
		// p.setMinEvictableIdleTimeMillis(30000);
		p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
				+ "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
		datasource = new DataSource();
		datasource.setPoolProperties(p);
		try {
			datasource.createPool();
		} catch (SQLException e) {
			datasource.close();
			throw new AppRuntimeException("start tomcat pool err", e);
		}
	}

	@Override
	public void shutdown() {
		if (datasource != null) {
			datasource.close();
		}
	}

}
