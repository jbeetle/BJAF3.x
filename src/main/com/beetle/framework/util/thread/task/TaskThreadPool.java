package com.beetle.framework.util.thread.task;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.beetle.framework.AppProperties;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.util.thread.RunWrapper;

public class TaskThreadPool extends ThreadPoolExecutor {
	private final static AppLogger logger = AppLogger
			.getInstance(TaskThreadPool.class);

	static {
		if (!RoutineMonitor.isMonitorAlive()) {
			new RoutineMonitor().startAsDaemon();
		}
	}

	/**
	 * 满了主线程执行策略池
	 * 
	 * @return
	 */
	public static TaskThreadPool getCommonPool() {
		if (instance == null) {
			int pool_max = AppProperties.getAsInt("routinespool_POOL_MAX_SIZE",
					200);
			int pool_min = AppProperties.getAsInt("routinespool_POOL_MIN_SIZE",
					25);
			int pool_ide = AppProperties.getAsInt(
					"routinespool_IDLESSE_MINUTE", 5) * 1000 * 60;
			instance = new TaskThreadPool(pool_min, pool_max, pool_ide, false,
					new CallerRunsPolicy());
		}
		return instance;
	}

	static class RoutineMonitor extends RunWrapper {

		private static ConcurrentHashMap<String, TaskImp> cache = new ConcurrentHashMap<String, TaskImp>(
				1334);
		private volatile static boolean aliveFlag = false;
		private int poo_monitor_interval;

		public RoutineMonitor() {
			super("TaskThreadPool-RoutineMonitor");
			selfStopFlag = false;
			poo_monitor_interval = AppProperties.getAsInt(
					"routinespool_MONITOR_INTERVAL", 250);
		}

		final static boolean isMonitorAlive() {
			return aliveFlag;
		}

		private boolean selfStopFlag;

		final static void removeFromCache(String threadName) {
			cache.remove(threadName);
		}

		final static void putRoutineIntoCache(TaskImp sbr) {
			// if (!cache.containsKey(sbr.getId())) {
			cache.put(sbr.getId(), sbr);
			// }
		}

		protected void end() {
			cache.clear();
			aliveFlag = false;
		}

		public void run() {
			aliveFlag = true;
			while (!getStopFlag()) {
				if (!cache.isEmpty()) {
					checkDie();
				}
				this.sleep(poo_monitor_interval);
				// System.out.println(poo_monitor_interval);
				if (cache.isEmpty()) {
					if (selfStopFlag) {
						this.stop();
					}
				}
			}
		}

		@SuppressWarnings("deprecation")
		private static void checkDie() {
			try {
				Set<?> kvs = cache.entrySet();
				Iterator<?> it = kvs.iterator();
				while (it.hasNext()) {
					@SuppressWarnings("rawtypes")
					Map.Entry kv = (Map.Entry) it.next();
					TaskImp sr = (TaskImp) kv.getValue();
					if (sr != null) {
						if (sr.isStopped()) {
							if (logger.isDebugEnabled()) {
								logger.debug("Thread:[" + sr.getId()
										+ "]normal stopped!");
							}
							sr = null;
							cache.remove(kv.getKey());
						} else {
							if (sr.isOvertime()) {
								Thread trd = sr.getRunThisRoutineThread();
								if (trd != null) {
									try {
										trd.interrupt();
										logger.info("[" + sr.getId()
												+ "]is interrupted!--");
										sr.terminated();
									} finally {
										if (sr.isKillWhenTimeout()) {
											trd.stop();
											logger.info("[" + sr.getId()
													+ "]is killed!--");
										}
										trd = null;
										sr = null;
										cache.remove(kv.getKey());
									}
								}
							}
						}
					} else {
						cache.remove(kv.getKey());
					}
				}
			} catch (Throwable e) {
				logger.error("checkDie thread err", e);
			} finally {
				// logger.debug("...xxx..");
			}
		}

		/**
		 * 默认为true，则内存不存在监控子线程，则自动退出
		 */
		public void disableSelfStop() {
			this.selfStopFlag = false;
		}

	}

	private volatile static TaskThreadPool instance = null;

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		TaskImp sr = (TaskImp) r;
		if (sr.isJoinMonFlag()) {
			sr.setRunThisThread(t);
		}
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {

	}

	/**
	 * 在池中执行
	 * 
	 * @param subRoutine
	 * @throws InterruptedException
	 */
	public boolean runInPool(TaskImp task) {
		try {
			execute(task);
			return true;
		} catch (RejectedExecutionException e) {// 采取CallerRunsPolicy应该不会有此异常，保留
			e.printStackTrace();
			return false;
		}
	}

	private static class DaemonThreadFactory implements ThreadFactory {
		static final AtomicInteger poolNumber = new AtomicInteger(1);
		final ThreadGroup group;
		final AtomicInteger threadNumber = new AtomicInteger(1);
		final String namePrefix;
		boolean isDaemonThread;

		private DaemonThreadFactory(boolean isDaemonThread) {
			SecurityManager s = System.getSecurityManager();
			group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
					.getThreadGroup();
			this.isDaemonThread = isDaemonThread;
			if (isDaemonThread) {
				namePrefix = "routinePool-" + poolNumber.getAndIncrement()
						+ "-daemonThread-";
			} else {
				namePrefix = "routinePool-" + poolNumber.getAndIncrement()
						+ "-thread-";
			}
		}

		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r, namePrefix
					+ threadNumber.getAndIncrement(), 0);
			if (isDaemonThread) {
				t.setDaemon(true);
			} else {
				if (t.isDaemon()) {
					t.setDaemon(false);
				}
			}
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
	}

	private TaskThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
			RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory, handler);
	}

	/**
	 * @param corePoolSize
	 * @param maximumPoolSize
	 * @param keepAliveTime
	 * @param workQueue
	 * @param isDaemonThread
	 * @param handler
	 */
	public TaskThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, BlockingQueue<Runnable> workQueue,
			boolean isDaemonThread, RejectedExecutionHandler handler) {
		this(corePoolSize, maximumPoolSize, keepAliveTime,
				TimeUnit.MILLISECONDS, workQueue, new DaemonThreadFactory(
						isDaemonThread), handler);
	}

	/**
	 * [采取SynchronousQueue，自己指定饱和策略]
	 * 
	 * @param corePoolSize
	 * @param maximumPoolSize
	 * @param keepAliveTime
	 *            --存活时间，单位ms
	 * @param isDaemonThread
	 * @param handler
	 */
	public TaskThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, boolean isDaemonThread,
			RejectedExecutionHandler handler) {
		this(corePoolSize, maximumPoolSize, keepAliveTime,
				TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(),
				new DaemonThreadFactory(isDaemonThread), handler);
	}

	/**
	 * [采取AbortPolicy策略和SynchronousQueue队列]<br>
	 * 新请求到达频率超过线程池处理它们的速度时，新请求不会放入等待队列，直接拒绝<br>
	 * 抛RejectedExecutionException<br>
	 * 
	 * @param corePoolSize
	 *            --核心池大小
	 * @param maximumPoolSize
	 *            --池最大大小
	 * @param keepAliveTime
	 *            --池存活的时间，单位毫秒
	 * @param isDaemonThread
	 *            --true,线程池的线程都为daemon线程<br>
	 *            --false,线程池的线程都为非daemon线程
	 */
	public TaskThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, boolean isDaemonThread) {
		this(corePoolSize, maximumPoolSize, keepAliveTime,
				TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(),
				new DaemonThreadFactory(isDaemonThread), new AbortPolicy());
	}

}
