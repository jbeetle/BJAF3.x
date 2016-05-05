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
package com.beetle.framework.business.command.imp.ejb;

import com.beetle.framework.AppProperties;
import com.beetle.framework.business.common.ejb.EJBFactoryException;
import com.beetle.framework.business.common.ejb.HomeFactoryHelper;
import com.beetle.framework.business.common.ejb.IEJBHomeFactory;
import com.beetle.framework.resource.container.ContainerConfig;
import com.beetle.framework.util.OtherUtil;

import java.rmi.RemoteException;

/**
 * <p>
 * Title: FrameWork
 * </p>
 * <p>
 * Description: 系统框架项目
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: 甲壳虫软件
 * 
 * </p>
 * 
 * @author 余浩东
 * 
 * @version 1.0
 */

public class CommandServerFactory implements IEJBHomeFactory {
	private static CommandServerFactory f = new CommandServerFactory();

	private static CommandServer cmdSrv = null;

	private CommandServerFactory() {
		remoteObjCacheFlag = isRemoteObjCacheMark();
	}

	public static CommandServer getCommandServer() throws EJBFactoryException {
		try {
			if (remoteObjCacheFlag) {
				if (cmdSrv == null) {
					cmdSrv = ((CommandServerHome) f.createRemoteHomeObject())
							.create();
				}
				return cmdSrv;
			} else {
				return ((CommandServerHome) f.createRemoteHomeObject())
						.create();
			}
		} catch (Exception e) {
			throw new EJBFactoryException(e);
		}
	}

	private static boolean remoteObjCacheFlag = false;

	public boolean isRemoteObjCacheMark() {
		String flag = AppProperties.get("command_ejb_remote_object_cache");
		if (flag != null && !flag.equals("")) {
			if (flag.equalsIgnoreCase("true")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static CommandServerLocal getCommandServerLocal()
			throws EJBFactoryException {
		try {
			return ((CommandServerLocalHome) f.createLocalHomeObject())
					.create();
		} catch (Exception e) {
			throw new EJBFactoryException(e);
		}
	}

	public Object createRemoteHomeObject() throws EJBFactoryException {
		try {
			return HomeFactoryHelper.findRemoteHome(null,
					AppProperties.get("command_ejb_jndi_name"),
					CommandServerHome.class);
		} catch (RemoteException e) {// 查找home出现异常，根据group定义获取其它容器上的home
			Object o = getOtherHomeObject();
			if (o != null) {
				return o;
			} else {
				throw new EJBFactoryException(e);
			}
		} catch (ClassNotFoundException e) {
			throw new EJBFactoryException(e);
		}

	}

	private static Object getOtherHomeObject() {
		Object o = null;
		String gnames = ContainerConfig.getGroupNames("default");
		if (gnames != null && gnames.equals("")) {
			String tgns[] = gnames.split("#");
			for (int i = 0; i < tgns.length; i++) {
				try {
					o = HomeFactoryHelper.findRemoteHome(tgns[i],
							AppProperties.get("command_ejb_jndi_name"),
							CommandServerHome.class);
					if (o != null) {
						break;
					}
				} catch (Exception e) {
					o = null;
					e.printStackTrace();
				}
			}
			OtherUtil.clearArray(tgns);
		}
		return o;
	}

	public Object createLocalHomeObject() throws EJBFactoryException {
		try {
			return HomeFactoryHelper.findLocalHome(AppProperties
					.get("command_ejb_local_jndi_name"));
		} catch (Exception e) {
			throw new EJBFactoryException(e);
		}

	}

}
