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

/**
 * 超时缓存器。<br>
 * 指定一个超时时间,调用get获取缓存值时，若超出定义时间，则会清空此值，并返回null
 * 
 */
public class TimeOutCache implements ICache {
	private int timeout;
	private Map<Object, V> map;

	private static class V {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (markTime ^ (markTime >>> 32));
			result = prime * result + ((obj == null) ? 0 : obj.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			V other = (V) obj;
			if (markTime != other.markTime)
				return false;
			if (this.obj == null) {
				if (other.obj != null)
					return false;
			} else if (!this.obj.equals(other.obj))
				return false;
			return true;
		}

		private long markTime;
		private Object obj;

		public V(long markTime, Object obj) {
			this.markTime = markTime;
			this.obj = obj;
		}

		public Object getObj() {
			return this.obj;
		}
	}

	/**
	 * @param timeout
	 *            --超时时间，单位ms
	 */
	public TimeOutCache(int timeout) {
		this.timeout = timeout;
		this.map = new WeakHashMap<Object, V>();
	}

	public void clear() {
		map.clear();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public Object get(Object key) {
		Object o = map.get(key);
		if (o == null) {
			return null;
		} else {
			V v = (V) o;
			long now = System.currentTimeMillis();
			long markTime = v.markTime;
			long k = now - markTime;
			if (k > timeout) {
				v = null;
				map.remove(key);
				return null;
			}
			return v.getObj();
		}
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<Object> keySet() {
		return map.keySet();
	}

	public void put(Object key, Object value) {
		V v = new V(System.currentTimeMillis(), value);
		map.put(key, v);
	}

	public void remove(Object key) {
		Object o = map.remove(key);
		if (o != null) {
			V v = (V) o;
			if (v.getObj() != null) {
				if (v.getObj() instanceof List) {
					((List<?>) v.getObj()).clear();
				} else if (v.getObj() instanceof Map) {
					((Map<?, ?>) v.getObj()).clear();
				}
			}
			v = null;
		}
	}

	public int size() {
		return map.size();
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public Set<?> entrySet() {
		return map.entrySet();
	}

	public Collection<V> values() {
		return map.values();
	}

}
