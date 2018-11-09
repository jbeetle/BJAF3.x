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

import com.beetle.framework.log.AppLogger;

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

import org.slf4j.Logger;

public class DBAccess {
	// 添加数据库异常日志，很多时候外部调用者并不捕捉异常，导致有时候很难定位问题，所以框架层面做必要的记录以便跟进问题,2017-1-11
	private static final Logger logger = AppLogger.getLogger(DBAccess.class);

	public static void queryForOneConnection(IAccessManner selectAccessManner, ResultSetHandler handlerImp,
			Connection con, int maxRow) throws DBAccessException {
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
			logger.error(sqle.getMessage(), sqle);
			throw new DBAccessException(-1002, "execute sql err!", sqle);
		} finally {
			try {
				ConnectionFactory.closeAll(null, ps, rs);
			} catch (ConnectionException ce) {
			}
		}
	}

	public static void query(IAccessManner selectAccessManner, ResultSetHandler handlerImp, Connection con, int maxRow)
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
			logger.error(sqle.getMessage(), sqle);
			throw new DBAccessException(-1002, "execute sql err!", sqle);
		} finally {
			try {
				ConnectionFactory.closeAll(con, ps, rs);
			} catch (ConnectionException ce) {
			}
		}
	}

	public static int[] updateBatch(IAccessManner updateAccessManner, Connection connection) throws DBAccessException {
		return updateBatchImp(updateAccessManner, connection);
	}

	public static int[] updateBatchForOneConnection(IAccessManner updateAccessManner, Connection con)
			throws DBAccessException {
		PreparedStatement ps = null;
		try {
			ps = updateAccessManner.accessByPreStatement(con);
			int r[] = ps.executeBatch();
			ps.clearBatch();
			return r;
		} catch (SQLException sqle) {
			logger.error(sqle.getMessage(), sqle);
			throw new DBAccessException(-1002, "execute sql err!", sqle);
		} finally {
			try {
				ConnectionFactory.closeAll(null, ps, null);
			} catch (ConnectionException ce) {
			}
		}
	}

	private static int[] updateBatchImp(IAccessManner updateAccessManner, Connection con) throws DBAccessException {
		PreparedStatement ps = null;
		try {
			ps = updateAccessManner.accessByPreStatement(con);
			int r[] = ps.executeBatch();
			ps.clearBatch();
			return r;
		} catch (SQLException sqle) {
			logger.error(sqle.getMessage(), sqle);
			throw new DBAccessException(-1002, "execute sql err!", sqle);
		} finally {
			try {
				ConnectionFactory.closeAll(con, ps, null);
			} catch (ConnectionException ce) {
			}
		}
	}

	public static int updateForOneConnection(IAccessManner updateAccessManner, Connection con)
			throws DBAccessException {
		int reRowNum;
		PreparedStatement ps = null;
		try {
			ps = updateAccessManner.accessByPreStatement(con);
			reRowNum = ps.executeUpdate();
			return reRowNum;
		} catch (SQLException sqle) {
			logger.error(sqle.getMessage(), sqle);
			throw new DBAccessException(-1002, "execute sql err!", sqle);
		} finally {
			try {
				ConnectionFactory.closeAll(null, ps, null);
			} catch (ConnectionException ce) {
			}
		}
	}

	public static Long updateReturnKey(IAccessManner updateAccessManner, Connection connection)
			throws DBAccessException {
		return updateImp2(updateAccessManner, connection);
	}

	private static Long updateImp2(IAccessManner updateAccessManner, Connection con) throws DBAccessException {
		Long id = -1l;
		PreparedStatement ps = null;
		try {
			ps = updateAccessManner.accessByPreStatement(con);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				// 知其仅有一列，故获取第一列
				id = rs.getLong(1);
			}
			return id;
		} catch (SQLException sqle) {
			logger.error(sqle.getMessage(), sqle);
			throw new DBAccessException(-1002, "execute sql err!", sqle);
		} finally {
			try {
				ConnectionFactory.closeAll(con, ps, null);
			} catch (ConnectionException ce) {
			}
		}
	}

	public static int update(IAccessManner updateAccessManner, Connection connection) throws DBAccessException {
		return updateImp(updateAccessManner, connection);
	}

	private static int updateImp(IAccessManner updateAccessManner, Connection con) throws DBAccessException {
		int reRowNum;
		PreparedStatement ps = null;
		try {
			ps = updateAccessManner.accessByPreStatement(con);
			reRowNum = ps.executeUpdate();
			return reRowNum;
		} catch (SQLException sqle) {
			logger.error(sqle.getMessage(), sqle);
			throw new DBAccessException(-1002, "execute sql err!", sqle);
		} finally {
			try {
				ConnectionFactory.closeAll(con, ps, null);
			} catch (ConnectionException ce) {
			}
		}
	}

}
