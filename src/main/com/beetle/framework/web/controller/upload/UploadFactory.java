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
package com.beetle.framework.web.controller.upload;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.beetle.framework.util.ClassUtil;
import com.beetle.framework.util.cache.ICache;
import com.beetle.framework.util.cache.StrongCache;
import com.beetle.framework.web.common.CommonUtil;
import com.beetle.framework.web.common.WebConst;
import com.beetle.framework.web.controller.ControllerFactory;

public class UploadFactory {
	private static  Map<String, String> uploadTable = new ConcurrentHashMap<String, String>();
	private static ICache cacheUpload = new StrongCache();
private final static Object locker = new Object();
	
	public static Map<String, String> getUploadConfig(ServletContext app) {
		loadUploadTable(app);
		return uploadTable;
	}

	private static void loadUploadTable(ServletContext app) {
		//
		if (!uploadTable.isEmpty()) {
			return;
		}
		synchronized (locker) {
			CommonUtil.fill_DataMap(app, WebConst.WEB_CONTROLLER_FILENAME,
					"mappings.controllers.upload", "uItem", "name", "class",
					uploadTable);
			Map<String, String> mItem = ControllerFactory.getModuleItem(app);
			// 加载其它文件的数据

			if (!mItem.isEmpty()) {
				Set<?> s = mItem.entrySet();
				Iterator<?> it = s.iterator();
				while (it.hasNext()) {
					@SuppressWarnings("rawtypes")
					Map.Entry e = (Map.Entry) it.next();
					String fn = (String) e.getKey();
					String active = (String) e.getValue();
					if (active.equalsIgnoreCase("true")) {
						CommonUtil.fill_DataMap(app, fn,
								"mappings.controllers.upload", "uItem", "name",
								"class", uploadTable);
					}
				}
			}
		}
	}

	public static boolean isUploadController(String url, ServletContext app) {
		loadUploadTable(app);
		return uploadTable.containsKey(url);
	}

	public static IUpload getUploadInstance(String url, String zoreImpClass)
			throws ServletException {
		Object upload = cacheUpload.get(url);
		if (upload == null) {
			String className = (String) uploadTable.get(url);
			if (className == null) {
				className = zoreImpClass;
				uploadTable.put(url, className);
			}
			try {
				upload = Class.forName(className.trim()).newInstance();
				IUpload imp = (IUpload) upload;
				if (ClassUtil.isThreadSafe(imp.getClass())) { // 判别此控制器对象是否需要缓存
					cacheUpload.put(url, upload);
				}
				/*
				 * if (imp.cacheFlag) { cacheUpload.put(url, upload); }
				 */
			} catch (Exception e) {
				upload = null;
				throw new ServletException(e);
			}
		}
		return (IUpload) upload;
	}
}
