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
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: 甲壳虫科技
 * </p>
 * 
 * @author 余浩东
 * @version 1.0 使用:static AppLogger logger = AppLogger.getInstance(类名.class)
 */

public final class AppLogger {
	private Logger logger;
	private final static Map<String, AppLogger> loggerCache = new ConcurrentHashMap<String, AppLogger>();
	private final static String FQCN = AppLogger.class.getName();

	public AppLogger(URL url, String name) {
		LogConfigReader.init(url);
		logger = Logger.getLogger(name);
	}

	public AppLogger(File configFile, String name) {
		LogConfigReader.init(configFile);
		logger = Logger.getLogger(name);
	}

	public AppLogger(Properties pro, String name) {
		LogConfigReader.init(pro);
		logger = Logger.getLogger(name);
	}

	private AppLogger(Class<?> logClass) {
		if (!LogConfigReader.isInited()) {
			LogConfigReader.init();
		}
		logger = Logger.getLogger(logClass);
	}

	private AppLogger(String className) {
		if (!LogConfigReader.isInited()) {
			LogConfigReader.init();
		}
		logger = Logger.getLogger(className);
	}

	/**
	 * 在运行时修改了配置文件，可以通过此方法重载配置，以便修改见效
	 */
	public synchronized static void reloadLogConfig() {
		LogConfigReader.reload();
	}

	public synchronized static void reloadLogConfig(Properties pro) {
		LogConfigReader.reload(pro);
	}

	/**
	 * 获取系统日志记录实例
	 * 
	 * @param logClassName
	 *            需要注册一个日志的类
	 * @return 系统日志实例
	 */
	public static AppLogger getInstance(Class<?> logClass) {
		String key = logClass.getName();
		if (loggerCache.containsKey(key)) {
			return loggerCache.get(key);
		}
		synchronized (loggerCache) {
			AppLogger al = loggerCache.get(key);
			if (al == null) {
				al = new AppLogger(logClass);
				loggerCache.put(key, al);
			}
			return al;
		}
	}

	/**
	 * 获取系统日志记录实例
	 * 
	 * @param className
	 *            需要注册一个日志名称
	 * @return 系统日志实例
	 */
	public static AppLogger getInstance(String name) {
		if (loggerCache.containsKey(name)) {
			return loggerCache.get(name);
		}
		synchronized (loggerCache) {
			AppLogger al = loggerCache.get(name);
			if (al == null) {
				al = new AppLogger(name);
				loggerCache.put(name, al);
			}
			return al;
		}
	}

	/**
	 * isDebugEnabled
	 * 
	 * @return boolean
	 */
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	/**
	 * 设置当前日志的输入级别
	 * 
	 * @param level
	 *            1--debug; 2--info; 3--warn; 4--error; 5--fatal
	 */
	public void setLevel(int level) {
		if (level == 1) {
			logger.setLevel(Level.DEBUG);
		} else if (level == 2) {
			logger.setLevel(Level.INFO);
		} else if (level == 3) {
			logger.setLevel(Level.WARN);
		} else if (level == 4) {
			logger.setLevel(Level.ERROR);
		} else if (level == 5) {
			logger.setLevel(Level.FATAL);
		}
	}

	/**
	 * isInfoEnabled
	 * 
	 * @return boolean
	 */
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	/**
	 * info
	 * 
	 * @param message
	 *            Object
	 */
	public void info(Object message) {
		if (logger.isInfoEnabled())
			logger.log(FQCN, Level.INFO, message, null);
	}

	/*
	 * 异常归结到error级别，不应该出现Info级别 public void info(String paramString, Throwable
	 * t) { if (logger.isInfoEnabled()) { logger.log(FQCN, Level.INFO,
	 * paramString, t); } }
	 */
	public void info(String paramString, Object... paramObjects) {
		if (logger.isInfoEnabled()) {
			String msgStr = LogFormatter.arrayFormat(paramString, paramObjects);
			logger.log(FQCN, Level.INFO, msgStr, null);
		}
	}

	public void warn(Object paramString) {
		logger.log(FQCN, Level.WARN, paramString, null);
	}

	public void warn(String paramString, Object... paramObjects) {
		if (logger.isEnabledFor(Level.WARN)) {
			String msgStr = LogFormatter.arrayFormat(paramString, paramObjects);
			logger.log(FQCN, Level.WARN, msgStr, null);
		}
	}

	/**
	 * 异常属于错误级别或更高级别，不出现在警告级别中 public void warn(String paramString, Throwable t)
	 * { logger.log(FQCN, Level.WARN, paramString, t); }
	 **/
	public void fatal(Object paramString) {
		logger.log(FQCN, Level.FATAL, paramString, null);
	}

	public void fatal(String paramString, Throwable t) {
		logger.log(FQCN, Level.FATAL, paramString, t);
	}

	public void debug(Object paramObject) {
		if (logger.isDebugEnabled()) {
			logger.log(FQCN, Level.DEBUG, paramObject, null);
		}
	}

	public void debug(String paramString, Object... paramObjects) {
		if (logger.isDebugEnabled()) {
			String msgStr = LogFormatter.arrayFormat(paramString, paramObjects);
			logger.log(FQCN, Level.DEBUG, msgStr, null);
		}
	}

	/**
	 * error
	 * 
	 * @param message
	 *            Object
	 * @todo Implement this org.apache.log4j.Category method
	 */
	public void error(Object message) {
		if (message == null) {
			return;
		}
		if (message instanceof Throwable) {
			Throwable t = (Throwable) message;
			error(t.getMessage(), t);
		} else {
			logger.log(FQCN, Level.ERROR, message, null);
		}
	}

	public void error(String paramString, Object... paramObjects) {
		if (logger.isEnabledFor(Level.ERROR)) {
			String msgStr = LogFormatter.arrayFormat(paramString, paramObjects);
			logger.log(FQCN, Level.ERROR, msgStr, null);
		}
	}

	public void error(String paramString, Throwable t) {
		if (t != null) {
			t.printStackTrace();// 在控制台答应，方便调试
		}
		logger.log(FQCN, Level.ERROR, paramString, t);
	}

	public String strFormat(String paramString, Object... paramObjects) {
		return LogFormatter.arrayFormat(paramString, paramObjects);
	}

	public String getStackTraceInfo(Throwable t) {
		return getErrStackTraceInfo(t);
	}

	/**
	 * 把错误堆栈信息封装成字符串
	 * 
	 * @param t
	 * @return
	 */
	public static String getErrStackTraceInfo(Throwable t) {
		java.io.CharArrayWriter cw = new java.io.CharArrayWriter();
		java.io.PrintWriter pw = new java.io.PrintWriter(cw, true);
		t.printStackTrace(pw);
		String info = cw.toString();
		cw.close();
		pw.close();
		return info;
	}
}
