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
package com.beetle.framework.util.thread.task;

import com.beetle.framework.util.UUIDGenerator;

/**
 * <p>
 * Title: Beetle AppServer Framework
 * </p>
 * 
 * <p>
 * Description: 子程序抽象类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: BeetleSoft
 * </p>
 * 
 * @author HenryYu (yuhaodong@gmail.com)
 * @version 1.0
 */
public abstract class TaskImp implements Runnable {

	private boolean joinMonFlag;
	private boolean killWhenTimeout;
	private long lastTime; // 最新时间

	private String id; // 线程名称
	private boolean stopped;// 线程停止标记
	private long maxIdle; // 最大空闲时间
	private Thread runThisThread;// 运行这个子程序的线程，是线程池中的线程
	private Object result;// 子程序返回的结果

	/**
	 * 设置子程序返回结果 routine方法体内调用才有效。适合需要返回结果的子程序（带超时执行机制）
	 * 由RoutineExecutor.runRoutineForResult方法执行
	 * 
	 * @param result
	 */
	protected void setResult(Object result) {
		this.result = result;
	}

	/**
	 * 获取最大允许阻塞的时间，单位为毫秒
	 * 
	 * 
	 * @return
	 */
	long getMaxIdle() {
		return maxIdle;
	}

	Object getResult() {
		return result;
	}

	/**
	 * 参与后台监控
	 * 
	 * @param maxBlockTime
	 *            --最大允许阻塞时间（单位为毫秒） 当maxBlockTime<=0时，不参与后台监控
	 */
	public TaskImp(long maxBlockTime) {
		this.id = "subroutine[" + UUIDGenerator.generateUUID() + "]"; 
		this.killWhenTimeout = false;
		this.maxIdle = maxBlockTime;
		if (maxIdle > 0) {
			this.joinMonFlag = true;
			this.stopped = false;
		} else {
			this.joinMonFlag = false;
			this.stopped = false;
		}
	}

	protected void sleep(long time) throws InterruptedException {
		Thread.sleep(time);
	}

	/**
	 * 不参与监控
	 */
	public TaskImp() {
		this(0);
	}

	/**
	 * 超时触发事件, 对于不同类型的阻塞中断，可以重载此方法，加入具体的终止线程代码 如果此子程序在执行过程中超时，则会触发此方法
	 */
	protected void timeoutEvent() {
	}

	/**
	 * 
	 */
	void terminated() {
		try {
			timeoutEvent();
		} catch (Throwable te) {
			te.printStackTrace();
		} finally {
			sp();
		}
	}

	/**
	 * @deprecated
	 */
	@SuppressWarnings("rawtypes")
	private void sp() {
		// this.stop();
		if (this.result != null) {
			if (this.result instanceof java.util.Map) {
				((java.util.Map) this.result).clear();
			} else if (this.result instanceof java.util.Collection) {
				((java.util.Collection) this.result).clear();
			}
			this.result = null;
		}
	}

	boolean isOvertime() {
		long curIdle = (System.currentTimeMillis() - lastTime);
		if (maxIdle > 0 && (curIdle >= maxIdle)) {
			return true;
		} else {
			return false;
		}
	}

	protected boolean isJoinMonFlag() {
		return joinMonFlag;
	}

	final public void run() {
		try {
			if (joinMonFlag) {
				this.lastTime = System.currentTimeMillis();
				TaskThreadPool.RoutineMonitor.putRoutineIntoCache(this);
			}
			routine();
		} catch (Throwable e) {
			throw new TaskRunException("raise err,thread interrpting!", e);
		} finally {
			end();
		}
	}

	/**
	 * 子程序的执行方法（入口），在此方法实现具体的工作代码。
	 */
	protected abstract void routine() throws InterruptedException;

	/**
	 * 正常结束时会调用此方法
	 */
	protected void end() {
		this.stopped = true;
		if (joinMonFlag) {
			TaskThreadPool.RoutineMonitor.removeFromCache(id);
		}
	}

	/**
	 * 获取执行这个子程序的线程
	 * 
	 * @return
	 */
	public Thread getRunThisRoutineThread() {
		// return RoutinesPool.getRoutineThreadFromPool(getThreadName());
		return runThisThread;
	}

	void setRunThisThread(Thread runThisThread) {
		this.runThisThread = runThisThread;
	}

	/**
	 * 获取执行这个子程序的名称
	 * 
	 * @return
	 */
	String getId() {
		return id;
	}

	boolean isStopped() {
		return stopped;
	}

	boolean isKillWhenTimeout() {
		return killWhenTimeout;
	}

	/**
	 * 在默认情况下，此子程序所执行的线程在超时后不会被强行杀死。<br>
	 * 如果在此子程序执行之前，显性调用此方法，则线程在超时后会被强行杀死<br>
	 */
	public TaskImp killThreadWhenTimeout() {
		this.killWhenTimeout = true;
		return this;
	}
}
