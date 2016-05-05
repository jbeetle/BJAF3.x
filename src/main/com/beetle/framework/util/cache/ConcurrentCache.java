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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentCache implements ICache {
	private ConcurrentHashMap<Object, Object> map = null;

	public ConcurrentCache() {
		map = new ConcurrentHashMap<Object, Object>();
	}

	public ConcurrentCache(int initialCapacity) {
		map = new ConcurrentHashMap<Object, Object>(initialCapacity);
	}

	public void clear() {
		map.clear();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public Object get(Object key) {
		return map.get(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<Object> keySet() {
		return map.keySet();
	}

	/*
	 * value--不能为空
	 */
	public void put(Object key, Object value) {
		map.put(key, value);
	}

	public void remove(Object key) {
		if (key == null) {
			return;
		}
		Object o = map.remove(key);
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
		return map.size();
	}

	public Set<?> entrySet() {
		return map.entrySet();
	}

	public Collection<Object> values() {
		return map.values();
	}

}
