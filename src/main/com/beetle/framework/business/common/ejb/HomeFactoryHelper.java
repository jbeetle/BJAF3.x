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

import com.beetle.framework.log.AppLogger;
import com.beetle.framework.resource.container.ContainerUtil;

import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Title: 框架设计
 * </p>
 * <p>
 * Description: EJBHome创建工厂帮助类，具备缓存home接口的功能
 * 
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: 甲壳虫科技
 * </p>
 * 
 * @author 余浩东
 * 
 * @version 1.0
 */

public class HomeFactoryHelper {
	private final static Map<String, EJBHome> HOME_INTERFACES_REMOTE = new HashMap<String, EJBHome>();
	private static AppLogger logger = AppLogger
			.getInstance(HomeFactoryHelper.class);
	private static Map<String, EJBLocalHome> HOME_INTERFACES_LOCAL = new HashMap<String, EJBLocalHome>();

	/**
	 * 找出远程Home对象
	 * 
	 * @param ejbContainerTagName
	 *            容器标签名称，在SysConfig.xml中配置
	 * 
	 * @param jndiName
	 *            ejb的jndi名称
	 * @param className
	 *            远程home对象的Class
	 * @throws RemoteException
	 * @throws ClassNotFoundException
	 * @throws NamingException
	 * @return EJBHome
	 */
	public static EJBHome findRemoteHome(String ejbContainerTagName,
			String jndiName, Class<?> className) throws java.rmi.RemoteException,
			ClassNotFoundException {
		String key = ejbContainerTagName + className;
		EJBHome ejbHome = HOME_INTERFACES_REMOTE.get(key);
		if (ejbHome == null) {
			ejbHome = getRemoteHome(ejbContainerTagName, jndiName, className,
					key);
		}
		return ejbHome;
	}

	/**
	 * 找出本地Home对象
	 * 
	 * @param jndiName
	 *            ejb的本地jndi名称
	 * @return EJBLocalHome
	 */
	public static EJBLocalHome findLocalHome(String jndiName) {
		EJBLocalHome ejbLocalHome = (EJBLocalHome) HOME_INTERFACES_LOCAL
				.get(jndiName);
		if (ejbLocalHome == null) {
			ejbLocalHome = getLocalHome(jndiName);
		}
		return ejbLocalHome;

	}

	/**
	 * 从缓存中删除home接口对象
	 * 
	 * @param ejbContainerTagName容器标签名称
	 *            ，在SysConfig.xml中配置
	 * 
	 * @param className远程home对象的Class
	 */
	public static void removeRemoteHomeFromCache(String ejbContainerTagName,
			Class<?> className) {
		String key = ejbContainerTagName + className;
		HOME_INTERFACES_REMOTE.remove(key);
	}

	/**
	 * 从缓存中删除home对象
	 * 
	 * @param jndiName
	 */
	public static void removeLocalHomeFromCache(String jndiName) {
		HOME_INTERFACES_LOCAL.remove(jndiName);
	}

	private static synchronized EJBHome getRemoteHome(
			String ejbContainerTagName, String jndiName, Class<?> className,
			String key) throws java.rmi.RemoteException, ClassNotFoundException {
		Context ctx;
		if (logger.isDebugEnabled()) {
			logger.debug("--getRemoteHome--");
			logger.debug("ejbContainerTagName:" + ejbContainerTagName);
			logger.debug("jndiName:" + jndiName);
			logger.debug("className:" + className);
			logger.debug("key:" + key);
		}
		try {
			if (ejbContainerTagName == null || ejbContainerTagName.equals("")) {
				ctx = ContainerUtil.getRemoteEJBContainerContext();
				if (logger.isDebugEnabled()) {
					logger.debug("call:getRemoteEJBContainerContext()");
				}
			} else {
				ctx = ContainerUtil
						.getRemoteEJBContainerContext(ejbContainerTagName);
				if (logger.isDebugEnabled()) {
					logger.debug("call:getRemoteEJBContainerContext("
							+ ejbContainerTagName + ")");
				}
			}
			Object ref = ctx.lookup(jndiName.trim());
			EJBHome ejbHome = (EJBHome) PortableRemoteObject.narrow(ref,
					className);
			HOME_INTERFACES_REMOTE.put(key, ejbHome);
			return ejbHome;
		} catch (NamingException e) {
			throw new RemoteException("getRemoteHome err", e);
		}
	}

	private static synchronized EJBLocalHome getLocalHome(String jndiName) {
		try {
			Context ctx = ContainerUtil.getLocalEJBContainerContext();
			EJBLocalHome ejbLocalHome = (EJBLocalHome) ctx.lookup(jndiName);
			HOME_INTERFACES_LOCAL.put(jndiName, ejbLocalHome);
			return ejbLocalHome;
		} catch (NamingException e) {
			e.printStackTrace();
			if (HOME_INTERFACES_LOCAL.containsKey(jndiName)) {
				HOME_INTERFACES_LOCAL.remove(jndiName);
			}
			return null;
		}

	}

}
