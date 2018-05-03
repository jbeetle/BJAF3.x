/*
 * BJAF - Beetle J2EE Application Framework
 * �׿ǳ�J2EE��ҵӦ�ÿ������
 * ��Ȩ����2003-2009 ��ƶ� (www.beetlesoft.net)
 * 
 * ����һ����ѿ�Դ�������������ڡ��׿ǳ�J2EEӦ�ÿ�������ȨЭ�顷
 *
 *   ��GNU Lesser General Public License v3.0��
 *<http://www.gnu.org/licenses/lgpl-3.0.txt/>�ºϷ�ʹ�á��޸Ļ����·�����
 *
 * ��л��ʹ�á��ƹ㱾��ܣ����н�������⣬��ӭ�������ϵ��
 * �ʼ��� <yuhaodong@gmail.com/>.
 */
package com.beetle.framework.persistence.pagination.imp;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.beetle.framework.log.AppLogger;
import com.beetle.framework.persistence.access.ConnectionFactory;
import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.persistence.access.operator.QueryOperator;
import com.beetle.framework.persistence.access.operator.RsDataSet;
import com.beetle.framework.persistence.access.operator.SqlParameter;
import com.beetle.framework.persistence.composite.CompositeQueryOperator.V;
import com.beetle.framework.persistence.pagination.IPagination;
import com.beetle.framework.persistence.pagination.PageBaseInfo;
import com.beetle.framework.persistence.pagination.PageHelper;
import com.beetle.framework.persistence.pagination.PageParameter;
import com.beetle.framework.persistence.pagination.PageResult;
import com.beetle.framework.persistence.pagination.PaginationException;
import com.beetle.framework.util.cache.ICache;
import com.beetle.framework.util.cache.TimeOutCache;

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
public class MysqlPaginationImp implements IPagination {
	private static ICache countCache = new TimeOutCache(120);

	private static AppLogger logger = AppLogger.getInstance(MysqlPaginationImp.class);

	public MysqlPaginationImp() {

	}

	private int getRecordAmount(Connection conn, PageParameter pInfo) throws DBOperatorException {
		Integer amount;
		if (pInfo.isCacheRecordAmountFlag()) {
			Integer key = new Integer(pInfo.getUserSql().hashCode());
			amount = (Integer) countCache.get(key);
			if (amount == null) {
				amount = caleAmount(conn, pInfo);
				countCache.put(key, amount);
			}
		} else {
			amount = caleAmount(conn, pInfo);
		}
		return amount.intValue();
	}

	private Integer caleAmount(Connection conn, PageParameter pInfo) throws NumberFormatException, DBOperatorException {
		// select count(1) from (select * from account) t
		Integer amount;
		QueryOperator query = createQueryOperator(pInfo);// new QueryOperator();
		query.setUseOnlyConnectionFlag(true);
		if (pInfo.isNormalSQLQueryMode()) {
			if (!pInfo.getSqlParameters().isEmpty()) {
				for (int i = 0; i < pInfo.getSqlParameters().size(); i++) {
					SqlParameter sp = (SqlParameter) pInfo.getSqlParameters().get(i);
					query.addParameter(sp);
				}
			}
			query.setSql("select count(*) from (" + pInfo.getUserSql() + ") c_t");
		} else {
			List<V> paramList = pInfo.getCompositeSQLParamList();
			if (!paramList.isEmpty()) {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < paramList.size(); i++) {
					V v = (V) paramList.get(i);
					sb.append("(? is null or " + v.getParameterName() + " " + v.getOperateSymbol() + " ?) and ");
					query.addParameter(v.getValue());
					query.addParameter(v.getValue());
				}
				String whereStr = sb.toString();
				int i = whereStr.lastIndexOf("and");
				whereStr = whereStr.substring(0, i);
				//
				String tmpSql = pInfo.getUserSql().toLowerCase();
				final String sql;
				if (tmpSql.indexOf("where") > 1) {
					sql = pInfo.getUserSql() + " and " + whereStr;
				} else {
					sql = pInfo.getUserSql() + " where " + whereStr;
				}
				//
				// String sql = pInfo.getUserSql() + " where " + whereStr;
				query.setSql("select count(*) from (" + sql + ") c_t");
				// paramList.clear();
			}
		}
		query.setPresentConnection(conn);
		query.access();
		RsDataSet rs = new RsDataSet(query.getSqlResultSet());
		amount = new Integer(rs.getFieldValueAsString(0));
		rs.clearAll();
		if (logger.isDebugEnabled()) {
			logger.debug("amount:" + amount.intValue());
		}
		return amount;
	}

	private QueryOperator createQueryOperator(PageParameter pInfo) {
		if (pInfo.isNotDesensitize()) {
			return new QueryOperator(true);
		}
		return new QueryOperator();
	}

	public PageResult page(PageParameter pInfo) throws PaginationException {
		Connection conn = null;
		PageResult pr = new PageResult();
		try {
			conn = ConnectionFactory.getConncetion(pInfo.getDataSourceName());
			int pos = pInfo.getPageNumber() - 1;
			if (pos >= 0) {
				QueryOperator query = createQueryOperator(pInfo);// new
																	// QueryOperator();
				query.setPresentConnection(conn);
				query.setUseOnlyConnectionFlag(true);
				if (pInfo.isNormalSQLQueryMode()) {
					String usersql = pInfo.getUserSql();
					StringBuffer sb = new StringBuffer();
					sb.append(usersql);
					int pn = pInfo.getPageNumber();
					int bn = ((pn - 1) * pInfo.getPageSize());
					sb.append(" limit " + bn + "," + pInfo.getPageSize());
					String usql = sb.toString();
					if (logger.isDebugEnabled()) {
						logger.debug("sql:" + usql);
					}
					query.setSql(usql);
					for (SqlParameter sp : pInfo.getSqlParameters()) {
						query.addParameter(sp);
					}
				} else {
					List<V> paramList = pInfo.getCompositeSQLParamList();
					if (!paramList.isEmpty()) {
						StringBuffer sb = new StringBuffer();
						if (pInfo.isUseNullParameter()) {
							for (int i = 0; i < paramList.size(); i++) {
								V v = (V) paramList.get(i);
								sb.append("(? is null or " + v.getParameterName() + " " + v.getOperateSymbol()
										+ " ?) and ");
								query.addParameter(v.getValue());
								query.addParameter(v.getValue());
							}
						} else {
							for (int i = 0; i < paramList.size(); i++) {
								V v = (V) paramList.get(i);
								if (v.getValue() != null) {
									sb.append(" " + v.getParameterName() + " " + v.getOperateSymbol() + " ? and ");
									query.addParameter(v.getValue());
								}
							}
						}
						String whereStr = sb.toString();
						String usersql = pInfo.getUserSql();
						if (StringUtils.isNotBlank(whereStr)) {
							int i = whereStr.lastIndexOf("and");
							whereStr = whereStr.substring(0, i);
							String tmpSql = pInfo.getUserSql().toLowerCase();
							if (tmpSql.indexOf("where") > 1) {
								usersql = usersql + " and " + whereStr;
							} else {
								usersql = usersql + " where " + whereStr;
							}
						}
						if (pInfo.getOrderExpression().length() > 1) {
							usersql = usersql + " " + pInfo.getOrderExpression().toLowerCase();
						}
						StringBuffer sb2 = new StringBuffer();
						sb2.append(usersql);
						int pn = pInfo.getPageNumber();
						int bn = ((pn - 1) * pInfo.getPageSize());
						sb2.append(" limit " + bn + "," + pInfo.getPageSize());
						String usql = sb2.toString();
						if (logger.isDebugEnabled()) {
							logger.debug("sql:" + usql);
						}
						query.setSql(usql);
						// paramList.clear();
					}
				}
				query.access();
				if (query.resultSetAvailable()) {
					pr.setSqlResultSet(query.getSqlResultSet());
					pr.setCurPageNumber(pInfo.getPageNumber());
					pr.setCurPageSize(pr.getSqlResultSet().size());
				}
			}
			pr.setRecordAmount(getRecordAmount(conn, pInfo));
			PageHelper.countPageInfo(pInfo, pr);
			if (logger.isDebugEnabled()) {
				logger.debug("==Page Info===");
				logger.debug("curPageNumber:" + pr.getCurPageNumber());
				logger.debug("nextPageNumber:" + pr.getNextPageNumber());
				logger.debug("prePageNumber:" + pr.getPrePageNumber());
				logger.debug("pageAmount:" + pr.getPageAmount());
				logger.debug("curPos:" + pr.getCurPos());
				logger.debug("curPageSize:" + pr.getCurPageSize());
				logger.debug("recordAmount:" + pr.getRecordAmount());
			}
			return pr;
		} catch (Throwable e) {
			logger.error(e);
			throw new PaginationException(e);
		} finally {
			ConnectionFactory.closeConnection(conn);
			if (!pInfo.getSqlParameters().isEmpty()) {
				pInfo.getSqlParameters().clear();
			}
		}
	}

	public PageBaseInfo calc(PageParameter pInfo) throws PaginationException {
		Connection conn = null;
		PageBaseInfo pbi = new PageBaseInfo();
		try {
			conn = ConnectionFactory.getConncetion(pInfo.getDataSourceName());
			pbi.setPageSize(pInfo.getPageSize());
			pbi.setRecordAmount(getRecordAmount(conn, pInfo));
			pbi.setPageAmount(PageHelper.pageCount(pInfo.getPageSize(), pbi.getRecordAmount()));
		} catch (Throwable e) {
			logger.error(e);
			throw new PaginationException(e);
		} finally {
			ConnectionFactory.closeConnection(conn);
			if (!pInfo.getSqlParameters().isEmpty()) {
				pInfo.getSqlParameters().clear();
			}
			pInfo.getCompositeSQLParamList().clear();
		}
		return pbi;
	}

}
