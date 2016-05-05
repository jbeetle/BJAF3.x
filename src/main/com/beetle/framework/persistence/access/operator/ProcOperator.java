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

import com.beetle.framework.persistence.access.ConnectionFactory;
import com.beetle.framework.persistence.access.base.AccessMannerFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public abstract class ProcOperator extends BaseOperator {
	private List<Map<String, Object>> sqlResultSet = null;
	protected int returnFlag; // 存储过程输出参数，成功与否的返回标记
	protected String returnMsg; // 存储过程输出参数，存储过程返回信息

	/**
	 * 存储过程执行成功标记
	 */
	public final static int PROC_SUCCESS_FLAG = 0; // 存储过程执行成功标记
	/**
	 * 存储过程拥有返回结果集标记
	 */
	public final static int PROC_HAVE_RESULT = 1; // 存储过程拥有返回结果集标记

	public ProcOperator() {
	}

	protected void accessImp() throws DBOperatorException {
		Connection conn = null;
		CallableStatement call = null;
		try {
			if (!this.isPresentConnectionUsable()) {
				conn = ConnectionFactory
						.getConncetion(this.getDataSourceName());
				this.setPresentConnection(conn);
			} else {
				conn = this.getPresentConnection();
			}
			// call = conn.prepareCall(genCallString());
			// 构造call语句
			call = AccessMannerFactory.getAccessManner(genCallString())
					.accessByCallableStatement(conn);
			processInputParameter(call); // 处理存储输入参数
			setProcOutParameters(call, this.getParameters().size()); // 登记输出参数
			call.execute(); // 执行
			this.returnFlag = call.getInt(this.getParameters().size() + 1);
			this.returnMsg = call.getString(this.getParameters().size() + 2);
			if (logger.isDebugEnabled()) {
				logger.debug("returnFlag:" + this.getReturnFlag());
				logger.debug("returnMsg:" + this.getReturnMsg());
			}
			if (this.getReturnFlag() >= PROC_SUCCESS_FLAG) {
				if (this.getReturnFlag() == PROC_HAVE_RESULT) {
					this.processResult(call);
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new DBOperatorException("call proc err", e);
		} finally {
			if (!this.isUseOnlyConnectionFlag()) {
				ConnectionFactory.closeAll(conn, call, null);
			} else {
				ConnectionFactory.closeAll(null, call, null);
			}
		}
	}

	protected abstract void processResult(CallableStatement call)
			throws SQLException;

	protected void processInputParameter(CallableStatement call)
			throws SQLException {
		List<SqlParameter> parameters = this.getParameters();
		for (int i = 0; i < parameters.size(); i++) {
			SqlParameter p = parameters.get(i);
			call.setObject(i + 1, p.getValue(), p.getType());
		}
	}

	protected abstract String genCallString();

	protected abstract void setProcOutParameters(CallableStatement cs, int pos)
			throws SQLException;

	/**
	 * 获取执行结果标记
	 * 
	 * @return int
	 */
	public int getReturnFlag() {
		return returnFlag;
	}

	/**
	 * 获取执行结果信息
	 * 
	 * @return String
	 */
	public String getReturnMsg() {
		return returnMsg;
	}

	/**
	 * 返回存储过程返回的结果列表
	 * 
	 * 
	 * @return List
	 * @throws DBOperatorExcepton
	 */
	public List<Map<String, Object>> getSqlResultSet() {
		return sqlResultSet;
	}

	void setSqlResultSet(List<Map<String, Object>> sqlResultSet) {
		this.sqlResultSet = sqlResultSet;
	}

}
