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

import com.beetle.framework.log.AppLogger;
import com.beetle.framework.util.file.XMLReader;
import com.beetle.framework.web.common.WebConst;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 * Title: BeetleWeb
 * </p>
 * 
 * <p>
 * Description: MVC Web Framework
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: 甲壳虫软件
 * </p>
 * 
 * @author 余浩东(hdyu@beetlesoft.net)
 * @version 1.0
 */
public class OnOffFactory {
	private static final AppLogger logger = AppLogger
			.getInstance(OnOffFactory.class);

	private static String geOnOffStr(ServletContext application, String onOff) {
		String onffClass;
		InputStream in2;
		in2 = application.getResourceAsStream(WebConst.WEB_CONTROLLER_FILENAME);
		onffClass = XMLReader.getTagContent(in2, "mappings.onoff." + onOff);
		if (in2 != null) {
			try {
				in2.close();
			} catch (IOException ex) {
				in2 = null;
			}
		}
		return onffClass;
	}

	public static IStartUp getStartUp(ServletContext application) {
		String s = geOnOffStr(application, "startUp");
		if (s == null || s.trim().equals("")) {
			return null;
		}
		try {
			Object o = Class.forName(s).newInstance();
			return (IStartUp) o;
		} catch (Exception ex) {
			logger.error(ex);
			return null;
		}
	}

	public static ICloseUp getCloseUp(ServletContext application) {
		String s = geOnOffStr(application, "closeUp");
		if (s == null || s.trim().equals("")) {
			return null;
		}
		try {
			Object o = Class.forName(s).newInstance();
			return (ICloseUp) o;
		} catch (Exception ex) {
			logger.error(ex);
			return null;
		}
	}
}
