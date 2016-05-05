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

/**
 * <p>
 * Title: BeetleSoft Framework
 * </p>
 * 
 * <p>
 * Description: 数据库连接池接口
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: beetlesoft
 * </p>
 * 
 * @author 余浩东
 * 
 * @version 1.0
 */
public interface IConnPool {
	Connection getConnection() throws ConnectionException;

	void closeAllConnections();

	public void setDriverName(String driverName);

	public void setConURL(String conURL);

	public void setUsername(String username);

	public void setPassword(String password);

	public void setMin(int min);

	public void setTestSql(String testSql);

	public void setMax(int max);

	void start();

	void shutdown();
}
