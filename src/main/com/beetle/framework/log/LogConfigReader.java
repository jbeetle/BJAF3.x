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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.LoggerFactory;

import com.beetle.framework.AppProperties;
import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.util.ResourceLoader;
import com.beetle.framework.util.file.PropertiesReader;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

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
	final static JoranConfigurator PropertyConfigurator = new JoranConfigurator();

	final static String getResStr(String key) {
		return LogConfigReader.getValue(LogConfigReader.getBundle("com.beetle.framework.log.log4j"), key);
	}

	private final static Set<String> loadpathSet = new HashSet<String>();
	private static volatile boolean hasInited = false;
	private static String lastpath = "";
	private static int loadFlag = 0;

	public static boolean isInited() {
		return hasInited;
	}

	public static void reload() throws JoranException {
		if (loadFlag == 1) {
			PropertyConfigurator.doConfigure(lastpath);
		} else if (loadFlag == 2) {
			URL url;
			try {
				url = ResourceLoader.getResURL(lastpath);
				PropertyConfigurator.doConfigure(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (String lp : loadpathSet) {
			try {
				PropertyConfigurator.doConfigure(lp);
			} catch (Exception e) {
				URL url;
				try {
					url = ResourceLoader.getResURL(lp);
					PropertyConfigurator.doConfigure(url);
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		}
	}

	public static void init(URL url) throws JoranException {
		if (!loadpathSet.contains(url.toString())) {
			synchronized (loadpathSet) {
				if (!loadpathSet.contains(url.toString())) {
					PropertyConfigurator.doConfigure(url);
				}
			}
		}
	}

	public static void init(File configFile) throws JoranException {
		if (!configFile.exists()) {
			System.out.println(configFile + " can't be found! err");
			return;
		}
		if (!loadpathSet.contains(configFile.getPath())) {
			synchronized (loadpathSet) {
				if (!loadpathSet.contains(configFile.getPath())) {
					PropertyConfigurator.doConfigure(configFile.getPath());
				}
			}
		}
	}

	public synchronized static void init() throws JoranException {
		if (!hasInited) {
			lastpath = AppProperties.getAppHome() + "logback.xml";
			LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
			PropertyConfigurator.setContext(lc);
			lc.reset();
			loadconf();
			StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
		}
		hasInited = true;
	}

	private static void loadconf() throws JoranException {
		File f = new File(lastpath);
		if (f.exists()) {
			PropertyConfigurator.doConfigure(lastpath);
			loadFlag = 1;
			System.out.println("loaded {" + lastpath + "} from file");
		} else { //
			try {
				URL url = ResourceLoader.getResURL(lastpath);
				loadFlag = 2;
				PropertyConfigurator.doConfigure(url);
				System.out.println("loaded 'config/logback.xml' from jar");
			} catch (IOException ex) {
				try {
					URL url = new URL(lastpath);
					PropertyConfigurator.doConfigure(url);
					System.out.println("loaded {" + lastpath + "} ");
				} catch (Exception e1) {
					otherway();
				}
			}
		}
		f = null;
	}

	private static void otherway() throws JoranException {
		lastpath = AppProperties.getAppHome() + "logback.groovy";
		File f = new File(lastpath);
		if (f.exists()) {
			PropertyConfigurator.doConfigure(lastpath);
			loadFlag = 1;
			System.out.println("loaded {" + lastpath + "} from file");
		} else {
			lastpath = "config/logback.xml";
			f = new File(lastpath);
			if (f.exists()) {
				PropertyConfigurator.doConfigure(lastpath);
				loadFlag = 1;
				System.out.println("loaded {" + lastpath + "} from file");
			} else {
				lastpath = "logback.xml";
				f = new File(lastpath);
				if (f.exists()) {
					PropertyConfigurator.doConfigure(lastpath);
					System.out.println("loaded logback.xml in current path or classpath");
				} else {
					throw new AppRuntimeException("can not load log config file! err!");
				}
			}
		}
		f = null;
	}

}
