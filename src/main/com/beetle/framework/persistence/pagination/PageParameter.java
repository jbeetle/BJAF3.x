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

import com.beetle.framework.persistence.access.operator.SqlParameter;

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
	private String dataSourceName;

	private int pageNumber;

	private int pageSize;

	private boolean cacheRecordAmountFlag = false;

	/**
	 * 唯一标识别字段名称

	 */
	private String identityKey;

	private String userSql;

	private List<SqlParameter> sqlParameters = new LinkedList<SqlParameter>(); 

	public PageParameter() {

	}

	public void addParameter(SqlParameter sqlParamter) {
		sqlParameters.add(sqlParamter);
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
	 * 设置当前显示第几页的页号
	 * 
	 * @param pageNumber
	 *            int
	 */
	public void setPageNumber(int pageNumber) {
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

	 * 系统都会重新计算一次总记录数，对于总记录数不发生变动（或者很长时间才发生变动的情况）
	 * 我们可以缓存它，达到优化的目的。在这里我们会缓存2分钟。设置为true则代表缓存， 默认为不缓存，设置为false则不缓存。

	 * 
	 * @param cacheRecordAmountFlag
	 *            boolean
	 */
	public void setCacheRecordAmountFlag(boolean cacheRecordAmountFlag) {
		this.cacheRecordAmountFlag = cacheRecordAmountFlag;
	}
}
