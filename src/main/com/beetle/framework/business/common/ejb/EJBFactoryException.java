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
package com.beetle.framework.business.common.ejb;

import com.beetle.framework.AppException;

/**
 * <p>Title: BeetleSoft Framework</p>
 * <p>Description: EJBFactoryException</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: 甲壳虫软件</p>
 * @author 余浩东
 * @version 1.0
 */

public class EJBFactoryException
    extends AppException {
  private static final long serialVersionUID = -19760224l;

  public EJBFactoryException(String p0, Throwable p1) {
    super(p0, p1);
  }

  public EJBFactoryException(Throwable p0) {
    super(p0);
  }

  public EJBFactoryException(String p0) {
    super(p0);
  }

}
