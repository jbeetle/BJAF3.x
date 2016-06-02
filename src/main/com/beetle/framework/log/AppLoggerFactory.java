package com.beetle.framework.log;

import org.slf4j.Logger;

import com.beetle.framework.AppRuntimeException;

import ch.qos.logback.core.joran.spi.JoranException;
/**
 * 使用AppLoggerFactory代替org.slf4j.LoggerFactory才能完成
 * config/logback.xml初始化工作
 * @author henryyu
 *
 */
public final class AppLoggerFactory {
	static {
		try {
			LogConfigReader.init();
		} catch (JoranException e) {
			throw new AppRuntimeException("Log initialize abnormal", e);
		}
	}

	public static Logger getLogger(Class<?> clazz) {
		return org.slf4j.LoggerFactory.getLogger(clazz);
	}
}
