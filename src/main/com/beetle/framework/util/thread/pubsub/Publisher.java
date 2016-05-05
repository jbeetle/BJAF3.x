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
package com.beetle.framework.util.thread.pubsub;

import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.util.queue.BlockQueue;
import com.beetle.framework.util.queue.IQueue;
import com.beetle.framework.util.thread.pubsub.Subscriber.SubWorker;
import com.beetle.framework.util.thread.task.TaskExecutor;
import com.beetle.framework.util.thread.task.TaskImp;
import com.beetle.framework.util.thread.task.TaskThreadPool;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

public final class Publisher {
	private IQueue docQueue;
	private static AppLogger logger = AppLogger.getInstance(Publisher.class);
	private TaskExecutor re;
	private long pubWorkerAmount;
	private Map<String, SubWorker>  docSubWorkerMap;

	static Publisher newInstance(int count, Map <String, SubWorker>  docSubWorkerMap) {
		return new Publisher(count, docSubWorkerMap);
	}

	private Publisher(int count, Map <String, SubWorker>  docSubWorkerMap) {
		this.pubWorkerAmount = 0;
		this.docQueue = new BlockQueue();
		this.docSubWorkerMap = docSubWorkerMap;
		this.re = new TaskExecutor(new TaskThreadPool(count, count,
				5 * 1000 * 60, false, new CallerRunsPolicy()));
	}

	long getPubWorkerAmount() {
		return pubWorkerAmount;
	}

	void runAndWaitForDone() {
		re.runRoutineParalleJoin();
	}

	void clear() {
		re.shutdownPool();
	}

	void addWorker(PubWorker worker) {
		this.pubWorkerAmount++;
		re.addSubRoutine(worker.setParams(this.docQueue, this.docSubWorkerMap));
	}

	/**
	 * 文档发布者
	 * 
	 */
	public abstract static class PubWorker extends TaskImp {
		PubWorker setParams(IQueue docQueue, Map<String, SubWorker>  docSubWorkerMap) {
			this.docQueue = docQueue;
			this.docSubWorkerMap = docSubWorkerMap;
			return this;
		}

		private Map <String, SubWorker>  docSubWorkerMap;
		private IQueue docQueue;

		public PubWorker() {
			super();
		}

		/**
		 * 制造一个Document，以便消费者消费
		 * 
		 * @return
		 * @throws AppRuntimeException
		 */
		protected abstract Document produceDocument()
				throws AppRuntimeException;

		protected void routine() throws InterruptedException {
			Document doc = null;
			try {
				doc = produceDocument();
				if (doc.isLegal(docSubWorkerMap)) {
					this.docQueue.push(doc);
				} else {
					doc.clear();
					logger.warn("this doc is illegal,Document is cleared");
					throw new InterruptedException(
							"this doc is illegal,Document is cleared");
				}
			} catch (Exception e) {
				logger.error(e);
				if (doc != null) {
					doc.clear();
				}
				logger.warn("produce mothod err,Document is cleared");
				throw new InterruptedException(e.getMessage());
			}
		}

	}

	IQueue getDocQueue() {
		return docQueue;
	}

}
