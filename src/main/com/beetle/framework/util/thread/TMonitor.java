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
 * <p>Title: FrameWork</p>
 * <p>Description: 后台线程监控类</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: 甲壳虫软件</p>
 * @author 余浩东

 * @version 1.0
 */

import com.beetle.framework.log.AppLogger;
import com.beetle.framework.util.ClassUtil;
import com.beetle.framework.util.OtherUtil;
import com.beetle.framework.util.thread.ThreadImp.TimeOutMode;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

final public class TMonitor extends RunWrapper {

	/**
	 * 获取监控器启动的时间
	 */
	public static long getMonitorStartTime() {
		return m.getStartTime();
	}

	/**
	 * 获取监控器最近一次工作的时间
	 */
	public static long getMonitorLastTime() {
		return m.getLastTime();
	}

	private static HashMap<String, MonitorInfo> monitorInfoCache = new HashMap<String, MonitorInfo>();// MonitorInfo后台任何的缓存

	static void addMonitorInfoIntoCache(String key, MonitorInfo mInfo) {
		monitorInfoCache.put(key, mInfo);
	}

	/**
	 * 获取后台监控对象列表的快照
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static HashMap<String, MonitorInfo> getMonitorInfoCahceSnapshot() {
		return (HashMap<String, MonitorInfo>) monitorInfoCache.clone();
	}

	/**
	 * 线程模块后台监控信息对象
	 * 
	 */
	public static class MonitorInfo implements Serializable {

		@Override
		public String toString() {
			return "MonitorInfo [dealFlag=" + dealFlag + ", id=" + id
					+ ", startTime=" + startTime + ", lastTime=" + lastTime
					+ ", status=" + status + ", plusInfo=" + plusInfo + "]";
		}

		public int getDealFlag() {
			return dealFlag;
		}

		public void setDealFlag(int dealFlag) {
			this.dealFlag = dealFlag;
		}

		public final static int RUNNING_STATUS = 1;
		public final static int STOPPED_STATUS = 2;
		public final static int READY_STATUS = 0;
		private int dealFlag;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public long getStartTime() {
			return startTime;
		}

		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}

		public long getLastTime() {
			return lastTime;
		}

		public void setLastTime(long lastTime) {
			this.lastTime = lastTime;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public String getPlusInfo() {
			return plusInfo;
		}

		public void setPlusInfo(String plusInfo) {
			this.plusInfo = plusInfo;
		}

		private String id;
		private long startTime;
		private long lastTime;
		private int status;
		private String plusInfo;
		private static final long serialVersionUID = 1L;

	}

	/**
	 * 线程监控器是否已经启动 返回true为已启动
	 */
	public static boolean isStarted() {
		return startedFlag;
	}

	private final static ConcurrentHashMap<String, ThreadImp> cacheThread = new ConcurrentHashMap<String, ThreadImp>();
	private static AppLogger logger = AppLogger.getInstance(TMonitor.class);
	private static TMonitor m = new TMonitor(); //
	private static boolean startedFlag = false;

	private TMonitor() {
		super("BJAF_TMonitor");
	}

	/**
	 * 启动后台线程监控器，重启无效
	 */
	public synchronized static void startMonitor() {
		if (!startedFlag) {
			m.startAsDaemon();
			startedFlag = true;
			if (logger.isDebugEnabled()) {
				logger.debug("TMonitor started");
			}
		}
	}

	/**
	 * 停止后台线程监控器，停止后不能重启
	 */
	public synchronized static void stopMonitor() {
		if (m != null) {
			m.stop();
			startedFlag = false;
		}
	}

	/**
	 * 把线程放入缓存，以便后台监控
	 * 
	 * @param appRunnable
	 *            AppRunnable
	 */
	public static void putIntoCache(ThreadImp appRunnable) {
		//if (!cacheThread.containsKey(appRunnable.getName())) {
			cacheThread.put(appRunnable.getName(), appRunnable);
		//}
	}

	/**
	 * 将线程从缓存删除，以便后台不再监控
	 * 
	 * 
	 * @param appRunnable
	 *            AppRunnable
	 */
	public static void removeFromCache(ThreadImp appRunnable) {
		if (cacheThread.containsKey(appRunnable.getName())) {
			cacheThread.remove(appRunnable.getName());
		}
	}

	private static void clearCacheThread() {
		cacheThread.clear();
		monitorInfoCache.clear();
	}

	protected void end() {
		Iterator<ThreadImp> it = cacheThread.values().iterator();
		while (it.hasNext()) {
			RunWrapper aRun = it.next();
			if (logger.isDebugEnabled()) {
				logger.debug(aRun.getName());
			}
			aRun.stop();
			this.sleep(200);
		}
		clearCacheThread();
		if (logger.isInfoEnabled()) {
			logger.info("all the threads by monitored had stopped!");
		}
	}

	private static void renew(ThreadImp imp) throws InterruptedException {
		String tname = imp.getName();
		String className = imp.getClass().getName();
		Constructor<?> ctts[] = imp.getClass().getConstructors();
		int ctflag = 3;
		for (int i = 0; i < ctts.length; i++) {
			Class<?> pts[] = ctts[i].getParameterTypes();
			ctflag = pts.length;
			OtherUtil.clearArray(pts);
			if (i >= 0) {
				break;// 只找一次
			}
		}
		@SuppressWarnings("rawtypes")
		Class[] constrParamTypes;
		Object[] constrParamValues;
		if (ctflag == 4) {
			constrParamTypes = new Class[] { String.class, long.class,
					long.class, TimeOutMode.class };
			constrParamValues = new Object[] { imp.getName(),
					Long.valueOf(imp.gettimeoutTime()),
					Long.valueOf(imp.getInterval()),
					ThreadImp.TimeOutMode.BreakOffAndStartAgain };
		} else {
			logger.error(className + ",don't by rule,not support restart");
			return;
		}
		boolean daemonFlag = imp.getThread().isDaemon();
		stopit(imp);
		imp = null;
		try {
			imp = (ThreadImp) ClassUtil.newInstance(className,
					constrParamTypes, constrParamValues);
			if (imp != null) {
				putIntoCache(imp);
				if (daemonFlag) {
					imp.startAsDaemon();
				} else {
					imp.start();
				}
			} else {
				logger.error("imp is null,restart failed");
			}
			if (logger.isInfoEnabled()) {
				logger.info("renew[" + tname + "]OK!");
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			OtherUtil.clearArray(constrParamValues);
			OtherUtil.clearArray(constrParamTypes);
			OtherUtil.clearArray(ctts);
		}
	}

	private static void stopit(ThreadImp imp) throws InterruptedException {
		imp.stop(); // 停止线程
		OtherUtil.blockSomeTime(cacheThread, 50);
		if (!imp.interrupted()) {// 如果没有停下来则粗暴对待
			imp.stopBrutally();
			if (logger.isInfoEnabled()) {
				logger.info("stop the thread[" + imp.getName() + "] brutally!");
			}
		}
		removeFromCache(imp);
	}

	/**
	 * 根据被监控线程的名称，重启此线程
	 * 
	 * @param appThreadName
	 */
	public static void reStartAppThread(String appThreadName) {
		ThreadImp aRun = cacheThread.get(appThreadName);
		if (aRun != null) {
			try {
				// renewAppThreadImp(aRun);
				renew(aRun);
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	public void run() {
		while (!this.getStopFlag()) {
			try {
				monitor();
				this.resetIdleTime();
				sleep(250);
			} catch (Throwable e) {
				logger.error("monitor run raise err", e);
			}
		}
	}

	private void monitor() {
		Iterator<ThreadImp> it = cacheThread.values().iterator();
		while (it.hasNext()) {
			ThreadImp aRun = it.next();
			if (aRun == null || !aRun.isStoped()) {
				continue;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("ThreadImp:" + aRun.getName());
			}
			try {
				MonitorInfo min = monitorInfoCache.get(aRun.getName());
				if (min != null) {
					if (min.getDealFlag() == 2) {
						renew(aRun);
					} else if (min.getDealFlag() == 1) {
						killit(aRun);
					}
				}
			} catch (Throwable e) {
				logger.error(e);
			}
		}
	}

	private void killit(ThreadImp aRun) throws InterruptedException {
		String name = aRun.getName();
		stopit(aRun);
		monitorInfoCache.remove(name);
	}
}
