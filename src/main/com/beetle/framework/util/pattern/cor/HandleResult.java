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
package com.beetle.framework.util.pattern.cor;

/**
 * <p>
 * Title: BeetleSoft Framework
 * </p>
 * 
 * <p>
 * Description: NodeHandler结果对象，负责传递结果和判断NodeHandler对象是否运行成功
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: 甲壳虫软件
 * </p>
 * 
 * @author 余浩东
 * @version 1.0
 */
public class HandleResult {
	public HandleResult() {
	}

	public HandleResult(boolean succeeded) {
		this.succeeded = succeeded;
	}

	private boolean succeeded;

	/**
	 * 是否成功，如果成功返回true
	 * 
	 * @return boolean
	 */
	public boolean isSuccessful() {
		return succeeded;
	}

	/**
	 * 设置成功标记，如果设置为true表示链表执行到此为止，不会再执行下一个节点（任务）
	 * 
	 * @param b
	 *            boolean
	 */
	public void setSuccessful(boolean b) {
		succeeded = b;
	}

}
