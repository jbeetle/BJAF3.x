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
package com.beetle.framework.resource.jta;

/**
 * <p>Title: BeetleSoft Framework</p>
 *
 * <p>Description: JTA事务接口</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: 甲壳虫软件</p>
 * @author 余浩东
 * @version 1.0
 */
public interface ITransaction {
  /**
   * 开始一个事务
   *
   * @throws JTAException
   */
  void begin() throws JTAException;

  /**
   * 提交事务
   *
   * @throws JTAException
   */
  void commit() throws JTAException;

  /**
   * 回滚事务
   *
   * @throws JTAException
   */
  void rollback() throws JTAException;

  /**
   * 设置回滚只读
   *
   * @throws JTAException
   */
  void setRollbackOnly() throws JTAException;

  /**
   * 返回事务目前的状态
   *
   * @throws JTAException
   * @return int
   */
  int getStatus() throws JTAException;

  /**
   * 设置事务超时时间
   *
   * @param timeout 秒
   * @throws JTAException
   */
  void setTransactionTimeout(int timeout) throws JTAException;

}
