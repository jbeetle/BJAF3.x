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
 * <p>数据库访问方式接口 </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 余浩东
 * @version 1.0
 */
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface IAccessManner {
  /**
   * 通过PreStatement方式访问数据库（SQL语句）
   *
   * @param conn 数据库连接
   * @return PreparedStatement对象
   * @throws SQLException
   */
  PreparedStatement accessByPreStatement(Connection conn) throws
      SQLException;

  /**
   * 通过CallableStatement访问数据库（主要是存储过程）
   *
   * @param conn 数据库访问连接
   * @return CallableStatement
   * @throws SQLException
   */
  CallableStatement accessByCallableStatement(Connection conn) throws
      SQLException;
}
