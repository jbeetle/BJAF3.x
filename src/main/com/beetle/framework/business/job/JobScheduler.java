package com.beetle.framework.business.job;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.beetle.framework.AppProperties;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.util.thread.ThreadImp;

/**
 * 
 * Job的调度器
 * 
 */
public final class JobScheduler {
	private static final Map<String, JobStatusInfo> statusCache = new ConcurrentHashMap<String, JobStatusInfo>();
	private static final AppLogger logger = AppLogger
			.getInstance(JobScheduler.class);

	public static JobScheduler getInstance() {
		return instance;
	}

	private JobScheduler() {

	}

	private static JobScheduler instance = new JobScheduler();
	private Scheduler scheduler;

	/**
	 * 
	 * job状态信息对象
	 * 
	 */
	public static class JobStatusInfo extends JobDef {
		public enum Status {
			Stop(0), Running(1), Cancel(2);
			public int getValue() {
				return value;
			}

			private int value;

			private Status(int value) {
				this.value = value;
			}
		};

		private Status status;//
		private int counter;// 记录已运行次数
		private long nextRunTime;
		private long lastStopTime;

		public int getCounter() {
			return counter;
		}

		public JobStatusInfo() {
			super();
			this.status = Status.Stop;
			this.counter = 0;
			this.nextRunTime = 0;
		}

		public void addOne() {
			counter++;
		}

		public Status getStatus() {
			return status;
		}

		public void setStatus(Status status) {
			this.status = status;
		}

		public long getNextRunTime() {
			return nextRunTime;
		}

		public void setNextRunTime(long nextRunTime) {
			this.nextRunTime = nextRunTime;
		}

		public long getLastStopTime() {
			return lastStopTime;
		}

		public void setLastStopTime(long lastStopTime) {
			this.lastStopTime = lastStopTime;
		}

		@Override
		public String toString() {
			return "JobStatusInfo [status=" + status + ", counter=" + counter
					+ ", nextRunTime=" + nextRunTime + ", lastStopTime="
					+ lastStopTime + "]" + super.toString();
		}

	}

	/**
	 * 获取所有当前Job的运行状态信息
	 * 
	 * @return
	 */
	public Map<String, JobStatusInfo> getJobStatusInfo() {
		return statusCache;
	}

	private static class RunJobThread extends ThreadImp {
		@Override
		protected void end() {
			JobStatusInfo jsi = statusCache.get(jobId);
			jsi.setStatus(JobStatusInfo.Status.Stop);
			jsi.addOne();
			jsi.setLastStopTime(System.currentTimeMillis());
			CronExpression ce;
			try {
				ce = new CronExpression(jsi.getCron());
				Date nt = ce.getNextValidTimeAfter(
						new Date(System.currentTimeMillis()), 0);
				if (nt == null) {
					jsi.setStatus(JobStatusInfo.Status.Cancel);
					jsi.setNextRunTime(-1);
					logger.info(
							"This job[{}] can't calculate the next execution time, the system automatically cancelled",
							jsi.getId());
				} else {
					jsi.setNextRunTime(nt.getTime());
				}
			} catch (Exception e) {
				logger.error("CronExpression err", e);
			} finally {
				this.cancelMonitored();
				super.end();
			}
		}

		private final String jobImp;
		private final String jobId;

		public RunJobThread(String jobId, String jobImp) {
			super("BJAF_job_[" + jobId + "]");
			this.setCycleMode(ThreadImp.CycleMode.MANUAL);
			this.jobImp = jobImp;
			this.jobId = jobId;
		}

		public RunJobThread(String jobId, String jobImp, long timeout) {
			super("BJAF_job_[" + jobId + "]", timeout, 1, TimeOutMode.BreakOff);
			this.setCycleMode(ThreadImp.CycleMode.MANUAL);
			this.jobImp = jobImp;
			this.jobId = jobId;
		}

		@Override
		protected void routine() throws Throwable {
			JobStatusInfo jsi = statusCache.get(jobId);
			jsi.setStatus(JobStatusInfo.Status.Running);
			JobImp job = (JobImp) Class.forName(jobImp.trim()).newInstance();
			job.run();
		}

	}

	private static class Scheduler extends ThreadImp {

		public Scheduler(String threadName, long timeout, long interval,
				TimeOutMode timeOutMode) {
			super(threadName, timeout, interval, timeOutMode);
		}

		@Override
		protected void routine() throws Throwable {
			List<JobDef> jobs = JobConfig.getJobs();
			try {
				for (JobDef job : jobs) {
					if (statusCache.containsKey(job.getId())) {
						JobStatusInfo js = statusCache.get(job.getId());
						js.setEnabled(job.isEnabled());
						js.setCron(job.getCron());
						js.setImplement(job.getImplement());
						js.setTimeout(job.getTimeout());
						js.setStarttime(job.getStarttime());
						// logger.debug("view JobStatusInfo:{}", js);
						if (js.getStatus().equals(JobStatusInfo.Status.Stop)) {
							startJob(js);
						} else if (js.getStatus().equals(
								JobStatusInfo.Status.Running)) {
							// 运行状态，做啥处理？
						} else if (js.getStatus().equals(
								JobStatusInfo.Status.Cancel)) {
							// 取消状态，做啥处理？
						} else {
							logger.warn("Can't handle this state:{}",
									js.getStatus());
						}
					} else {
						if (job.isEnabled()) {
							initJob(job);
						}
					}
				}
			} finally {
				jobs.clear();
			}
		}

		private void startJob(JobStatusInfo js) {
			long now = System.currentTimeMillis();
			if (now >= js.getStarttime() && now >= js.getNextRunTime()
					&& js.getNextRunTime() > 0 && js.isEnabled()) {
				if (js.getTimeout() > 0) {
					new RunJobThread(js.getId(), js.getImplement(),
							js.getTimeout()).startAsDaemon();
				} else {
					new RunJobThread(js.getId(), js.getImplement())
							.startAsDaemon();
				}
				logger.debug("startJob:{}", js);
			}
		}

		private void initJob(JobDef job) throws ParseException {
			JobStatusInfo js = new JobStatusInfo();
			js.setCron(job.getCron());
			js.setEnabled(job.isEnabled());
			js.setId(job.getId());
			js.setImplement(job.getImplement());
			js.setStarttime(job.getStarttime());
			js.setTimeout(job.getTimeout());
			CronExpression ce = new CronExpression(job.getCron());
			Date nt = ce.getNextValidTimeAfter(
					new Date(System.currentTimeMillis()), 0);
			if (nt == null) {// 时间表达式算不出下次执行的时间，过期了
				js.setNextRunTime(-1);
				js.setStatus(JobStatusInfo.Status.Cancel);
			} else {
				js.setStatus(JobStatusInfo.Status.Stop);
				js.setNextRunTime(nt.getTime());
			}
			statusCache.put(js.getId(), js);
		}
	}

	public void start() {
		if (this.scheduler == null) {
			scheduler = new Scheduler("BJAF_JobScheduler", 1000 * 60,
					AppProperties.getAsInt("job_Scheduler_interval", 250),
					ThreadImp.TimeOutMode.BreakOffAndStartAgain);
			scheduler.start();
			logger.info("JobScheduler started");
		}
	}

	public void stop() {
		statusCache.clear();
		if (this.scheduler != null) {
			this.scheduler.stop();
		}
		logger.info("JobScheduler stopped!");
	}
}
