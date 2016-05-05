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
package com.beetle.framework.persistence.access.base;

/**
 * <p>Title: </p>
 * <p>Description: 数据库访问方法实现类</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 余浩东
 * @version 1.0
 */

import com.beetle.framework.persistence.access.ConnectionException;
import com.beetle.framework.persistence.access.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBAccess {

	public static void queryForOneConnection(IAccessManner selectAccessManner,
			ResultSetHandler handlerImp, Connection con, int maxRow)
			throws DBAccessException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = selectAccessManner.accessByPreStatement(con);
			rs = ps.executeQuery();
			if (maxRow <= 0) {
				while (rs.next()) {
					handlerImp.handle(rs); // 处理结果集
				}
			} else {
				int i = 0;
				while (rs.next()) {
					handlerImp.handle(rs);
					i++;
					if (i > maxRow) {
						break;
					}
				}
			}
		} catch (SQLException sqle) {
			throw new DBAccessException("execute sql err!", sqle);
		} finally {
			try {
				ConnectionFactory.closeAll(null, ps, rs);
			} catch (ConnectionException ce) {
			}
		}
	}

	public static void query(IAccessManner selectAccessManner,
			ResultSetHandler handlerImp, Connection con, int maxRow)
			throws DBAccessException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = selectAccessManner.accessByPreStatement(con);
			rs = ps.executeQuery();
			if (maxRow <= 0) {
				while (rs.next()) {
					handlerImp.handle(rs); // 处理结果集
				}
			} else {
				int i = 0;
				while (rs.next()) {
					handlerImp.handle(rs);
					i++;
					if (i > maxRow) {
						break;
					}
				}
			}
		} catch (SQLException sqle) {
			// logger.error("run[" + selectAccessManner + "]err", sqle);
			throw new DBAccessException("execute sql err!", sqle);
		} finally {
			try {
				ConnectionFactory.closeAll(con, ps, rs);
			} catch (ConnectionException ce) {
			}
		}
	}

	public static int[] updateBatch(IAccessManner updateAccessManner,
			Connection connection) throws DBAccessException {
		return updateBatchImp(updateAccessManner, connection);
	}

	public static int[] updateBatchForOneConnection(
			IAccessManner updateAccessManner, Connection con)
			throws DBAccessException {
		PreparedStatement ps = null;
		try {
			ps = updateAccessManner.accessByPreStatement(con);
			int r[] = ps.executeBatch();
			ps.clearBatch();
			return r;
		} catch (SQLException sqle) {
			throw new DBAccessException("execute sql err!", sqle);
		} finally {
			try {
				ConnectionFactory.closeAll(null, ps, null);
			} catch (ConnectionException ce) {
			}
		}
	}

	private static int[] updateBatchImp(IAccessManner updateAccessManner,
			Connection con) throws DBAccessException {
		PreparedStatement ps = null;
		try {
			ps = updateAccessManner.accessByPreStatement(con);
			int r[] = ps.executeBatch();
			ps.clearBatch();
			return r;
		} catch (SQLException sqle) {
			throw new DBAccessException("execute sql err!", sqle);
		} finally {
			try {
				ConnectionFactory.closeAll(con, ps, null);
			} catch (ConnectionException ce) {
			}
		}
	}

	public static int updateForOneConnection(IAccessManner updateAccessManner,
			Connection con) throws DBAccessException {
		int reRowNum;
		PreparedStatement ps = null;
		try {
			ps = updateAccessManner.accessByPreStatement(con);
			reRowNum = ps.executeUpdate();
			return reRowNum;
		} catch (SQLException sqle) {
			throw new DBAccessException("execute sql err!", sqle);
		} finally {
			try {
				ConnectionFactory.closeAll(null, ps, null);
			} catch (ConnectionException ce) {
			}
		}
	}

	public static int update(IAccessManner updateAccessManner,
			Connection connection) throws DBAccessException {
		return updateImp(updateAccessManner, connection);
	}

	private static int updateImp(IAccessManner updateAccessManner,
			Connection con) throws DBAccessException {
		int reRowNum;
		PreparedStatement ps = null;
		try {
			ps = updateAccessManner.accessByPreStatement(con);
			reRowNum = ps.executeUpdate();
			return reRowNum;
		} catch (SQLException sqle) {
			throw new DBAccessException("execute sql err!", sqle);
		} finally {
			try {
				ConnectionFactory.closeAll(con, ps, null);
			} catch (ConnectionException ce) {
			}
		}
	}

}
