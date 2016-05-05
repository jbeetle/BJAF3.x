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
package com.beetle.framework.util.structure;

import java.util.NoSuchElementException;
import java.util.Vector;

public class DataStack {
	private Vector<Object> list = new Vector<Object>();

	public DataStack() {
	} 

	/**
	 * Stack push.
	 * 
	 * @param o
	 */
	public void push(Object o) {
		list.addElement(o);
	}

	/**
	 * Stack pop.
	 * 
	 * @return poped object from stack
	 */
	public Object pop() {
		Object result = null;
		try {
			result = list.lastElement();
			list.remove(list.size() - 1);
		} catch (NoSuchElementException nsee) {
		}
		return result;
	}

	public Object[] popAll() {
		Object[] res = list.toArray();
		list.clear();
		return res;
	}

	/**
	 * Peek element from stack.
	 * 
	 * @return peeked object
	 */
	public Object peek() {
		return list.lastElement();
	}

	/**
	 * Is stack empty?
	 * 
	 * @return true if stack is empty
	 */
	public boolean isEmpty() {
		return list.isEmpty();
	}

	public void clearAll() {
		list.removeAllElements();
	}
}
