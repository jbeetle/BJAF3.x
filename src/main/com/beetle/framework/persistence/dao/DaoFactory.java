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
package com.beetle.framework.persistence.dao;

import com.beetle.framework.resource.dic.DIContainer;

/**
 * <p>
 * Title: 框架设计
 * </p>
 * <p>
 * Description: DAO对象工厂
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
public class DaoFactory {

	private DaoFactory() {
	}

	public static void initialize() {
		// 暂不支持初始化
	}

	/**
	 * 直接通过DAO接口或其实现类来获取一个DAO接口实现对象<br>
	 * 
	 * @param daoFaceOrImpClass
	 *            Dao接口或其实现类
	 * 
	 * @return Object
	 * @throws DaoFactoryException
	 */
	public static <T> T getDaoObject(Class<T> daoFace)
			throws DaoFactoryException {
		return DIContainer.getInstance().retrieve(daoFace);
	}

	/*
	 * 根据接口名称获取对象，为了兼容保留的方法，不推荐使用，请使用‘<T> T getDaoObject(Class<T>
	 * daofaceClass)’方法代替
	 */
	@Deprecated
	public static Object getDaoObject(String interFaceName)
			throws DaoFactoryException {
		try {
			return DIContainer.getInstance().retrieve(
					Class.forName(interFaceName));
		} catch (ClassNotFoundException e) {
			throw new DaoFactoryException(e);
		}
	}

}
