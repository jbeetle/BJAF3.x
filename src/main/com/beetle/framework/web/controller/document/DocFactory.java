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
package com.beetle.framework.web.controller.document;

import com.beetle.framework.util.ClassUtil;
import com.beetle.framework.util.cache.ICache;
import com.beetle.framework.util.cache.StrongCache;
import com.beetle.framework.web.common.CommonUtil;
import com.beetle.framework.web.common.WebConst;
import com.beetle.framework.web.controller.ControllerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
public class DocFactory {
	private  static Map<String, String> docTable = new HashMap<String, String>();
	private static ICache cacheDoc = new StrongCache();
private final static Object locker = new Object();

	public static Map<String, String> getDocConfig(ServletContext app) {
		loadDocInfo(app);
		return docTable;
	}

	public static boolean isDocController(String url, ServletContext app) {
		loadDocInfo(app);
		return docTable.containsKey(url);
	}

	private static void loadDocInfo(ServletContext app) {
		if (!docTable.isEmpty()) {
			return;
		}
		synchronized (locker) {
			CommonUtil.fill_DataMap(app, WebConst.WEB_CONTROLLER_FILENAME,
					"mappings.controllers.document", "docItem", "name",
					"class", docTable);
			Map<?, ?> mItem = ControllerFactory.getModuleItem(app);
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
								"mappings.controllers.document", "docItem",
								"name", "class", docTable);
					}
				}
			}
		}
	}

	public static IDocument getDocInstance(String url, ServletContext app,
			String zoreImpClass) throws ServletException {
		loadDocInfo(app);
		Object doc = cacheDoc.get(url);
		if (doc == null) {
			String className = (String) docTable.get(url);
			if (className == null) {
				className = zoreImpClass;
				docTable.put(url, className);
			}
			try {
				doc = (IDocument) Class.forName(className.trim()).newInstance();
				// 判别此控制器对象是否需要缓存
				if (ClassUtil.isThreadSafe(doc.getClass())) {
					cacheDoc.put(url, doc);
				}
			} catch (Exception e) {
				doc = null;
				throw new ServletException(e);
			}
		}
		return (IDocument) doc;
	}

}
