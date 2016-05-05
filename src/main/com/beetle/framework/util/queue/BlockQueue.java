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
package com.beetle.framework.util.queue;

import com.beetle.framework.AppRuntimeException;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class BlockQueue implements IQueue {
	public BlockQueue() {
		q = new LinkedBlockingQueue<Object>();
	}

	public Object peek() {
		return q.peek();
	}

	private LinkedBlockingQueue<Object> q;

	public Object poll() {
		return q.poll();
	}

	public void clear() {
		q.clear();
	}

	public boolean isEmpty() {
		return q.isEmpty();
	}

	public Object pop() {
		try {
			return q.take();
		} catch (InterruptedException e) {
			throw new AppRuntimeException(e);
		}
	}

	/**
	 * Retrieves and removes the head of this queue, waiting up to the specified
	 * wait time if necessary<br>
	 * for an element to become available.
	 * 
	 * @param timeout
	 *            --ms
	 * @return the head of this queue, or null if the specified waiting time
	 *         elapses before an element is available
	 */
	public Object pop(long timeout) {
		try {
			return q.poll(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new AppRuntimeException(e);
		}
	}

	public void push(Object obj) {
		try {
			q.put(obj);
		} catch (InterruptedException e) {
			throw new AppRuntimeException(e);
		}

	}

	public int size() {
		return q.size();
	}

}
