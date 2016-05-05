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

import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean;
import com.beetle.framework.persistence.access.ConnectionException;
import com.beetle.framework.persistence.access.IConnPool;
import com.beetle.framework.util.UUIDGenerator;

import java.sql.Connection;

/**
 * Atomikos TransactionsEssentials非 Xa数据源连接池封装
 * 
 */
public class NonXaPool implements IConnPool {
	private AtomikosNonXADataSourceBean adsb = null;
	private String driverName;
	private String conURL;
	private String username;
	private String password;
	private int min, max;
	private String testSql;

	@Override
	public Connection getConnection() throws ConnectionException {
		try {
			return adsb.getConnection();
		} catch (Exception e) {
			throw new ConnectionException(e);
		}
	}

	@Override
	public void closeAllConnections() {
		shutdown();
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
		if (adsb == null) {
			adsb = new AtomikosNonXADataSourceBean();
			adsb.setMinPoolSize(min);
			adsb.setMaxPoolSize(max);
			adsb.setTestQuery(testSql);
			adsb.setUniqueResourceName(UUIDGenerator.generateUUID());
			adsb.setUrl(this.conURL);
			adsb.setUser(username);
			adsb.setPassword(password);
			adsb.setDriverClassName(driverName);
			adsb.setBorrowConnectionTimeout(60);
		}
	}

	@Override
	public void shutdown() {
		if (adsb != null) {
			adsb.close();
			adsb = null;
		}
	}

}
