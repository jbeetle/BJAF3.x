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
package com.beetle.framework.persistence.pagination;

import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.persistence.access.operator.SqlParameter;
import com.beetle.framework.persistence.access.operator.SqlType;
import com.beetle.framework.persistence.composite.CompositeQueryOperator.V;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * Title: Beetle Persistence Framework
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: BeetleSoft
 * </p>
 * 
 * @author HenryYu (yuhaodong@gmail.com)
 * @version 1.0
 */
public class PageParameter {
	public enum QueryMode {
		NormalSQL(0), CompositeSQL(1);
		public int getValue() {
			return value;
		}

		private int value;

		private QueryMode(int value) {
			this.value = value;
		}
	};

	private String dataSourceName;
	private QueryMode queryMode;
	private int pageNumber;

	private int pageSize;

	private boolean cacheRecordAmountFlag = false;
	
	private boolean useNullParameter=true;//使用null作为查询条件，组合查询模式时候使用

	/**
	 * 唯一标识别字段名称
	 * 
	 */
	private String identityKey;

	private String userSql;

	private List<SqlParameter> sqlParameters = new LinkedList<SqlParameter>();
	private List<V> paramList = new LinkedList<V>();
	// order表达式(order by xxx desc)
	private String orderExpression = "";
	private boolean notDesensitize;
	
	public boolean isNotDesensitize() {
		return notDesensitize;
	}

	public void setNotDesensitize(boolean notDesensitize) {
		this.notDesensitize = notDesensitize;
	}

	public PageParameter() {
		this.queryMode = QueryMode.NormalSQL;
		orderExpression = "";
	}

	public PageParameter(QueryMode queryMode) {
		super();
		this.queryMode = queryMode;
		orderExpression = "";
	}

	/**
	 * 设置sql查询结果集的排序表达式，如："order by xxx desc"<br>
	 * 只针对QueryMode为CompositeSQL模式
	 * 
	 * @param orderExpression
	 */
	public void setOrderExpression(String orderExpression) {
		if (this.queryMode == QueryMode.NormalSQL) {
			throw new DBOperatorException("QueryMode.NormalSQL can't use this methods!");
		}
		this.orderExpression = orderExpression;
	}

	public String getOrderExpression() {
		return orderExpression;
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
	public void addParameter(String parameterName, String OperateSymbol, Object value) {
		if (queryMode == QueryMode.NormalSQL) {
			throw new DBOperatorException("uses the [addParameter(Object value)]instead this methods!");
		}
		V v = new V();
		v.setOperateSymbol(OperateSymbol);
		v.setValue(value);
		v.setParameterName(parameterName);
		paramList.add(v);
	}

	public void addParameter(SqlParameter sqlParamter) {
		if (queryMode == QueryMode.CompositeSQL) {
			throw new DBOperatorException(
					"uses the [addParameter(String parameterName, Object value)]instead this methods!");
		}
		sqlParameters.add(sqlParamter);
	}

	/**
	 * 添加sql语句参数
	 * 
	 * @param 参数值
	 * 
	 *            （无需指定构造sqlParamter对象，不指定参数类型。依赖具体的jdbc驱动）
	 */
	public void addParameter(Object value) {
		if (queryMode == QueryMode.CompositeSQL) {
			throw new DBOperatorException(
					"uses the [addParameter(String parameterName, Object value)]instead this methods!");
		}
		if (value == null) {
			sqlParameters.add(new SqlParameter(SqlType.NUMERIC, null)); // has
																		// bug
		} else {
			sqlParameters.add(new SqlParameter(value));
		}
	}

	public boolean isNormalSQLQueryMode() {
		if (queryMode == QueryMode.NormalSQL) {
			return true;
		}
		return false;
	}

	public List<V> getCompositeSQLParamList() {
		return paramList;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public String getUserSql() {
		return userSql;
	}

	public String getIdentityKey() {
		return identityKey;
	}

	public List<SqlParameter> getSqlParameters() {
		return sqlParameters;
	}

	public boolean isCacheRecordAmountFlag() {
		return cacheRecordAmountFlag;
	}

	/**
	 * 设置当前显示第几页的页号 页面从1开始算
	 * 
	 * @param pageNumber
	 *            int
	 */
	public void setPageNumber(int pageNumber) {
		if (pageNumber <= 0) {
			pageNumber = 1;
		}
		this.pageNumber = pageNumber;
	}

	/**
	 * 设置页面大小，即每页显示的数据量
	 * 
	 * @param pageSize
	 *            int
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * 设置系统数据源
	 * 
	 * 
	 * @param dataSourceName
	 *            String
	 */
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	/**
	 * 设置用户分页语句
	 * 
	 * @param userSql
	 *            String
	 */
	public void setUserSql(String userSql) {
		this.userSql = userSql.trim();
	}

	/**
	 * 设置这个查询结果集的唯一标识字段名称(可选)
	 * 
	 * @param identityKey
	 *            String
	 */
	public void setIdentityKey(String identityKey) {
		this.identityKey = identityKey;
	}

	/**
	 * 设置总记录数是否缓存。由于执行没有状态，没调用一次查分页查询操作，
	 * 
	 * 系统都会重新计算一次总记录数，对于总记录数不发生变动（或者很长时间才发生变动的情况）
	 * 我们可以缓存它，达到优化的目的。在这里我们会缓存2分钟。设置为true则代表缓存， 默认为不缓存，设置为false则不缓存。
	 * 
	 * 
	 * @param cacheRecordAmountFlag
	 *            boolean
	 */
	public void setCacheRecordAmountFlag(boolean cacheRecordAmountFlag) {
		this.cacheRecordAmountFlag = cacheRecordAmountFlag;
	}

    public boolean isUseNullParameter() {
        return useNullParameter;
    }

    public void setUseNullParameter(boolean useNullParameter) {
        this.useNullParameter = useNullParameter;
    }
}
