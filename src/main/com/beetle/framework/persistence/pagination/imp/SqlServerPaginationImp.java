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
package com.beetle.framework.persistence.pagination.imp;

import com.beetle.framework.log.AppLogger;
import com.beetle.framework.persistence.access.ConnectionFactory;
import com.beetle.framework.persistence.access.operator.*;
import com.beetle.framework.persistence.pagination.*;
import com.beetle.framework.util.OtherUtil;
import com.beetle.framework.util.cache.ICache;
import com.beetle.framework.util.cache.TimeOutCache;

import java.sql.Connection;
import java.util.StringTokenizer;

/**
 * sqlserver��ҳʵ���� ��Ҫ��װ����Ĵ洢���
 * 
 * @querySql ��ѯ���, @pageNum ҳ��, @pageSize ÿҳ��¼����
 * @storeproc CREATE procedure Sp_Pagination
 * @querySql varchar(8000),
 * @pageNum int,
 * @pageSize int,
 * @o_flag int output,
 * @o_msg varchar(50) output as begin set nocount on declare @p int,@nRowCount
 *        int exec sp_cursoropen @p
 *        output,@querySql,@scrollopt=2,@ccopt=335873,@rowcount=@nRowCount
 *        output if(@p!=0) begin set @pageNum=(@pageNum-1)*@pageSize+1 exec
 *        sp_cursorfetch @p,32,@pageNum,@pageSize exec sp_cursorclose @p end
 *        select @o_flag=1 select @o_msg='ok' end GO
 */
public class SqlServerPaginationImp implements IPagination {
	public SqlServerPaginationImp() {

	}

	private static AppLogger logger = AppLogger
			.getInstance(SqlServerPaginationImp.class);

	/**
	 * ִ�з�ҳ��ѯ�����ز�ѯ��� CREATE procedure Sp_Pagination
	 * 
	 * @querySql varchar(8000),
	 * @pageNum int,
	 * @pageSize int,
	 * @o_flag int output,
	 * @o_msg varchar(50) output as begin set nocount on declare @p
	 *        int,@nRowCount int exec sp_cursoropen @p
	 *        output,@querySql,@scrollopt=2,@ccopt=335873,@rowcount=@nRowCount
	 *        output if(@p!=0) begin set @pageNum=(@pageNum-1)*@pageSize+1 exec
	 *        sp_cursorfetch @p,32,@pageNum,@pageSize exec sp_cursorclose @p end
	 *        select @o_flag=1 select @o_msg='ok' end GO
	 * 
	 * @param pInfo
	 *            PageParameter
	 * @return PageResult
	 * @throws PaginationException
	 * @todo Implement this
	 *       com.beetle.framework.persistence.pagination.IPagination method
	 */
	public PageResult page(PageParameter pInfo) throws PaginationException {
		Connection conn = null;
		PageResult pr = new PageResult();
		try {
			conn = ConnectionFactory.getConncetion(pInfo.getDataSourceName());
			int pos = pInfo.getPageNumber() - 1;
			if (pos >= 0) {
				ProcOperator proc = new SqlServerProcOperator();
				proc.setSql("Sp_Pagination");
				proc.setPresentConnection(conn);
				proc.setUseOnlyConnectionFlag(true);
				String usersql = pInfo.getUserSql();
				StringBuffer sb = new StringBuffer();
				if (usersql.indexOf("?") > 0) {
					StringTokenizer st = new StringTokenizer(usersql, "?");
					int i = 0;
					while (st.hasMoreTokens()) {
						sb.append(st.nextToken());
						if (i >= pInfo.getSqlParameters().size()) {
							break;
						}
						SqlParameter sp = (SqlParameter) pInfo
								.getSqlParameters().get(i);
						int type = sp.getType();
						if (type == SqlType.INTEGER || type == SqlType.BIGINT
								|| type == SqlType.DECIMAL
								|| type == SqlType.DOUBLE
								|| type == SqlType.FLOAT
								|| type == SqlType.NUMERIC
								|| type == SqlType.REAL
								|| type == SqlType.SMALLINT) {
							if (sp.getValue() != null) {
								sb.append(sp.getValue().toString());
							} else {
								sb.append("null");
							}
						} else {
							if (sp.getValue() != null) {
								sb.append("'");
								sb.append(sp.getValue().toString());
								sb.append("'");
							} else {
								sb.append("null");
							}
						}
						i++;
					}
				} else {
					sb.append(usersql);
				}
				String usql = sb.toString();
				// usql = usql.replaceAll("SELECT", "select");
				// usql = usql.replaceAll("FROM", "from");
				if (logger.isDebugEnabled()) {
					logger.debug("sql:" + usql);
				}
				proc.addParameter(new SqlParameter(SqlType.VARCHAR, usql));
				proc.addParameter(new SqlParameter(SqlType.INTEGER, Integer
						.valueOf(pInfo.getPageNumber())));
				proc.addParameter(new SqlParameter(SqlType.INTEGER, Integer
						.valueOf(pInfo.getPageSize())));
				proc.access();
				if (proc.getReturnFlag() == 1) {
					pr.setSqlResultSet(proc.getSqlResultSet());
					pr.setCurPageNumber(pInfo.getPageNumber() + 1);
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
			// e.printStackTrace();
			logger.error(e);
			throw new PaginationException(e);
		} finally {
			ConnectionFactory.closeConnection(conn);
			if (!pInfo.getSqlParameters().isEmpty()) {
				pInfo.getSqlParameters().clear();
			}
		}
	}

	/*
	 * private void dropTempTable(String tempTableName, Connection conn) throws
	 * DBOperatorException { UpdateOperator u2 = new UpdateOperator();
	 * u2.setPresentConnection(conn); u2.setUseOnlyConnectionFlag(true);
	 * u2.setSql("drop table " + tempTableName); u2.access(); }
	 */
	private static ICache countCache = new TimeOutCache(120);

	private int getRecordAmount(Connection conn, PageParameter pInfo)
			throws DBOperatorException {
		Integer amount;
		if (pInfo.isCacheRecordAmountFlag()) {
			Integer key = Integer.valueOf(pInfo.getUserSql().hashCode());
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

	private Integer caleAmount(Connection conn, PageParameter pInfo)
			throws NumberFormatException, DBOperatorException {
		Integer amount;
		String tempViewName = "##tmpView" + OtherUtil.randomInt(100, 10000);
		UpdateOperator u = new UpdateOperator();
		u.setPresentConnection(conn);
		u.setUseOnlyConnectionFlag(true);
		String tip = "into " + tempViewName;
		String tipSql = pInfo.getUserSql();
		if (tipSql.indexOf("from") > 0) {
			tipSql = tipSql.replaceAll("from", tip + " from");
		} else {
			tipSql = tipSql.replaceAll("FROM", tip + " FROM");
		}
		u.setSql(tipSql);
		//
		if (!pInfo.getSqlParameters().isEmpty()) {
			for (int i = 0; i < pInfo.getSqlParameters().size(); i++) {
				SqlParameter sp = (SqlParameter) pInfo.getSqlParameters()
						.get(i);
				u.addParameter(sp);
			}
		}
		//
		u.access();
		QueryOperator query = new QueryOperator();
		query.setUseOnlyConnectionFlag(true);
		query.setSql("select count(*) from " + tempViewName + " as c_t");
		query.setPresentConnection(conn);
		query.access();
		RsDataSet rs = new RsDataSet(query.getSqlResultSet());
		amount = new Integer(rs.getFieldValueAsString(0));
		rs.clearAll();
		UpdateOperator u2 = new UpdateOperator();
		u2.setPresentConnection(conn);
		u2.setUseOnlyConnectionFlag(true);
		u2.setSql("drop table " + tempViewName);
		u2.access();
		return amount;
	}

	public PageBaseInfo calc(PageParameter pInfo) throws PaginationException {
		Connection conn = null;
		PageBaseInfo pbi = new PageBaseInfo();
		try {
			conn = ConnectionFactory.getConncetion(pInfo.getDataSourceName());
			pbi.setPageSize(pInfo.getPageSize());
			pbi.setRecordAmount(getRecordAmount(conn, pInfo));
			pbi.setPageAmount(PageHelper.pageCount(pInfo.getPageSize(),
					pbi.getRecordAmount()));
		} catch (Throwable e) {
			logger.error(e);
			throw new PaginationException(e);
		} finally {
			ConnectionFactory.closeConnection(conn);
			if (!pInfo.getSqlParameters().isEmpty()) {
				pInfo.getSqlParameters().clear();
			}
		}
		return pbi;
	}
}
