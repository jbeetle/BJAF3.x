package com.beetle.framework.persistence.access.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import com.beetle.framework.AppProperties;
import com.beetle.framework.persistence.access.ConnectionException;
import com.beetle.framework.persistence.access.IConnPool;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author henryyu
 *相关属性可以通过application.properties应用配置文件扩展
 * 当前暴露的属性有：
 * HikariCPPool_cachePrepStmts
 * HikariCPPool_prepStmtCacheSize
 * HikariCPPool_prepStmtCacheSqlLimit
 * HikariCPPool_connectionTimeout
 * HikariCPPool_idleTimeout
 * HikariCPPool_maxLifetime
 * 参数说明请参考
 * https://github.com/brettwooldridge/HikariCP
 * 
 */
public class HikariCPPool implements IConnPool {
	private String driverName;
	private String conURL;
	private String username;
	private String password;
	private int min, max;
	private String testSql;
	private HikariDataSource datasource;

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
		datasource.close();
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
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(this.conURL);
		config.setUsername(this.username);
		config.setPassword(this.password);
		config.setDriverClassName(this.driverName);
		config.setMaximumPoolSize(max);
		config.setMinimumIdle(min);
		if (this.testSql != null && this.testSql.trim().length() > 0) {
			config.setConnectionTestQuery(testSql);
		}
		config.setPoolName("HikariCPPool[" + username + "]");
		config.addDataSourceProperty("cachePrepStmts", AppProperties.get("HikariCPPool_cachePrepStmts", "true"));
		config.addDataSourceProperty("prepStmtCacheSize", AppProperties.get("HikariCPPool_prepStmtCacheSize", "250"));
		config.addDataSourceProperty("prepStmtCacheSqlLimit",
				AppProperties.get("HikariCPPool_prepStmtCacheSqlLimit", "2048"));
		config.setConnectionTimeout(AppProperties.getAsInt("HikariCPPool_connectionTimeout", 30000));
		config.setIdleTimeout(AppProperties.getAsInt("HikariCPPool_idleTimeout", 600000));
		config.setMaxLifetime(AppProperties.getAsInt("HikariCPPool_maxLifetime", 1800000));
		// HikariDataSource ds = new HikariDataSource(config);
		// this.datasource=ds.getDataSource();
		this.datasource = new HikariDataSource(config);
	}

	@Override
	public void shutdown() {
		if (this.datasource != null) {
			this.datasource.close();
		}
	}

}
