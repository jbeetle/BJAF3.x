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
package com.beetle.framework.web.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.util.cache.ICache;
import com.beetle.framework.util.cache.WeakCache;
import com.beetle.framework.util.file.XMLReader;
import com.beetle.framework.web.common.CommonUtil;
import com.beetle.framework.web.common.WebConst;

import freemarker.template.Template;

public class ViewFactory {
	private static ICache templateCache = new WeakCache();
	private static Map<String, String> moduleItemMap = new ConcurrentHashMap<String, String>(); // 存储模块项
	private static Map<String, String> viewCache = new ConcurrentHashMap<String, String>();
	private final static Object locker = new Object();
	private static AppLogger logger = AppLogger.getInstance(ViewFactory.class);

	public final static void dealWithFreeMarkerFtl(ServletContext app,
			HttpServletRequest request, HttpServletResponse response,
			String url, String viewName, Map<String, Object> viewData) {
		Template t = getTemplate(app, url, viewName);
		genContent(response, viewData, t, request);
	}

	private static Template getTemplate(ServletContext app, String url,
			String viewName) {
		Object obj = templateCache.get(viewName);
		if (obj != null) {
			return (Template) obj;
		}
		synchronized (locker) {
			Object obj2 = templateCache.get(viewName);
			if (obj2 != null) {
				return (Template) obj2;
			}
			Template t = null;
			Reader reader = new InputStreamReader(app.getResourceAsStream(url));
			try {
				t = new Template(url, reader, null);
				templateCache.put(viewName, t);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
						reader = null;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return t;
		}
	}

	private static void genContent(HttpServletResponse response,
			Map<String, Object> viewData, Template t, HttpServletRequest request) {
		// Merge the data-model and the template
		try {
			Writer out = response.getWriter();
			t.setEncoding((String) request
					.getAttribute(CommonUtil.WEB_ENCODE_CHARSET));
			t.process(viewData, out);
		} catch (Exception e) {
			System.err.print("Error while processing FreeMarker template");
			e.printStackTrace();
		} finally {
			viewData.clear();
		}
	}

	/**
	 * 把模型返回的数据进行转化 传递到request请求中,以便在视图页面显示出来
	 * 
	 * 
	 * @param modeldata
	 *            模型返回需要在视图显示的数据
	 * 
	 * @param request
	 *            请求对象HttpServletRequest
	 * @throws ServletException
	 */
	public final static void transferDataForView(Map<String, Object> modeldata,
			HttpServletRequest request) throws ServletException {
		if (modeldata != null) {
			Map<String, Object> tempMap = new HashMap<String, Object>(modeldata);
			putModelsToRequest(tempMap, request);
		}
	}

	private static void putModelsToRequest(Map<String, Object> model,
			HttpServletRequest request) {
		Set<?> entrys = model.entrySet();
		Iterator<?> it = entrys.iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry e = (Map.Entry) it.next();
			request.setAttribute(e.getKey().toString(), e.getValue());
		}
		if (!model.isEmpty()) {
			model.clear();
			model = null;
		}
	}

	public static Map<String, String> getViewCache() {
		return viewCache;
	}

	/**
	 * 读取配置视图配置数据
	 * 
	 * @param app
	 *            ServletContext
	 */
	public static void loadViewConfigInfo(ServletContext app) {
		synchronized (locker) {
			CommonUtil.fill_DataMap(app, WebConst.WEB_VIEW_FILENAME,
					"mappings.views.standard", "sItem", "name", "url",
					viewCache);
			CommonUtil.fill_DataMap(app, WebConst.WEB_VIEW_FILENAME,
					"mappings.views.freemarker", "fItem", "name", "url",
					viewCache);
			CommonUtil.fill_DataMap(app, WebConst.WEB_VIEW_FILENAME,
					"mappings.module", "mItem", "filename", "active",
					moduleItemMap);
			// 加载其它文件的数据
			if (!moduleItemMap.isEmpty()) {
				Set<?> s = moduleItemMap.entrySet();
				Iterator<?> it = s.iterator();
				while (it.hasNext()) {
					@SuppressWarnings("rawtypes")
					Map.Entry e = (Map.Entry) it.next();
					String fn = (String) e.getKey();
					String active = (String) e.getValue();
					if (active.equalsIgnoreCase("true")) {
						// standard
						CommonUtil.fill_DataMap(app, fn,
								"mappings.views.standard", "sItem", "name",
								"url", viewCache);
						CommonUtil.fill_DataMap(app, fn,
								"mappings.views.freemarker", "fItem", "name",
								"url", viewCache);

					}
				}
				moduleItemMap.clear(); //
			}
			// get no session define
			InputStream in2;
			in2 = app.getResourceAsStream(WebConst.WEB_VIEW_FILENAME);
			String nsViewName = XMLReader.getTagContent(in2,
					"mappings.views.DisabledSessionView");
			if (nsViewName != null && !nsViewName.equals("")) {
				String url = viewCache.get(nsViewName);
				viewCache.put(CommonUtil.DISABLED_SESSION_VIEW, url);
			}
			try {
				if (in2 != null) {
					in2.close();
				}
			} catch (IOException ex) {
				logger.error(ex);
			}
			// get err view define
			InputStream in3;
			in3 = app.getResourceAsStream(WebConst.WEB_VIEW_FILENAME);
			String errViewName = XMLReader.getTagContent(in3,
					"mappings.views.ErrorView");
			if (errViewName != null && !errViewName.equals("")) {
				String url = viewCache.get(errViewName);
				viewCache.put("Beetle_ErrorView_19760224", url);
			}
			try {
				if (in3 != null) {
					in3.close();
				}
			} catch (IOException ex) {
				logger.error(ex);
			}
		}
	}

	public static boolean isDefinedErrView() {
		return viewCache.containsKey("Beetle_ErrorView_19760224");
	}

	/**
	 * 通过视图名称获取此名称对于的具体视图文件的url 为了支持视图0配置，若视图名称本身就是url，则返回其本身
	 * 
	 * 
	 * @param app
	 * @param name
	 * @return
	 */
	public final static String getViewUrlByName(ServletContext app, String name) {
		if (viewCache.isEmpty()) {
			loadViewConfigInfo(app);
		}
		String url = viewCache.get(name);
		if (url == null) {
			// eg:$|xpath|ypath|zcontroller.ctrl
			if (name.indexOf('$') >= 0) {
				String b = CommonUtil.formatPath(name);
				b = b.replace('|', '/');
				try {
					String c = b + ".jsp";
					URL urlObj = app.getResource(c);
					if (urlObj != null) {
						url = c;
						viewCache.put(name, url);
					} else {
						c = b + ".html";
						urlObj = app.getResource(c);
						if (urlObj != null) {
							url = c;
							viewCache.put(name, url);
						} else {
							throw new AppRuntimeException(
									"sorry,not support this url format[" + c
											+ "]");
						}
					}
				} catch (MalformedURLException e) {
					throw new AppRuntimeException(e);
				}
			} else {
				int i = name.indexOf('.');
				if (i >= 0) {// 此视图名称为具体的url
					url = name;
					viewCache.put(url, url);// 缓存起来，key-value一样
				} else {
					throw new com.beetle.framework.AppRuntimeException("找不到视图[" + name
							+ "]所对应的服务器文件，请检查你的视图文件是否已配置");
				}
			}
		}
		return url;
	}

	/**
	 * 获取当前系统所有视图的映射 （每次都是一个新的map）
	 * 
	 * 
	 * @return
	 */
	public final static Map<String, String> getAllViews() {
		Map<String, String> m = new HashMap<String, String>();
		m.putAll(viewCache);
		return m;
	}
}
