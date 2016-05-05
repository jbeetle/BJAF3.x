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
package com.beetle.framework.web.controller;

import com.beetle.framework.web.view.View;

/**
 * <p>Title: BeetleWeb</p>
 *
 * <p>IControllerGlobalPreCall需要在WebController.xml的标签&lt;globalBackCall&gt;配置实现类</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: 甲壳虫软件</p>
 *
 * @author 余浩东(hdyu@beetlesoft.net)
 * @version 1.0
 */
public interface ICutBackAction {
  /**
   * 执行全局后置回调
   *
   * @param webInput WebInput
   * @return 返回一个扩展View视图,如果View为null代表不处理，流程继续往下走
   * @throws ControllerException
   */
  View act(WebInput webInput) throws ControllerException;

}
