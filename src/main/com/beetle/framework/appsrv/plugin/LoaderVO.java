package com.beetle.framework.appsrv.plugin;

import java.net.URLClassLoader;

public class LoaderVO {
	private String id;

	private URLClassLoader loader;

	private long lasttime;

	private Object handler;// 具体接口的处理者

	public Object getHandler() {
		return handler;
	}

	public void setHandler(Object handler) {
		this.handler = handler;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public URLClassLoader getLoader() {
		return loader;
	}

	public void setLoader(URLClassLoader loader) {
		this.loader = loader;
	}

	public long getLasttime() {
		return lasttime;
	}

	public void setLasttime(long lasttime) {
		this.lasttime = lasttime;
	}

	@Override
	public String toString() {
		return "LoaderVO [id=" + id + ", loader=" + loader + ", lasttime="
				+ lasttime + ", handler=" + handler + "]";
	}

}
