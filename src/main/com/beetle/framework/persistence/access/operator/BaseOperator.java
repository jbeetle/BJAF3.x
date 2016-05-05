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
package com.beetle.framework.persistence.access.operator;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 余浩东

 * @version 1.0
 */

import com.beetle.framework.log.AppLogger;
import com.beetle.framework.persistence.access.ConnectionFactory;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

public abstract class BaseOperator {
	private String sql;
	private static final String SYSDATASOURCE_DEFAULT = "SYSDATASOURCE_DEFAULT";
	private String dataSourceName = SYSDATASOURCE_DEFAULT;
	private List<SqlParameter> parameters = new LinkedList<SqlParameter>(); // sql参数
	private Connection presentConnection; // 当前使用的数据库连接
	private boolean useOnlyConnectionFlag = false; // 仅使用一个连接的标记，默认为false，需要手工关闭连接

	protected final static AppLogger logger = AppLogger
			.getInstance(BaseOperator.class);
	private boolean accessFlag = false;

	protected BaseOperator() {
	}

	/**
	 * 设置sql语句
	 * 
	 * @param sql
	 *            String
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}

	/**
	 * 执行访问（查询）
	 * 
	 * @throws DBOperatorExcepton
	 */
	public final void access() throws DBOperatorException {
		if (!isAccessed()) {
			if (sql == null) {
				this.getParameters().clear();
				throw new DBOperatorException("sql has not set yet");
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("sql:[" + this.getSql() + "]");
				}
				try {
					accessImp();
				} finally {
					accessFlag = true;
					this.parameters.clear();
				}
			}
		} else {
			throw new DBOperatorException(
					"can't access again![One Operator only access once! ]");
		}
	}

	protected abstract void accessImp() throws DBOperatorException;

	/**
	 * 是否已访问（执行）
	 * 
	 * 
	 * @return true为已访问
	 */
	public boolean isAccessed() {
		return accessFlag;
	}

	/**
	 * 设置数据源名称，以便根据它获取数据库连接
	 * 
	 * @param dataSourceName
	 *            配置文件定义的数据源名称
	 */
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	/**
	 * 添加sql语句参数
	 * 
	 * @param sqlParamter
	 *            sql语句参数封装对象
	 */
	public void addParameter(SqlParameter sqlParamter) {
		parameters.add(sqlParamter);
	}

	/**
	 * 添加sql语句参数
	 * 
	 * @param 参数值
	 * 
	 *            （无需指定构造sqlParamter对象，不指定参数类型。依赖具体的jdbc驱动）
	 */
	public void addParameter(Object value) {
		if (value == null) {
			parameters.add(new SqlParameter(SqlType.NUMERIC, null)); // has bug
		} else {
			parameters.add(new SqlParameter(value));
		}
	}

	List<SqlParameter> getParameters() {
		return parameters;
	}

	protected String getSql() {
		return sql;
	}

	String getDataSourceName() {
		return dataSourceName;
	}

	/**
	 * 关闭当前数据连接
	 */
	public void closePresentConnection() {
		ConnectionFactory.closeAll(this.getPresentConnection(), null, null);
	}

	/**
	 * 单连接使用标记，一般我们的数据库连接都是从数据源定义获取，<br>
	 * 使用完以后就自动关闭，但是，为了在一个连接里面多次访问，就需要手工维护
	 * 
	 * 
	 * @return boolean
	 */
	public boolean isUseOnlyConnectionFlag() {
		return useOnlyConnectionFlag;
	}

	/**
	 * 设置使用单连接标记
	 * 
	 * 
	 * @param useOnlyConnectionFlag
	 *            boolean
	 */
	public void setUseOnlyConnectionFlag(boolean useOnlyConnectionFlag) {
		this.useOnlyConnectionFlag = useOnlyConnectionFlag;
	}

	/**
	 * 是否当前连接可用
	 * 
	 * @return boolean
	 */
	public boolean isPresentConnectionUsable() {
		if (this.getPresentConnection() != null) {
			boolean abl = true;
			try {
				if (this.getPresentConnection().isClosed()) {
					abl = false;
				}
			} catch (java.sql.SQLException se) {
				abl = false;
			}
			return abl;
		} else {
			return false;
		}
	}

	/**
	 * 获取当前数据访问连接
	 * 
	 * @return Connection
	 */
	public Connection getPresentConnection() {
		return presentConnection;
	}

	/**
	 * 需要手动维护连接时候，设置当前连接
	 * 
	 * @param presentConnection
	 *            Connection
	 */
	public void setPresentConnection(Connection presentConnection) {
		this.presentConnection = presentConnection;
	}
}
