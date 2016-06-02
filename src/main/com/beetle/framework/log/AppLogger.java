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
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beetle.framework.AppRuntimeException;

import ch.qos.logback.core.joran.spi.JoranException;

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

	public AppLogger(URL url, String name) {
		try {
			LogConfigReader.init(url);
		} catch (JoranException e) {
			throw new AppRuntimeException(e);
		}
		logger = LoggerFactory.getLogger(name);
	}

	public AppLogger(File configFile, String name) {
		try {
			LogConfigReader.init(configFile);
		} catch (JoranException e) {
			throw new AppRuntimeException(e);
		}
		logger = LoggerFactory.getLogger(name);
	}

	private AppLogger(Class<?> logClass) {
		if (!LogConfigReader.isInited()) {
			try {
				LogConfigReader.init();
			} catch (JoranException e) {
				throw new AppRuntimeException(e);
			}
		}
		logger = LoggerFactory.getLogger(logClass);
	}

	private AppLogger(String className) {
		if (!LogConfigReader.isInited()) {
			try {
				LogConfigReader.init();
			} catch (JoranException e) {
				throw new AppRuntimeException(e);
			}
		}
		logger = LoggerFactory.getLogger(className);
	}

	/**
	 * 在运行时修改了配置文件，可以通过此方法重载配置，以便修改见效
	 */
	public synchronized static void reloadLogConfig() {
		try {
			LogConfigReader.reload();
		} catch (JoranException e) {
			throw new AppRuntimeException(e);
		}
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

	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
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
		logger.info("{}", message);
	}

	/*
	 * 异常归结到error级别，不应该出现Info级别 public void info(String paramString, Throwable
	 * t) { if (logger.isInfoEnabled()) { logger.log(FQCN, Level.INFO,
	 * paramString, t); } }
	 */
	public void info(String paramString, Object... paramObjects) {
		logger.info(paramString, paramObjects);
	}

	public void warn(Object paramString) {
		logger.warn("{}", paramString);
	}

	public void warn(String paramString, Object... paramObjects) {
		logger.warn(paramString, paramObjects);
	}

	public void trace(Object paramObject) {
		logger.trace("{}", paramObject);
	}

	public void trace(String paramString, Object... paramObjects) {
		logger.trace(paramString, paramObjects);
	}

	public void debug(Object paramObject) {
		logger.debug("{}", paramObject);
	}

	public void debug(String paramString, Object... paramObjects) {
		logger.debug(paramString, paramObjects);
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
			logger.error("{}", message);
		}
	}

	public void error(String paramString, Object... paramObjects) {
		logger.error(paramString, paramObjects);
	}

	public void error(String paramString, Throwable t) {
		if (t != null) {
			t.printStackTrace();// 在控制台答应，方便调试
		}
		logger.error(paramString, t);
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
