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
package com.beetle.framework.business.command;

import java.io.Serializable;

public abstract class CommandImp implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int FATAL_ERR_FLAG = -1000;
	public static final int BREAK_OFF_FLAG = -1001;
	public static final int SUCCEED_FLAG = 0;
	public static final String SUCCEED_MSG = "ok";
	private int returnFlag = SUCCEED_FLAG;
	private String returnMsg = SUCCEED_MSG;
	private Object plus; // 附加信息，一般作为总横切时候，数据传递之用

	// protected static final transient SysLogger logger = SysLogger
	// .getInstance(CommandImp.class);

	/**
	 * 实现业务逻辑过程
	 * 
	 * @throws CommandException
	 */
	public abstract void process() throws CommandException;

	/**
	 * 手工会滚事务，只有事务执行方式才有效
	 * 
	 * @throws CommandException
	 */
	public void rollbackByHand() throws CommandException {
		throw new CommandException(String.valueOf(FATAL_ERR_FLAG));
	}

	/**
	 * 获取command执行后的运行状态标记
	 * 
	 * 
	 * @return int
	 */
	public int getReturnFlag() {
		return returnFlag;
	}

	/**
	 * 获取command执行后的运行状态情况说明
	 * 
	 * 
	 * @return String
	 */
	public String getReturnMsg() {
		return returnMsg;
	}

	/**
	 * 获取附加信息对象，一般作为全局横切的数据传送对象，平时不应采取它来作为数据的输入参数
	 * 
	 * 
	 * @return Object
	 */
	public Object getPlus() {
		return plus;
	}

	/**
	 * 设置命令执行状态情况说明
	 * 
	 * 
	 * @param returnMsg
	 *            String
	 */
	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

	/**
	 * 设置命令执行状态标记
	 * 
	 * 
	 * @param returnMsg
	 *            String
	 */
	public void setReturnFlag(int returnFlag) {
		this.returnFlag = returnFlag;
	}

	/**
	 * 设置附加对象，附加对象一般作为全局横切的数据传送对象， 一般情况下，不应该用来作为一个command的参数来使用
	 * 
	 * @param plus
	 *            Object
	 */
	public void setPlus(Object plus) {
		this.plus = plus;
	}

}
