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
package com.beetle.framework.util.thread;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 支持高并发性能的计数器 如果计算器爆了，会根据初始值重新开始
 */
public class Counter {
	private final AtomicLong count;
	private long initialValue;

	public Counter() {
		this.count = new AtomicLong(0);
		this.initialValue = 0;
	}

	public Counter(long initialValue) {
		this.count = new AtomicLong(initialValue);
		this.initialValue = initialValue;
	}

	public long increaseAndGet() {
		try {
			return count.incrementAndGet();
		} catch (Throwable e) {
			synchronized (this) {
				count.set(initialValue);
				return count.incrementAndGet();
			}
		}
	}

	/**
	 * 获取当前计数器的值
	 * 
	 * 
	 * @return
	 */
	public long getCurrentValue() {
		return this.count.get();
	}

	/**
	 * 加1
	 */
	public void increase() {
		try {
			this.count.incrementAndGet();
		} catch (Throwable e) {
			synchronized (this) {
				count.set(initialValue);
				this.count.incrementAndGet();
			}
		}
	}

	/**
	 * 减1
	 */
	public void decrease() {
		try {
			this.count.decrementAndGet();
		} catch (Throwable e) {
			synchronized (this) {
				count.set(initialValue);
				this.count.decrementAndGet();
			}
		}
	}
}
