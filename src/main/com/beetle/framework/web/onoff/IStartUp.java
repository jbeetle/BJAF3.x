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
package com.beetle.framework.web.onoff;

import javax.servlet.ServletContext;

/**
 * web apllication统一的初始化（启动）接口 依赖于Globaldispatchservlet总指派servlet，
 * 
 * 同时需要在<load-on-startup>标签配置初始化线程数（至少大于1）才能工作，例如：
 * 
 * <servlet> <servlet-name>Globaldispatchservlet</servlet-name>
 * <servlet-class>com.beetle.framework.web.GlobalDispatchServlet</servlet-class>
 * <load-on-startup>3</load-on-startup> </servlet>
 * 
 * @author 余浩东(hdyu@beetlesoft.net)
 * @version 1.0
 */
public interface IStartUp {
	/**
	 * startUp
	 */
	void startUp(ServletContext application);
}
