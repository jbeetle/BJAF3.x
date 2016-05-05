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

public class LifeCycleCache implements ICache {
	/**
	 * 注册超时处理事件
	 * 
	 * @param timeOutEvent
	 */
	public void registerTimeOutEvent(TimeOutEvent timeOutEvent) {
		this.timeOutEvent = timeOutEvent;
	}

	/**
	 * 超时触发事件 在超时销毁此对象之前，触发此事件
	 */
	public static abstract class TimeOutEvent {
		public abstract void deal(Object key, Object value);
	}

	private long globalLifeTime;
	private ICache cache;
	private boolean stopflag = false;
	private TimeOutEvent timeOutEvent;

	private class CycleMinitor implements Runnable {

		public void run() {
			while (!stopflag) {
				try {
					check();
					Thread.sleep(250);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}

		@SuppressWarnings("rawtypes")
		private void check() {
			Set<?> s = cache.entrySet();
			Iterator<?> it = s.iterator();
			while (it.hasNext()) {
				Map.Entry kv = (Map.Entry) it.next();
				V v = (V) kv.getValue();
				if (v == null) {
					continue;
				}
				long now = System.currentTimeMillis();
				long markTime = v.getMarkTime();
				long k = now - markTime;
				if (k > v.getTimeout()) {
					// System.out.println("remove-"+kv.getKey());
					if (timeOutEvent != null) {
						V vv = (V) kv.getValue();
						timeOutEvent.deal(kv.getKey(), vv.getObj());
					}
					remove(kv.getKey());
				}
			}
		}

	}

	/**
	 * @param globalLifeTime
	 *            --存储器中所有对象默认存活时间，单位为毫秒ms
	 * @param tot
	 *            --超时事件
	 */
	public LifeCycleCache(long globalLifeTime, TimeOutEvent tot) {
		this.globalLifeTime = globalLifeTime;
		this.cache = new ConcurrentCache();
		this.timeOutEvent = tot;
		startMonitory();
	}

	public LifeCycleCache(long globalLifeTime) {
		this.globalLifeTime = globalLifeTime;
		this.cache = new ConcurrentCache();
		this.timeOutEvent = null;
		startMonitory();
	}

	/**
	 * @param globalLifeTime
	 *            --存储器中所有对象默认存活时间，单位为毫秒ms
	 * @param initialCapacity
	 *            --缓冲器初始大小

	 */
	public LifeCycleCache(long globalLifeTime, int initialCapacity) {
		this.globalLifeTime = globalLifeTime;
		this.cache = new ConcurrentCache(initialCapacity);
		this.timeOutEvent = null;
		startMonitory();
	}

	public LifeCycleCache(long globalLifeTime, int initialCapacity,
			TimeOutEvent tot) {
		this.globalLifeTime = globalLifeTime;
		this.cache = new ConcurrentCache(initialCapacity);
		this.timeOutEvent = tot;
		startMonitory();
	}

	private void startMonitory() {
		Thread thead = new Thread(new CycleMinitor());
		thead.setDaemon(true);
		thead.start();
	}

	protected void finalize() throws Throwable {
		this.clear();
		this.stopflag = true;
		super.finalize();
	}

	private static class V {

		private long markTime;
		private Object obj;
		private long timeout;

		public V(long markTime, Object obj, long timeout) {
			this.markTime = markTime;
			this.obj = obj;
			this.timeout = timeout;
		}

		public long getTimeout() {
			return timeout;
		}

		public long getMarkTime() {
			return this.markTime;
		}

		public Object getObj() {
			return this.obj;
		}
	}

	public void clear() {
		cache.clear();
	}

	public boolean containsKey(Object key) {
		return cache.containsKey(key);
	}
 
	public Set<?> entrySet() {
		return cache.entrySet();
	}

	/*
	 * 从缓冲器获取值，若超出时间则返回null （缓冲器会保证过期的对象会从缓冲器从自动清除）

	 */
	public Object get(Object key) {
		Object o = cache.get(key);
		if (o == null) {
			return null;
		} else {
			V v = (V) o;
			long now = System.currentTimeMillis();
			long markTime = v.getMarkTime();
			long k = now - markTime;
			if (k > v.getTimeout()) {
				this.remove(key);
				return null;
			}
			return v.getObj();
		}
	}

	public boolean isEmpty() {
		return cache.isEmpty();
	}

	public Set<?> keySet() {
		return cache.keySet();
	}

	/*
	 * 往缓冲器中存放对象 -此对象存活的时间(globalLifeTime)ms
	 */
	public void put(Object key, Object value) {
		V v = new V(System.currentTimeMillis(), value, this.globalLifeTime);
		cache.put(key, v);
	}

	/**
	 * 往缓冲器中存放对象
	 * 
	 * @param key
	 * @param value
	 * @param lifeTime
	 *            --此对象存活的时间（单位：ms毫秒）

	 */
	public void put(Object key, Object value, long lifeTime) {
		V v = new V(System.currentTimeMillis(), value, lifeTime);
		cache.put(key, v);
	}

	public void remove(Object key) {
		Object o = cache.get(key);
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
		cache.remove(key);
	}

	public int size() {
		return cache.size();
	}

	public Collection<?> values() {
		return cache.values();
	}

}
