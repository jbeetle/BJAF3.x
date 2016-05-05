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

import com.beetle.framework.persistence.access.base.ResultSetHandler;

import java.sql.CallableStatement;
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

public final class CommonProcOperator extends ProcOperator {
	public CommonProcOperator() {
	}

	@Override
	protected void processResult(CallableStatement cs) throws SQLException {
		ResultSet rs = cs.getResultSet();
		if (rs == null && cs.getUpdateCount() == -1) {
			int updateCount;
			if (this.getReturnFlag() == PROC_HAVE_RESULT) {
				cs.execute();
			}
			do {
				updateCount = cs.getUpdateCount();
				if (updateCount != -1) {
					cs.getMoreResults();
					continue;
				}
				rs = cs.getResultSet();
				if (rs != null) {
					break;
				}
			} while (!(updateCount == -1 && rs == null));
		}
		if (rs != null) {
			ResultSetHandler rsh = new ResultSetHandler();
			boolean abl = false;
			while (rs.next()) {
				abl = true;
				rsh.handle(rs);
			}
			rs.close();
			if (!abl) { // 如果第一个结果没有,获取多一次
				cs.getMoreResults();
				rs = cs.getResultSet();
				if (rs != null) {
					while (rs.next()) {
						rsh.handle(rs);
					}
					rs.close();
				}
			}
			if (rsh.getRowCount() > 0) {
				this.setSqlResultSet(rsh.getResultDataSet());
			}
		}
	}

	protected String genCallString() {
		List<SqlParameter> parameters = this.getParameters();
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
