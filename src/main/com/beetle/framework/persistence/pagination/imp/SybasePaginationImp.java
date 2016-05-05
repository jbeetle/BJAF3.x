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
 * create procedure Sp_Pagination
 * 
 * @querySql varchar(16384),
 * @pageNum int,
 * @pageSize int,
 * @tmpName varchar(50),
 * @o_flag int output,
 * @o_msg varchar(50) output as begin declare @rcount int declare @execsql
 *        varchar(16384) declare @disflag int select @rcount=@pageNum*@pageSize
 *        set rowcount @rcount select @disflag=charindex('distinct',@querySql)
 *        if(@disflag=0) begin select @execsql =
 *        stuff(@querySql,charindex('select',@querySql),6,'select
 *        sybid=identity(12),') end else begin select @querySql =
 *        stuff(@querySql,charindex('distinct',@querySql),8,'') select @execsql
 *        = stuff(@querySql,charindex('select',@querySql),6,'select distinct
 *        sybid=identity(12),') end select @execsql = stuff(@execsql,
 *        charindex('from',@execsql),4,'into '||@tmpName||' from') select @execsql
 *        = @execsql || ' select * from '||@tmpName||' where sybid>' ||
 *        convert(varchar,(@pageNum-1)*@pageSize) || ' and sybid <= ' ||
 *        convert(varchar,@pageNum*@pageSize) execute (@execsql) set rowcount 0
 *        select @o_flag=1 select @o_msg='ok' end
 * @author HenryYu (yuhaodong@gmail.com)
 * @version 1.0
 */
public class SybasePaginationImp implements IPagination {
	public SybasePaginationImp() {
		/* @querySql ��ѯ���, @pageNum ҳ��, @pageSize ÿҳ��¼����,@tmpName ��ʱ����� */
		/*
		 * create procedure Sp_Pagination
		 * 
		 * @querySql varchar(16384),
		 * 
		 * @pageNum int,
		 * 
		 * @pageSize int,
		 * 
		 * @tmpName varchar(50),
		 * 
		 * @o_flag int output,
		 * 
		 * @o_msg varchar(50) output as begin declare @rcount int declare
		 * 
		 * @execsql varchar(16384) declare @disflag int select
		 * 
		 * @rcount=@pageNum@pageSize set rowcount @rcount select
		 * 
		 * @disflag=charindex('distinct',@querySql) if(@disflag=0) begin select
		 * 
		 * @execsql = stuff(@querySql,charindex('select',@querySql),6,'select
		 * sybid=identity(12),') end else begin select @querySql =
		 * stuff(@querySql,charindex('distinct',@querySql),8,'') select @execsql
		 * = stuff(@querySql,charindex('select',@querySql),6,'select distinct
		 * sybid=identity(12),') end select @execsql = stuff(@execsql,
		 * charindex('from',@execsql),4,'into '||@tmpName||' from') select
		 * 
		 * @execsql = @execsql || ' select from '||@tmpName||' where sybid>' ||
		 * convert(varchar,(@pageNum-1)@pageSize) || ' and sybid <= ' ||
		 * convert(varchar,@pageNum@pageSize) execute (@execsql) set rowcount 0
		 * select @o_flag=1 select @o_msg='ok' end
		 */
	}

	private static AppLogger logger = AppLogger
			.getInstance(SybasePaginationImp.class);

	/**
	 * ִ�з�ҳ��ѯ�����ز�ѯ���
	 * 
	 * 
	 * @param pInfo
	 *            PageParameter
	 * @return PageResult
	 * @throws PaginationException
	 * @todo Implement this
	 *       com.beetle.framework.persistence.pagination.IPagination method
	 */
	public PageResult page(PageParameter pInfo) throws PaginationException {
		String tempTableName = "#tmpTable" + OtherUtil.randomInt(100, 10000);
		Connection conn = null;
		PageResult pr = new PageResult();
		try {
			conn = ConnectionFactory.getConncetion(pInfo.getDataSourceName());
			int pos = pInfo.getPageNumber() - 1;
			if (pos >= 0) {
				ProcOperator proc = new CommonProcOperator();
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
				usql = usql.replaceAll("SELECT", "select");
				usql = usql.replaceAll("FROM", "from");
				if (logger.isDebugEnabled()) {
					logger.debug("sql:" + usql);
				}
				proc.addParameter(new SqlParameter(SqlType.VARCHAR, usql));
				proc.addParameter(new SqlParameter(SqlType.INTEGER,
						new Integer(pInfo.getPageNumber())));
				proc.addParameter(new SqlParameter(SqlType.INTEGER, pInfo
						.getPageSize()));
				proc.addParameter(new SqlParameter(SqlType.VARCHAR,
						tempTableName));
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
			e.printStackTrace();
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

	private Integer caleAmount(Connection conn, PageParameter pInfo)
			throws NumberFormatException, DBOperatorException {
		Integer amount;
		String tempViewName = "#tmpView" + OtherUtil.randomInt(100, 10000);
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

	public static void main(String[] args) throws PaginationException {
		PageParameter pp = new PageParameter();
		pp.setCacheRecordAmountFlag(true);
		pp.setDataSourceName("xx");
		pp.setPageNumber(1);
		pp.setPageSize(5);
		pp.setUserSql("SELECT * FROM  fc_AccRecord2View where  group_id=?  and gen_mode=?");
		// pp.setUserSql(
		// "SELECT * FROM  fc_AccRecord2View where  group_id=?  and gen_mode=? and rec_status in (100)");
		pp.addParameter(new SqlParameter(SqlType.BIGINT, new Long(
				500000000000002l)));
		pp.addParameter(new SqlParameter(SqlType.INTEGER, new Integer(10)));
		SybasePaginationImp sp = new SybasePaginationImp();
		PageResult pr = sp.page(pp);
		System.out.println(pr.getRecordAmount());
		System.out.println(pr.getCurPageNumber());
		System.out.println(pr.getCurPageSize());
		RsDataSet rs = new RsDataSet(pr.getSqlResultSet());
		for (int i = 0; i < rs.rowCount; i++) {
			for (int j = 0; j < rs.colCount; j++) {
				System.out.println(rs.getFieldValue(j));
			}
			rs.next();
			System.out.println("---");
		}
		// PageDataList
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
