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

import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.util.OtherUtil;
import com.beetle.framework.util.thread.TMonitor.MonitorInfo;

/**
 * <p>
 * Title: FrameWork
 * </p>
 * <p>
 * Description: 应用线程抽象类<br>
 * 
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: 甲壳虫软件
 * 
 * </p>
 * 
 * @author 余浩东
 * 
 * @version 1.0
 */

public abstract class ThreadImp extends RunWrapper {

	private MonitorInfo mInfo = null;
	private long interval;

	public enum CycleMode {
		AUTO(0), MANUAL(1);
		private int id;

		private CycleMode(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}
	};

	public enum TimeOutMode {
		BreakOff(1), BreakOffAndStartAgain(2);
		public int getValue() {
			return value;
		}

		private int value;

		private TimeOutMode(int value) {
			this.value = value;
		}
	};

	private CycleMode cycleMode;
	private TimeOutMode timeOutMode;
	private volatile boolean done;
	private static AppLogger logger = AppLogger.getInstance(ThreadImp.class);

	/**
	 * 如果线程超过这个超时时间还没有结束的话，则可采取两种处理方式：<br>
	 * （1）停止此线程处理（中断线程）BreakOff<br>
	 * （2）停止这个线程，并重新拉起此线程（重开一个新的）。BreakOffAndStartAgain<br>
	 * 这个模型的基本运行流程为： <br>
	 * （1），设置超时时间，创建一个线程对象。 <br>
	 * （2），调用start()方法启动线程，自动调用run()方法完成业务逻辑。 <br>
	 * （3），线程运行期间，Monitor检查线程阻塞情况，判别是否线程已经停止或超出超时时间。<br>
	 * （4），调用stop()方法结束线程的执行，结束前，触发stopEvent()事件。<br>
	 * [ 注意： 对于参与后台监控的应用线程，要保留构造函数，不能在现有构造函数的基础上 新添加构造参数。]
	 * 
	 * @param threadName
	 *            线程名称
	 * @param timeout
	 *            超时时间，单位为ms毫秒， 超过此空闲时间，系统后台监控线程会认为线程已经瘫痪
	 * @param interval
	 *            运行间隔时间，单位为ms毫秒
	 * @param timeOutMode
	 * 
	 */
	public ThreadImp(final String threadName, long timeout, long interval,
			TimeOutMode timeOutMode) {
		super(threadName, timeout);
		this.interval = interval;
		if (timeout <= 0) {
			throw new AppRuntimeException("timeout must >0");
		}
		this.cycleMode = CycleMode.AUTO;
		// joinMonitor(timeOutMode.getValue());
		this.timeOutMode = timeOutMode;
	}

	/**
	 * 
	 * @param threadName
	 *            --线程名称
	 * @param interval
	 *            --执行间隔,单位为ms毫秒
	 */
	public ThreadImp(final String threadName, long interval) {
		super(threadName, 0);
		this.interval = interval;
		this.cycleMode = CycleMode.AUTO;
	}

	/**
	 * @param threadName
	 *            --线程名称,执行间隔0毫秒
	 */
	public ThreadImp(final String threadName) {
		super(threadName, 0);
		this.interval = 0;
		this.cycleMode = CycleMode.AUTO;
	}

	@Override
	public void startAsDaemon() {
		if (this.gettimeoutTime() > 0) {
			joinMonitor(timeOutMode.getValue());
		}
		super.startAsDaemon();
		if (mInfo != null) {
			mInfo.setStartTime(this.getStartTime());
		}
	}

	/**
	 * 线程任务（routine方法）是否已经执行完毕
	 * 
	 * @return
	 */
	public boolean isRoutineDone() {
		return done;
	}

	/**
	 * 启动线程执行，并在定义的超时间内阻塞等待线程结束。<br>
	 * 超时后，任务没有结束的话，依然回收（结束）线程 <br>
	 * 注意：此方法只针对 ThreadImp(final String threadName, long interval) 和
	 * ThreadImp(final String threadName)两个构造函数创建的线程有效
	 */
	public void startAndWaitForTimeOut(long timeout) {
		this.settimeout(timeout);
		if (this.gettimeoutTime() <= 0) {
			throw new AppRuntimeException("timeout value must >0");
		}
		// this.start();
		super.start();
		try {
			this.getThread().join(gettimeoutTime());
		} catch (Exception e) {
		} finally {
			this.stop();
			try {
				OtherUtil.blockSomeTime(this, 10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				if (!this.isRoutineDone()) {
					this.stopBrutally();
				}
			}
		}
	}

	public void start() {
		if (this.gettimeoutTime() > 0) {
			joinMonitor(timeOutMode.getValue());
		}
		super.start();
		if (mInfo != null) {
			mInfo.setStartTime(this.getStartTime());
		}
	}

	public void stop() {
		try {
			this.interrupt();
		} finally {
			super.stop();
		}
	}

	public void cancelMonitored() {
		TMonitor.removeFromCache(this);
	}

	private void joinMonitor(int dealFlag) {
		mInfo = new MonitorInfo();
		mInfo.setId(this.getName());
		// mInfo.setStartTime(startTime)
		mInfo.setStatus(MonitorInfo.READY_STATUS);
		mInfo.setDealFlag(dealFlag);
		TMonitor.addMonitorInfoIntoCache(mInfo.getId(), mInfo);
		if (!TMonitor.isStarted()) {
			TMonitor.startMonitor();
		}
		TMonitor.putIntoCache(this);
	}

	@Override
	final public void run() {
		this.done = false;
		if (logger.isDebugEnabled()) {
			logger.debug("begin info");
			logger.debug(this);
		}
		if (this.cycleMode.getId() == CycleMode.MANUAL.getId()) { // 自己手动控制循环
			this.resetIdleTime();
			try {
				routine();
				markMInfoRunning();
			} catch (Throwable e) {
				logger.error(this.getName(), e);
			} finally {
				this.done = true;
				end();
			}
		} else {
			try {
				while (!interrupted() && !getStopFlag()) { // 根据执行标记还决定线程的持续运行
					this.resetIdleTime(); // 复位当然线程时间，为后台监控线程提供计算标准
					try {
						routine();// 执行业务逻辑方法
					} catch (Throwable e) {
						if (e instanceof ThreadDeath) {
							logger.error(getName()
									+ " timeout,was broke off by monitor!");
						} else {
							logger.error(this.getName(), e);
						}
					}
					markMInfoRunning();
					if (interval > 0) {
						this.sleep(interval); // 可以根据需要让线程休眠一段时间，让cpu更合理的分配任务
					}
					// logger.debug(this);
				}
				this.settimeout(-1);// 标明调用stopNow方法已经结束，从通过isStopped反应
			} finally {
				this.done = true;
				end();
			}
		}
	}

	private void markMInfoRunning() {
		if (mInfo != null) {
			mInfo.setLastTime(this.getLastTime());
			mInfo.setStatus(MonitorInfo.RUNNING_STATUS);
		}
	}

	/**
	 * 线程结束事件
	 */
	protected void end() {
		if (mInfo != null) {
			mInfo.setStatus(MonitorInfo.STOPPED_STATUS);
		}
	}

	/**
	 * 工作过程<br>
	 * 在CycleMode的auto模式下（默认），无需考虑线程后台循环执行逻辑,只需实现业务逻辑即可。另外，此方法调用时已做异常处理，
	 * 即使出现异常也不会中断循环体， 所以在此方法体内无效考虑异常的捕捉。
	 */
	protected abstract void routine() throws Throwable;

	/**
	 * 设置此线程执行间隔时间
	 * 
	 * 
	 * @param interval
	 *            单位为毫秒
	 */
	public void setInterval(long interval) {
		this.interval = interval;
	}

	/**
	 * 根据输入最小值和最大值算出随机数。
	 * 
	 * @param min
	 * @param max
	 */
	public void randomInterval(int min, int max) {
		this.setInterval(OtherUtil.randomInt(min, max));
	}

	CycleMode getCycleMode() {
		return cycleMode;
	}

	public void setCycleMode(CycleMode cycleMode) {
		this.cycleMode = cycleMode;
	}

	/**
	 * 返回运行间隔，单位毫秒
	 * 
	 * 
	 * @return
	 */
	public long getInterval() {
		return interval;
	}

	@Override
	public String toString() {
		return "ThreadImp [mInfo=" + mInfo + ", interval=" + interval
				+ ", cycleMode=" + cycleMode + ", done=" + done + "]";
	}
}
