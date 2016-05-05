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


/**
 * 简单对象锁 简化Object的.wait和notify编程, 用于跨越多个线程锁
 * 
 * 
 */
public class Locker {

	public Locker() {
		this.inuse_ = true;
	}

	private boolean inuse_;

	/**
	 * 锁住（阻塞当前主流程）
	 * 
	 * 
	 * @throws InterruptedException
	 */
	public final void lock() throws InterruptedException {
		if (Thread.interrupted())
			throw new InterruptedException();
		synchronized (this) {
			try {
				while (inuse_)
					wait();
			} catch (InterruptedException ex) {
				notify();
				throw ex;
			}
		}
	}

	/**
	 * 锁住一段时间（在其他线程调用此对象的unlock方法之前，或者超过指定的时间量前，导致当前线程等待）
	 * 
	 * @param msecs
	 *            --要等待的最长时间，毫秒ms
	 * @throws InterruptedException
	 */
	public final void lockForTime(long msecs) throws InterruptedException {
		if (Thread.interrupted())
			throw new InterruptedException();
		synchronized (this) {
			long waitTime = msecs;
			long start = System.currentTimeMillis();
			try {
				for (;;) {
					wait(waitTime);
					if (!inuse_) {
						break;
					} else {
						waitTime = msecs - (System.currentTimeMillis() - start);
						if (waitTime <= 0) {
							break;
						}
					}
				}
			} catch (InterruptedException ex) {
				notify();
				throw ex;
			}
		}
	}

	/**
	 * 解锁（恢复当前主流程）
	 */
	public final synchronized void unlock() {
		inuse_ = false;
		notify();
	}

}
