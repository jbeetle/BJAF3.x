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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StrongCache implements ICache {

	private Map<Object, Object> m_map = null;

	/** Creates a new instance of StrongCache */
	public StrongCache() {
		m_map = new HashMap<Object, Object>();
	}

	/** Creates a new instance of StrongCache */
	public StrongCache(int size) {
		m_map = new HashMap<Object, Object>(size);
	}

	/**
	 * Removes all mappings from this cache.
	 */
	public void clear() {
		m_map.clear();
	}

	/**
	 * Returns true if this cache contains a mapping for the specified key.
	 * 
	 * @param key
	 *            key whose presence in this cache is to be tested.
	 * @return true if this cache contains a mapping for the specified key.
	 */
	public boolean containsKey(Object key) {
		return m_map.containsKey(key);
	}

	/**
	 * Returns the value to which cache maps the specified key. Returns null if
	 * the cache contains no mapping for this key. A return value of null does
	 * not necessarily indicate that the cache contains no mapping for the key;
	 * it's also possible that the cache explicitly maps the key to null. The
	 * containsKey operation may be used to distinguish these two cases.
	 * 
	 * @param key
	 *            key whose associated value is to be returned.
	 * @return the value to which this cache maps the specified key, or null if
	 *         the cache contains no mapping for this key.
	 */
	public Object get(Object key) {
		return m_map.get(key);
	}

	/**
	 * Returns true if this cache contains no key-value mappings.
	 * 
	 * @return true if this cache contains no key-value mappings.
	 */
	public boolean isEmpty() {
		return m_map.isEmpty();
	}

	/**
	 * Returns a set view of the keys contained in this cache. The set is backed
	 * by the cache, so changes to the cache are reflected in the set, and
	 * vice-versa. If the cache is modified while an iteration over the set is
	 * in progress, the results of the iteration are undefined.
	 * 
	 * @return a set view of the keys contained in this cache.
	 */
	public Set<Object> keySet() {
		return m_map.keySet();
	}

	/**
	 * Associates the specified value with the specified key in this cache. If
	 * the cache previously contained a mapping for this key, the old value is
	 * replaced by the specified value.
	 * 
	 * @param key
	 *            key with which the specified value is to be associated.
	 * @param value
	 *            value to be associated with the specified key.
	 */
	public void put(Object key, Object value) {
		synchronized (this) {
			m_map.put(key, value);
		}
	}

	/**
	 * Removes the mapping for this key from this cache if it is present.
	 * 
	 * @param key
	 *            key whose mapping is to be removed from the cache.
	 */
	public void remove(Object key) {
		synchronized (this) {
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
	}

	/**
	 * Returns the number of key-value mappings in this cache.
	 * 
	 * @return the number of key-value mappings in this cache.
	 */
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
