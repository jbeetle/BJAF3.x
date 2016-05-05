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
package com.beetle.framework.web.common;

import com.beetle.framework.util.file.XMLReader;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * <p>
 * Title: BeetleWeb
 * </p>
 * <p/>
 * <p>
 * Description: MVC Web Framework
 * </p>
 * <p/>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * <p/>
 * <p>
 * Company: 甲壳虫软件
 * <p/>
 * </p>
 * 
 * @author 余浩东(hdyu@beetlesoft.net)
 * @version 1.0
 */

public class CommonUtil extends WebUtil {
	public static final String WEB_ENCODE_CHARSET = "WEB_ENCODE_CHARSET";
	public static final String WEB_SERVER_INFO = "WEB_SERVER_INFO";
	public static final String WEB_EXCEPTION_INFO = "WEB_EXCEPTION_INFO";
	public static final String WEB_CTRL_PREFIX = "WEB_CTRL_PREFIX";
	public static final String CTRL_VIEW_MAP_ENABLED = "CTRL_VIEW_MAP_ENABLED";
	public static final String DISABLED_SESSION_VIEW = "DISABLED_SESSION_VIEW";
	public static final String CANCEL_SESSION_CHECK_FLAG = "CANCEL_SESSION_CHECK_FLAG";
	public static final String WEB_SERVICE_DATA_DEFAULT_FORMAT = "WEB_SERVICE_DATA_DEFAULT_FORMAT";
	// public static final String SESSION_CHECK = "SESSION_CHECK";
	// public static final String GLOBAL_BACK_CALL = "GLOBAL_BACK_CALL";
	// public static final String GLOBAL_FRONT_CALL = "GLOBAL_FRONT_CALL";
	public static final String controllname = "yuhaodong@gmail.com";
	public static final String controllerimpclassname = "hdyu@beetlesoft.net";
	public static final String TRUE_STR = "true";
	public static final String DOT_STR = ".";
	public static final char DOT = '.';
	public static final char RIGHT_SLASHDOT = '/';
	public static final char DOLLAR = '$';
	public static final String app_Context = "app_servlet_context";
	public static final String GET_STR = "get";
	public static final String TOMCAT_STR = "Tomcat";
	public static final String ACTION_STR = "$action";

	/**
	 * 填充配置文件数据
	 * 
	 * @param app
	 *            ServletContext
	 * @param filename
	 *            String
	 * @param itemPath
	 *            String
	 * @param ElementName
	 *            String
	 * @param keyName
	 *            String
	 * @param valueName
	 *            String
	 * @param map
	 *            Map
	 */
	public static final void fill_DataMap(ServletContext app, String filename,
			String itemPath, String ElementName, String keyName,
			String valueName, Map<String, String> map) {
		InputStream in;
		if (filename.indexOf("/config/") >= 0) {
			in = app.getResourceAsStream(filename);
		} else {
			in = app.getResourceAsStream("/config/" + filename);
		}
		Map<String, String> m = XMLReader.getProperties(in, itemPath,
				ElementName, keyName, valueName);
		if (!m.isEmpty()) {
			map.putAll(m);
			m.clear();
		}
		try {
			if (in != null) {
				in.close();
				in = null;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * getCookie
	 * 
	 * @param cookieName
	 *            String
	 * @param request
	 *            HttpServletRequest
	 * @return Cookie
	 */
	public static final Cookie getCookie(String cookieName,
			HttpServletRequest request) {
		Cookie cks[] = request.getCookies();
		if (cks == null) {
			return null;
		} else {
			for (int i = 0; i < cks.length; i++) {
				Cookie ck = cks[i];
				String name = ck.getName();
				if (name.equals(cookieName)) {
					return ck;
				}
			}
			return null;
		}
	}

	public final static boolean isHaveExt(String url) {
		int i = url.indexOf(DOT);
		if (i >= 0) {
			return true;
		}
		return false;
	}

	/**
	 * 获取url的后缀名
	 * 
	 * @param url
	 * @return
	 */
	public final static String getExt(String url) {
		int i = url.indexOf(DOT);
		if (i >= 0) {
			String ext = url.substring(i + 1);
			return ext;
		}
		return url;
	}

	/**
	 * 获取url的资源目录
	 * 
	 * @param url
	 *            String
	 * @return String
	 */
	public final static String getUrlDir(String url) {
		int i = url.lastIndexOf(RIGHT_SLASHDOT);
		if (i == -1) {
			return url;
		} else {
			return url.substring(0, i);
		}
	}

	/**
	 * 格式化零配置路径 删除首个'/','$'和后缀
	 * 
	 * @param
	 * @return
	 */
	public final static String formatPath(String a) {
		int k = a.indexOf(DOLLAR);
		if (k >= 0) {
			a = a.substring(k + 1);
		}
		if (a.indexOf(RIGHT_SLASHDOT) == 0) {
			a = a.substring(1);
		}
		int i = a.lastIndexOf(DOT);
		if (i > 0) {
			a = a.substring(0, i);
		}
		return a;
	}
	public final static String delLastBevel(String a) {
		if (a.endsWith("/")) {
			a = a.substring(0, a.length() - 1);
		}
		return a;
	}
	public final static String delLastDot(String a) {
		if (a.endsWith(".")) {
			a = a.substring(0, a.length() - 1);
		}
		return a;
	}

	/**
	 * 在路径上加上“/”（如果没有的话）
	 * 
	 * @param path
	 * @return
	 */
	public final static String addLastBeveltoPatch(String path) {
		int i = path.lastIndexOf(RIGHT_SLASHDOT);
		if (i == -1) {
			return path + RIGHT_SLASHDOT;
		} else {
			return path;
		}
	}

	/**
	 * 分析请求路径 只返回请求控制器名称（不带路径）
	 * 
	 * @param path
	 *            String
	 * @return String
	 */
	public final static String analysePath(String path) {
		int i = path.lastIndexOf(RIGHT_SLASHDOT);
		if (i == -1) {
			return path;
		} else {
			return path.substring(i + 1);
		}
	}

}
