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
package com.beetle.framework.persistence.seq.imp;

import com.beetle.framework.persistence.access.ConnectionFactory;
import com.beetle.framework.persistence.access.DBHelper;
import com.beetle.framework.persistence.access.operator.*;
import com.beetle.framework.persistence.seq.ISequence;
import com.beetle.framework.persistence.seq.SeqType;
import com.beetle.framework.persistence.seq.SeqType.SeqImpType;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * <p>
 * Title: BeetleSoft Framework
 * </p>
 * 
 * <p>
 * Description:由于CommonSeqGenerator的获取种子方法在一个完整的事务内完成，
 * 为了避免嵌套事务的发生，建议专门建立一个"no-tx-datasource"数据源为此类服务<br>
 * <b>create table SEQUENCE ( NAME varchar(30) not null, NEXTID DECIMAL(20,0),
 * primary key (NAME) );</b>
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: 甲壳虫软件
 * 
 * </p>
 * 
 * @author 余浩东
 * 
 * @version 1.0
 */
public class CommonSeqGenerator implements ISequence {
	private static CommonSeqGenerator generator = new CommonSeqGenerator();

	private static final String selectSql = "select nextid from sequence where name=?"; // for

	// update";
	private static final String selectSql_forupdate = "select nextid from sequence where name=? for update";

	private static final String selectSql_fordb2 = "select nextid from sequence where name=? for update with rs";

	private static final String selectSql_forsqlserver = "select nextid from with (rowlock) sequence where name=?";

	private static final String updateSql = "update sequence set nextid=nextid+1 where name=? ";

	private CommonSeqGenerator() {
	}

	public static ISequence getInstance() {
		return generator;
	}

	/**
	 * nextSequenceNum
	 * 
	 * @param seqtype
	 *            SeqType
	 * @return long
	 * @todo Implement this com.beetle.framework.persistence.dao.ISequence
	 *       method
	 */
	public synchronized long nextSequenceNum(SeqType seqtype) {
		return getNext(seqtype);
	}

	private long getNext(SeqType seqtype) {
		Connection conn = null;
		long r;
		try {
			conn = ConnectionFactory.newDsConncetion(seqtype
					.getDataSourceName());
			conn.setAutoCommit(false);
			int fu_flag = 0;
			String dbname = DBHelper.getDBMSName(conn);
			if (dbname.equalsIgnoreCase("oracle")) {// for oracle
				fu_flag = 1;
			} else if (dbname.equalsIgnoreCase("mysql")) {
				fu_flag = 1;
			} else if (dbname.equalsIgnoreCase("sybase")) {// ?
				fu_flag = 1;
			} else if (dbname.equalsIgnoreCase("db2")) {// ?
				fu_flag = 3;
			} else if (dbname.equalsIgnoreCase("Microsoft SQL Server")) {
				fu_flag = 2;
			}
			QueryOperator query = new QueryOperator();
			query.setUseOnlyConnectionFlag(true);
			if (fu_flag == 1) {
				query.setSql(selectSql_forupdate);
			} else if (fu_flag == 2) {// sqlserver
				query.setSql(selectSql_forsqlserver);
			} else if (fu_flag == 3) {
				query.setSql(selectSql_fordb2);
			} else {
				query.setSql(selectSql);
			}
			query.setPresentConnection(conn);
			query.addParameter(new SqlParameter(SqlType.VARCHAR, seqtype
					.getSequenceName()));
			query.access();
			RsDataSet rs = new RsDataSet(query.getSqlResultSet());
			if (rs.rowCount > 0) {
				try {
					r = rs.getFieldValueAsLong("nextid").longValue();
				} catch (java.lang.ClassCastException cce) {
					r = rs.getFieldValueAsInteger("nextid").longValue();
				}
				rs.clearAll();
				UpdateOperator update = new UpdateOperator();
				update.setUseOnlyConnectionFlag(true);
				update.setPresentConnection(conn);
				update.setSql(updateSql);
				update.addParameter(new SqlParameter(SqlType.VARCHAR, seqtype
						.getSequenceName()));
				update.access();
				conn.commit();
			} else {
				conn.commit();
				throw new com.beetle.framework.AppRuntimeException(
						"没有找到记录，请检查数据库sequence表是否存在相应的种子记录。");
			}
			return r;
		} catch (DBOperatorException e) {
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException e1) {
			}
			throw new com.beetle.framework.AppRuntimeException("access err!", e);
		} catch (SQLException e) {
			throw new com.beetle.framework.AppRuntimeException(
					"connection err!", e);
		} finally {
			ConnectionFactory.closeConnection(conn);
		}
	}

	public SeqImpType getImpType() {
		return SeqType.SeqImpType.Common;
	}

	public void initSequenceValue(int initValue, SeqType seqtype) {
		UpdateOperator update = new UpdateOperator();
		update.setDataSourceName(seqtype.getDataSourceName());
		update.setSql("update sequence set nextid=? where name=?");
		update.addParameter(initValue);
		update.addParameter(seqtype.getSequenceName());
		try {
			update.access();
			if (update.getEffectCounts() == 0) {
				UpdateOperator u = new UpdateOperator();
				u.setDataSourceName(seqtype.getDataSourceName());
				u.setSql("insert into sequence (nextid,name) values (?,?)");
				u.addParameter(initValue);
				u.addParameter(seqtype.getSequenceName());
				u.access();
			}
		} catch (DBOperatorException ex) {
			throw new com.beetle.framework.AppRuntimeException("访问出现异常", ex);
		}
	}

}
