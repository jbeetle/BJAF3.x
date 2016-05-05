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
package com.beetle.framework;

import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.beetle.framework.resource.dic.DIContainer;

public class AppContext {
	private final static ConcurrentHashMap<Object, Object> table = new ConcurrentHashMap<Object, Object>();
	private static AppContext instance = new AppContext();
	static final String appHomePath = "beetle.application.home.path";

	public void setAppHomePath(String homePath) {
		bind(appHomePath, homePath);
	}

	public String getAppHomePathDefineFromContext() {
		return (String) lookup(appHomePath);
	}

	String getAppHome() {
		String fp = System.getProperty(appHomePath);
		if (fp != null && fp.trim().length() > 0) {
			if (!fp.endsWith("/")) {
				fp = fp + "/";
			}
			return fp;
		} else {
			String ap = (String) AppContext.getInstance().lookup(appHomePath);
			if (ap != null && ap.trim().length() > 0) {
				if (!ap.endsWith("/")) {
					ap = ap + "/";
				}
				return ap;
			}
		}
		return "config/";
	}

	public Enumeration<Object> getContextKeys() {
		return table.keys();
	}

	private AppContext() {
		// table = new Hashtable();
	}

	public void bind(Object name, Object obj) {
		table.put(name, obj);
	}

	public void close() {
		if (!table.isEmpty()) {
			table.clear();
		}
	}

	final public <T> T retrieveInDic(Class<T> face) {
		return DIContainer.getInstance().retrieve(face);
	}

	final public Object retrieveInDic(String key) {
		return DIContainer.getInstance().retrieve(key);
	}

	public Map<Object, Object> getEnvironment() {
		return table;
	}

	public boolean exist(Object name) {
		return table.containsKey(name);
	}

	public Object lookup(Object name) {
		return table.get(name);
	}

	public void rebind(Object name, Object obj) {
		if (table.containsKey(name)) {
			table.remove(name);
		}
		table.put(name, obj);
	}

	public void unbind(Object name) {
		table.remove(name);
	}

	public static AppContext getInstance() {
		return instance;
	}
}
