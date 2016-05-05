package com.beetle.framework.persistence.access.datasource;

import com.beetle.framework.persistence.access.ConnectionException;
import com.beetle.framework.persistence.access.IConnPool;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import java.sql.Connection;
import java.sql.SQLException;

public class BoneCPPool implements IConnPool {
	private String driverName;
	private String conURL;
	private String username;
	private String password;
	private int min, max;
	private String testSql;

	@Override
	public Connection getConnection() throws ConnectionException {
		try {
			return connectionPool.getConnection();
		} catch (SQLException e) {
			throw new ConnectionException(e);
		}
	}

	@Override
	public void closeAllConnections() {
		connectionPool.shutdown();
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
	public void setMax(int max) {
		this.max = max;
	}

	private BoneCP connectionPool = null;

	@Override
	public void start() {
		BoneCPConfig config = new BoneCPConfig();
		config.setUsername(username);
		config.setPassword(password);
		config.setJdbcUrl(conURL);
		config.setMinConnectionsPerPartition(min);
		config.setMaxConnectionsPerPartition(max);
		config.setPartitionCount(3);
		if (testSql != null && testSql.length() > 0) {
			config.setInitSQL(testSql);
		}
		try {
			Class.forName(this.driverName);
			connectionPool = new BoneCP(config);
		} catch (Exception e) {
			throw new ConnectionException("start db pool err", e);
		}
	}

	@Override
	public void shutdown() {
		if (connectionPool != null) {
			connectionPool.shutdown();
		}
	}

	@Override
	public void setTestSql(String testSql) {
		this.testSql = testSql;
	}

}
