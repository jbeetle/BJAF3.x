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

public final class OracleProcOperator extends ProcOperator {
	public OracleProcOperator() {
	}

	@Override
	protected void processResult(CallableStatement cs)
			throws java.sql.SQLException {
		ResultSet rs = (ResultSet) cs
				.getObject(this.getParameters().size() + 3);
		if (rs != null) {
			ResultSetHandler rsh = new ResultSetHandler();
			while (rs.next()) {
				rsh.handle(rs);
			}
			this.setSqlResultSet(rsh.getResultDataSet());
			rs.close();
		}
	}

	/*
	 * protected String genCallString() { List parameters =
	 * this.getParameters(); String callString = "{call " + getSql() + "("; for
	 * (int i = 0; i < parameters.size(); i++) { if (i > 0) { callString +=
	 * ", "; } callString += "?"; } callString += ",?,?,?)}"; if
	 * (logger.isDebugEnabled()) { logger.debug("callString:" + callString); }
	 * return callString; }
	 */
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
			sb.append(",?,?,?)}");
		} else {
			sb.append("?,?,?)}");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("callString:" + sb.toString());
		}
		return sb.toString();
	}

	protected void setProcOutParameters(CallableStatement cs, int pos)
			throws java.sql.SQLException {
		cs.registerOutParameter(pos + 1, 2); // OracleTypes.NUMBER
		cs.registerOutParameter(pos + 2, 12); // OracleTypes.VARCHAR
		cs.registerOutParameter(pos + 3, -10); // OracleTypes.CURSOR;
	}
}
