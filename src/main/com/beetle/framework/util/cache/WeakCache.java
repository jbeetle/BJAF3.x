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
package com.beetle.framework.util.cache;

import java.util.*;

public class WeakCache implements ICache {

	private Map<Object, Object> m_map = null;

	public WeakCache() { 
		m_map = new WeakHashMap<Object, Object>();
	}

	public WeakCache(int size) {
		m_map = new WeakHashMap<Object, Object>(size);
	}

	/**
	 * Removes all mappings from this cache.
	 */
	public void clear() {
		m_map.clear();
	}

	public boolean containsKey(Object key) {
		return m_map.containsKey(key);
	}

	public Object get(Object key) {
		return m_map.get(key);
	}

	public boolean isEmpty() {
		return m_map.isEmpty();
	}

	public Set<Object> keySet() {
		return m_map.keySet();
	}

	public void put(Object key, Object value) {
		m_map.put(key, value);
	}

	public void remove(Object key) {
		Object o = m_map.remove(key);
		if (o != null) {
			if (o instanceof List) {
				((List<?>) o).clear();
			} else if (o instanceof Map) {
				((Map<?, ?>) o).clear();
			}
			o = null;
		}
	}

	public int size() {
		return m_map.size();
	}

	public Set<?> entrySet() {
		return m_map.entrySet();
	}

	public Collection<Object> values() {
		return m_map.values();
	}

}
