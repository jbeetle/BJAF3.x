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
package com.beetle.framework.web.controller.draw;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.beetle.framework.util.ClassUtil;
import com.beetle.framework.util.cache.ICache;
import com.beetle.framework.util.cache.StrongCache;
import com.beetle.framework.web.common.CommonUtil;
import com.beetle.framework.web.common.WebConst;
import com.beetle.framework.web.controller.ControllerFactory;

/**
 * <p>
 * Title: BeetleSoft Framework
 * </p>
 * 
 * <p>
 * Description: J2EE系统开发框架
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: 甲壳虫软件
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class DrawFactory {
	private static Map<String, String> drawTable = new ConcurrentHashMap<String, String>();
	private static ICache cacheDraw = new StrongCache();
	private static boolean initFlag = false;

	public static Map<String, String> getDrawConfig(ServletContext app) {
		if (!initFlag) {
			loadDrawTable(app);
		}
		return drawTable;
	}

	public static boolean isDrawController(String url, ServletContext app) {
		if (!initFlag) {
			loadDrawTable(app);
			initFlag = true;
		}
		return drawTable.containsKey(url);
	}

	private static synchronized void loadDrawTable(ServletContext app) {
		initFlag = true;
		CommonUtil.fill_DataMap(app, WebConst.WEB_CONTROLLER_FILENAME,
				"mappings.controllers.drawing", "dItem", "name", "class",
				drawTable);
		Map<String, String> mItem = ControllerFactory.getModuleItem(app);
		// 加载其它文件的数据
		if (!mItem.isEmpty()) {
			Iterator<?> it = mItem.entrySet().iterator();
			while (it.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry e = (Map.Entry) it.next();
				String fn = (String) e.getKey();
				String active = (String) e.getValue();
				if (active.equalsIgnoreCase("true")) {
					CommonUtil.fill_DataMap(app, fn,
							"mappings.controllers.drawing", "dItem", "name",
							"class", drawTable);
				}
			}
		}
	}

	public static IDraw getDrawInstance(String url, ServletContext app,
			String zoreImpClass) throws ServletException {
		if (!initFlag) {
			loadDrawTable(app);
		}
		Object drawing = cacheDraw.get(url);
		if (drawing == null) {
			String className = (String) drawTable.get(url);
			if (className == null) {
				className = zoreImpClass;
				drawTable.put(url, className);
			}
			try {
				drawing = Class.forName(className.trim()).newInstance();
				IDraw imp = (IDraw) drawing;
				// 判别此控制器对象是否需要缓存
				if (ClassUtil.isThreadSafe(imp.getClass())) {
					cacheDraw.put(url, drawing);
				}
			} catch (Exception e) {
				drawing = null;
				throw new ServletException(e);
			}
		}
		return (IDraw) drawing;
	}

}
