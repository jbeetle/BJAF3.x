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

import com.beetle.framework.persistence.access.ConnectionFactory;
import com.beetle.framework.persistence.access.base.AccessMannerFactory;
import com.beetle.framework.persistence.access.base.ResultSetHandler;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * <p>
 * Title: BeetleSoft Framework
 * </p>
 * <p>
 * Description: J2EE系统开发框架
 * 
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: 甲壳虫软件
 * 
 * </p>
 * 
 * @author 余浩东
 * 
 * @version 1.0
 */

public final class SqlServerProcOperator extends ProcOperator {
	public SqlServerProcOperator() {
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
			boolean f = call.execute(); // 执行
			if (!f) {
				this.returnFlag = call.getInt(this.getParameters().size() + 1);
				this.returnMsg = call
						.getString(this.getParameters().size() + 2);
			} else {
				this.processResult(call);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("returnFlag:" + this.getReturnFlag());
				logger.debug("returnMsg:" + this.getReturnMsg());
			}
			/*
			 * if (this.getReturnFlag() >= PROC_SUCCESS_FLAG) { if
			 * (this.getReturnFlag() == PROC_HAVE_RESULT) {
			 * this.processResult(call, rs); } }
			 */
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

	@Override
	protected void processResult(CallableStatement cs) throws SQLException {
		ResultSet rs = cs.getResultSet();
		if (rs == null) {
			return;
		}
		ResultSetHandler rsh = new ResultSetHandler();
		while (rs.next()) {
			rsh.handle(rs);
		}
		// this.returnFlag = cs.getInt(this.getParameters().size() + 1);
		// this.returnMsg = cs.getString(this.getParameters().size() + 2);
		rs.close();
		if (rsh.getRowCount() == 0) {
			while (cs.getMoreResults()) {
				rs = cs.getResultSet();
				if (rs != null) {
					while (rs.next()) {
						rsh.handle(rs);
					}
					rs.close();
					if (rsh.getRowCount() > 0) {
						break;
					}
				}
			}
		}
		this.returnFlag = cs.getInt(this.getParameters().size() + 1);
		this.returnMsg = cs.getString(this.getParameters().size() + 2);
		if (rsh.getRowCount() > 0) {
			this.setSqlResultSet(rsh.getResultDataSet());
		} else {
			if (rsh.getResultDataSet() != null) {
				rsh.getResultDataSet().clear();
			}
			rsh = null;
		}
	}

	protected String genCallString() {
		List<?> parameters = this.getParameters();
		StringBuffer sb = new StringBuffer();
		sb.append("{call ");
		sb.append(this.getSql());
		sb.append("(");
		for (int i = 0; i < parameters.size(); i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append("?");
		}
		if (sb.toString().indexOf('?') >= 0) {
			sb.append(",?,?)}");
		} else {
			sb.append("?,?)}");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("callString:" + sb.toString());
		}
		return sb.toString();
	}

	/*
	 * protected String genCallString() { List parameters =
	 * this.getParameters(); String callString = "{call " + getSql() + "("; for
	 * (int i = 0; i < parameters.size(); i++) { if (i > 0) { callString +=
	 * ", "; } callString += "?"; } callString += ",?,?)}"; if
	 * (logger.isDebugEnabled()) { logger.debug("callString:" + callString); }
	 * return callString; }
	 */
	protected void setProcOutParameters(CallableStatement cs, int pos)
			throws java.sql.SQLException {
		cs.registerOutParameter(pos + 1, java.sql.Types.INTEGER);
		cs.registerOutParameter(pos + 2, java.sql.Types.VARCHAR);
	}

	/*
	 * public static void main(String arg[]) { StringBuffer sb = new
	 * StringBuffer(); sb.append("{call "); sb.append("xxx"); sb.append("(");
	 * for (int i = 0; i < 6; i++) { if (i > 0) { sb.append(","); }
	 * sb.append("?"); } sb.append(",?,?)}"); if (logger.isDebugEnabled()) {
	 * logger.debug("callString:" + sb.toString()); } }
	 */
}
