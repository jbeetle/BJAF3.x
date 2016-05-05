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
import com.beetle.framework.util.queue.IQueue;
import com.beetle.framework.util.thread.Counter;
import com.beetle.framework.util.thread.RunWrapper;
import com.beetle.framework.util.thread.task.TaskImp;
import com.beetle.framework.util.thread.task.TaskThreadPool;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

public final class Subscriber {
	/**
	 * 文档订阅消费者
	 * 
	 */
	public static interface SubWorker {
		/**
		 * 在此方法中完成文档的处理
		 * 
		 * @param doc
		 * @throws AppRuntimeException
		 */
		void handleDocument(Document doc) throws AppRuntimeException;

	}

	void runAndWaitForDone(long refAmount) {
		while (true) {
			long now = concurrentCounter.getCurrentValue();
			if (now >= refAmount) {
				break;
			} else {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					logger.error("runAndWaitForDone sleep err", e);
				}
			}
		}
	}

	private Dispatcher dp;
	private TaskThreadPool wkpool;
	private static AppLogger logger = AppLogger.getInstance(Subscriber.class);
	private Counter concurrentCounter;
	private Map<String, SubWorker> docSubWorkerMap;

	/**
	 * 获取订阅者实例
	 * 
	 * @param count
	 *            --订阅处理工作线程数量
	 * @return
	 */
	static Subscriber newInstance(int count, IQueue publisherDocqueue,
			Map<String, SubWorker> docSubWorkerMap) {
		return new Subscriber(count, publisherDocqueue, docSubWorkerMap);
	}

	private Subscriber(int count, IQueue publisherDocqueue, Map<String, SubWorker> docSubWorkerMap) {
		this.concurrentCounter = new Counter(0);
		this.docSubWorkerMap = docSubWorkerMap;
		this.wkpool = new TaskThreadPool(count, count, 5 * 1000 * 60, false,
				new CallerRunsPolicy());
		dp = new Dispatcher(this.wkpool, this.concurrentCounter,
				publisherDocqueue, this.docSubWorkerMap);
	}

	/**
	 * 启动订阅
	 */
	void start() {
		dp.startAsDaemon();
	}

	/**
	 * 停止订阅
	 */
	void stop() {
		try {
			this.wkpool.shutdown();
		} finally {
			dp.stop();
		}
	}

	private static class Woker extends TaskImp {

		protected void end() {
			concurrentCounter.increase();
			super.end();
		}

		private Document doc;
		private Counter concurrentCounter;
		private Map<String, SubWorker> docSubWorkerMap;

		public Woker(Document doc, Counter concurrentCounter,
				Map <String, SubWorker>docSubWorkerMap) {
			super();
			this.doc = doc;
			this.concurrentCounter = concurrentCounter;
			this.docSubWorkerMap = docSubWorkerMap;
		}

		protected void routine() throws InterruptedException {
			handleDoc(doc, docSubWorkerMap);
		}

	}

	private static void handleDoc(Document doc, Map<String, SubWorker> docSubWorkerMap) {
		if (doc == null) {
			return;
		}
		try {
			SubWorker sw = doc.getHandler(docSubWorkerMap);
			if (sw != null) {
				sw.handleDocument(doc);
			} else {
				throw new AppRuntimeException(
						"this doc subwork is null,illegal document!");
			}
		} catch (Throwable e) {
			logger.error("handle doc err", e);
		} finally {
			if (doc != null) {
				doc.clear();
			}
		}
	}

	private static class Dispatcher extends RunWrapper {

		private TaskThreadPool wtp;
		private Counter concurrentCounter;
		private IQueue queue;
		private Map<String, SubWorker> docSubWorkerMap;

		public Dispatcher(TaskThreadPool wtp, Counter concurrentCounter,
				IQueue publisherDocqueue, Map<String, SubWorker> docSubWorkerMap) {
			super();
			this.wtp = wtp;
			this.concurrentCounter = concurrentCounter;
			this.queue = publisherDocqueue;
			this.docSubWorkerMap = docSubWorkerMap;
		}

		public void run() {
			// IQueue queue = Publisher.getDocQueue();
			while (true) {
				Document doc = (Document) queue.pop();
				if (!wtp.runInPool(new Woker(doc, concurrentCounter,
						docSubWorkerMap))) {
					handleDoc(doc, docSubWorkerMap);
				}
			}
		}
	}

}
