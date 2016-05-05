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
 * Description: 线程接口封装类
 * <p>
 * RunWrapper有两个基本属性，theadName线程名称，是这个线程的标识。timeout超时时间，单位为毫秒，
 * 此类作为一个线程框架抽象的基础类，除非特殊需求，否则不推荐直接继承使用， 建议使用ThreadImp抽象类
 */
public abstract class RunWrapper implements Runnable {

	/**
	 * 获取线程启动时间（固定）
	 */
	public long getStartTime() {
		return iStartTime;
	}

	/**
	 * 获取此线程最近一次工作时间
	 */
	public long getLastTime() {
		return iLastTime;
	}

	private volatile boolean bStop;

	private transient Thread thread; // 线程
	private long iStartTime;// 线程启动时间
	private long iLastTime; // 最新时间
	static final AtomicLong couter = new AtomicLong(0);
	private String sThreadName; // 线程名称

	private long itimeout; // 超时时间

	public RunWrapper() {
		bStop = true;
		thread = null;
		iLastTime = 0L;
		itimeout = 0;
		sThreadName = "BJAF-TAR-" + couter.incrementAndGet();
	}

	/**
	 * RunWrapper
	 * 
	 * @param timeout
	 *            超时时间，单位为毫秒
	 */
	RunWrapper(long timeout) {
		this();
		settimeout(timeout);
	}

	/**
	 * RunWrapper
	 * 
	 * @param threadName
	 *            线程名称
	 */
	public RunWrapper(final String threadName) {
		bStop = true;
		thread = null;
		iLastTime = 0L;
		itimeout = 0;
		sThreadName = threadName;
	}

	/**
	 * RunWrapper
	 * 
	 * @param threadName
	 *            线程名称
	 * @param MaixIdle
	 *            超时时间，单位为秒
	 */
	RunWrapper(final String threadName, long timeout) {
		this(threadName);
		settimeout(timeout);
	}

	/**
	 * 设置超时时间，单位为秒
	 * 
	 * @param i
	 *            int
	 */
	void settimeout(long i) {
		itimeout = i;
	}

	/**
	 * 返回线程的名称
	 * 
	 * 
	 * @return String
	 */
	public String getName() {
		return sThreadName;
	}

	boolean interrupted() {
		return Thread.interrupted();
	}

	long gettimeoutTime() {
		return itimeout;
	}

	/**
	 * 检测当前线程是否已经结束 若run方法调用isStopped来作为检查一个循环接受标记，
	 * 要在run方法体内显性调用resetIdleTime方法来每次复位时间
	 * 
	 * 
	 * @return 返回true为已经结束
	 */
	public boolean isStoped() {
		if (thread == null) {
			return true;
		}
		if (getIdleTime2() > gettimeoutTime()) {
			return true;
		}
		if (getStopFlag()) {
			return true;
		}
		return false;
	}

	/**
	 * 启动线程 多次启动一个线程是非法的。特别是当线程已经结束执行后，不能再重新启动。
	 * 
	 */
	public void start() {
		bStop = false;
		thread = new Thread(this, this.getName());
		thread.start();
		iLastTime = System.currentTimeMillis();
		iStartTime = iLastTime;
	}

	/**
	 * 作为守护线程来启动 多次启动一个线程是非法的。特别是当线程已经结束执行后，不能再重新启动。
	 * 
	 * 
	 * @return
	 */
	public void startAsDaemon() {
		bStop = false;
		thread = new Thread(this, this.getName());
		thread.setDaemon(true);
		thread.start();
		iLastTime = System.currentTimeMillis();
		iStartTime = iLastTime;
	}

	/**
	 * 返回当前已空闲的时间（线程已运行的时间ms）
	 * 
	 * 
	 * @return
	 */
	long getIdleTime2() {
		long l = (System.currentTimeMillis() - iLastTime);
		return l;
	}

	/**
	 * 重置空闲时间，重置后通过getIdleTime()获取的空闲时间为0
	 */
	public void resetIdleTime() {
		iLastTime = System.currentTimeMillis();
	}

	/**
	 * 调用stop触发的事件
	 */
	protected void stopEvent() {
	}

	/**
	 * 停止线程
	 */
	public void stop() {
		try {
			stopEvent();
		} finally {
			stopByFlag();
		}
	}

	/**
	 * 线程休眠
	 * 
	 * @param iTime
	 *            休眠时间，单位为毫秒
	 */
	public void sleep(long iTime) {
		if (thread == null) {
			return;
		}
		if (iTime < 0) {
			return;
		}
		try {
			Thread.sleep(iTime);
		} catch (InterruptedException e) {
			// throw new AppRuntimeException(this.getName() + ",sleep err", e);
		}

	}

	/**
	 * 返回当前线程对象
	 * 
	 * @return Thread
	 */
	public Thread getThread() {
		if (thread == null) {
			return Thread.currentThread();
		} else {
			return thread;
		}
	}

	/**
	 * 返回当前结束标记状态
	 * 
	 * 
	 * @return boolean
	 */
	protected boolean getStopFlag() {
		return bStop;
	}

	/**
	 * 通过标记结束线程，但不触发end事件
	 */
	private void stopByFlag() {
		bStop = true;
	}

	/**
	 * 粗暴地停止此线程 终止之前先调用stop
	 */
	public void stopBrutally() {
		try {
			this.stop();
			this.interrupt();
			kill();
		} catch (Throwable e) {
			// e.printStackTrace();
		}
	}

	/**
	 * @deprecated
	 */
	private void kill() {
		getThread().stop();
	}

	/**
	 * 中断线程
	 */
	public void interrupt() {
		getThread().interrupt();
	}

	/**
	 * 线程运行主方法
	 */
	public abstract void run();

	@Override
	public String toString() {
		return "RunWrapper [bStop=" + bStop + ", iStartTime=" + iStartTime
				+ ", iLastTime=" + iLastTime + ", sThreadName=" + sThreadName
				+ ", itimeout=" + itimeout + "]";
	}
}
