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

import com.beetle.framework.util.thread.pubsub.Publisher.PubWorker;
import com.beetle.framework.util.thread.pubsub.Subscriber.SubWorker;

import java.util.HashMap;
import java.util.Map;

public class PsEntry {
	private Subscriber sb = null;
	private Map<String, SubWorker> docSubWorkerMap = new HashMap<String, SubWorker>();
	private Publisher pb = null;
	private int publisherThreadPoolSize;

	/**
	 * 构造函数
	 * 
	 * @param threadPoolSize
	 *            --处理线程池大小
	 */
	public PsEntry(int threadPoolSize) {
		if (threadPoolSize < 4) {
			threadPoolSize = 4;
		}
		this.publisherThreadPoolSize = threadPoolSize;
		initializeEvent();
	}

	/**
	 * 初始化事件
	 */
	protected void initializeEvent() {
		int pubsize = publisherThreadPoolSize / 2;
		int subsize = pubsize + 5;
		if (pb == null) {
			pb = Publisher.newInstance(pubsize, docSubWorkerMap);
		}
		if (sb == null) {
			sb = Subscriber.newInstance(subsize, pb.getDocQueue(),
					docSubWorkerMap);
			sb.start();
		}
	}

	/**
	 * 执行并等待结束，此方法会阻塞
	 * 
	 * @throws Exception
	 */
	final public void execute() throws Exception {
		try {
			pb.runAndWaitForDone();
			sb.runAndWaitForDone(pb.getPubWorkerAmount());
		} catch (Exception e) {
			throw e;
		} finally {
			completeEvent();
		}
	}

	/**
	 * 结束事件
	 */
	protected void completeEvent() {
		if (sb != null) {
			sb.stop();
		}
		if (pb != null) {
			pb.clear();
		}
	}

	/**
	 * 加入工作队列，以便并发执行
	 * 
	 * @param pubWorker
	 */
	final public void joinIn(PubWorker pubWorker) {
		pb.addWorker(pubWorker);
	}

	/**
	 * 映射Document的具体处理器
	 * 
	 * @param subWorkerId
	 *            --订阅消费者唯一标识
	 * @param subWorker 
	 *            --订阅消费者
	 */
	final public void mappingr(String subWorkerId, SubWorker subWorker) {
		docSubWorkerMap.put(subWorkerId, subWorker);
	}

}
