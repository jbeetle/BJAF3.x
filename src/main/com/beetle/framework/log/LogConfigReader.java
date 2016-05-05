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
package com.beetle.framework.log;

import com.beetle.framework.AppProperties;
import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.util.ResourceLoader;
import com.beetle.framework.util.file.PropertiesReader;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * <p>
 * Title: BeetleSoft Framework
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * 
 * @version 1.0
 */
class LogConfigReader extends PropertiesReader {
	final static String getResStr(String key) {
		return LogConfigReader.getValue(
				LogConfigReader.getBundle("com.beetle.framework.log.log4j"),
				key);
	}

	private final static Set<String> loadpathSet = new HashSet<String>();
	private static volatile boolean hasInited = false;
	private static String lastpath = "";
	private static int loadFlag = 0;

	public static boolean isInited() {
		return hasInited;
	}

	public static void reload(Properties pro) {
		PropertyConfigurator.configure(pro);
	}

	public static void reload() {
		if (loadFlag == 1) {
			PropertyConfigurator.configure(lastpath);
		} else if (loadFlag == 2) {
			URL url;
			try {
				url = ResourceLoader.getResURL(lastpath);
				PropertyConfigurator.configure(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (String lp : loadpathSet) {
			try {
				PropertyConfigurator.configure(lp);
			} catch (Exception e) {
				URL url;
				try {
					url = ResourceLoader.getResURL(lp);
					PropertyConfigurator.configure(url);
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		}
	}

	public static void init(Properties pro) {
		PropertyConfigurator.configure(pro);
	}

	public static void init(URL url) {
		if (!loadpathSet.contains(url.toString())) {
			synchronized (loadpathSet) {
				if (!loadpathSet.contains(url.toString())) {
					PropertyConfigurator.configure(url);
				}
			}
		}
	}

	public static void init(File configFile) {
		if (!configFile.exists()) {
			System.out.println(configFile + " can't be found! err");
			return;
		}
		if (!loadpathSet.contains(configFile.getPath())) {
			synchronized (loadpathSet) {
				if (!loadpathSet.contains(configFile.getPath())) {
					PropertyConfigurator.configure(configFile.getPath());
				}
			}
		}
	}

	public synchronized static void init() {
		if (!hasInited) {
			lastpath = AppProperties.getAppHome() + "log4j.properties";
			Enumeration<?> e = LogManager.getCurrentLoggers();
			if (!e.hasMoreElements()) {
				loadconf();
			} else {
				int i = AppProperties.getAsInt("resource_LOG4J_REUSE", 0);
				if (i == 0) {
					loadconf();
				} else {
					System.out
							.println("other log4j's config has loaded,uses it.");
				}
			}
		}
		hasInited = true;
	}

	private static void loadconf() {
		File f = new File(lastpath);
		if (f.exists()) {
			PropertyConfigurator.configure(lastpath);
			loadFlag = 1;
			System.out.println("loaded {" + lastpath + "} from file");
		} else { //
			try {
				URL url = ResourceLoader.getResURL(lastpath);
				loadFlag = 2;
				PropertyConfigurator.configure(url);
				System.out.println("loaded 'config/log4j.properties' from jar");
			} catch (IOException ex) {

				try {
					URL url = new URL(lastpath);
					PropertyConfigurator.configure(url);
					System.out.println("loaded {" + lastpath + "} ");
				} catch (Exception e1) {
					otherway();
				}
			}
		}
		f = null;
	}

	private static void otherway() {
		lastpath = AppProperties.getAppHome() + "log4j.xml";
		File f = new File(lastpath);
		if (f.exists()) {
			PropertyConfigurator.configure(lastpath);
			loadFlag = 1;
			System.out.println("loaded {" + lastpath + "} from file");
		} else {
			lastpath = "config/log4j.properties";
			f = new File(lastpath);
			if (f.exists()) {
				PropertyConfigurator.configure(lastpath);
				loadFlag = 1;
				System.out.println("loaded {" + lastpath + "} from file");
			} else {
				lastpath = "log4j.properties";
				f = new File(lastpath);
				if (f.exists()) {
					PropertyConfigurator.configure(lastpath);
					System.out
							.println("loaded log4j.properties in current path or classpath");
				} else {
					throw new AppRuntimeException(
							"can not load log4j config file! err!");
				}
			}
		}
		f = null;
	}

}
