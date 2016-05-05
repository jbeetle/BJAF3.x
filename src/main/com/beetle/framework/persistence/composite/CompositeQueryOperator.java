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
package com.beetle.framework.persistence.composite;

import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.persistence.access.operator.QueryOperator;
import com.beetle.framework.persistence.access.operator.SqlParameter;

import java.util.LinkedList;
import java.util.List;

public class CompositeQueryOperator extends QueryOperator {
	public void addParameter(Object value) {
		throw new DBOperatorException(
				"uses the [addParameter(String parameterName, Object value)]instead this methods!");
		// super.addParameter(value);
	}

	public void addParameter(SqlParameter sqlParamter) {
		throw new DBOperatorException(
				"uses the [addParameter(String parameterName, Object value)]instead this methods!");
	}

	// select * from a where (? is null or p1=?) and (? is null or p2=?) and ...

	private List<V> paramList = new LinkedList<V>();

	private static class V {
		public String getParameterName() {
			return parameterName;
		}

		public void setParameterName(String parameterName) {
			this.parameterName = parameterName;
		}

		public String getOperateSymbol() {
			return OperateSymbol;
		}

		public void setOperateSymbol(String operateSymbol) {
			OperateSymbol = operateSymbol;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		private String OperateSymbol;
		private Object value;
		private String parameterName;
	}

	/**
	 * 添加组合查询参数
	 * 
	 * @param parameterName
	 *            --参数名称(检索字段的名称)
	 * @param OperateSymbol
	 *            --操作符号（检索条件，如：=;>;<等）
	 * @param value
	 *            --参数值（检索值，没有，则输入null）
	 */
	public void addParameter(String parameterName, String OperateSymbol,
			Object value) {
		V v = new V();
		v.setOperateSymbol(OperateSymbol);
		v.setValue(value);
		v.setParameterName(parameterName);
		paramList.add(v);
	}

	protected void accessImp() throws DBOperatorException {
		if (!paramList.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < paramList.size(); i++) {
				V v = (V) paramList.get(i);
				sb.append("(? is null or " + v.getParameterName()
						+ v.getOperateSymbol() + "?) and ");
				super.addParameter(v.getValue());
				super.addParameter(v.getValue());
			}
			String whereStr = sb.toString();
			int i = whereStr.lastIndexOf("and");
			whereStr = whereStr.substring(0, i);
			this.setSql(this.getSql() + " where " + whereStr);
			// System.out.println(this.getSql());
			paramList.clear();
		}
		super.accessImp();
	}

	public CompositeQueryOperator() {
		super();
	}

}
