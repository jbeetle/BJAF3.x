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

import com.beetle.framework.util.thread.pubsub.Subscriber.SubWorker;

import java.util.HashMap;
import java.util.Map;

public class Document extends HashMap<Object, Object> {
	private transient SubWorker handler;

	/** 
	 * 构造函数
	 * @param subWorker--指定此文档的订阅消费者
	 */
	public Document(SubWorker subWorker) {
		super();
		this.handler = subWorker;
	}

	SubWorker getHandler(Map<String, SubWorker> docSubWorkerMap) {
		if (handler != null) {
			return handler;
		} else {
			return (SubWorker) docSubWorkerMap.get(getSubWorkerId());
		}
	}

	/**
	 * 构造函数
	 * @param subWorkerId--指定此文档的订阅消费者标识
	 */
	public Document(String subWorkerId) {
		super();
		this.put("subWorkerId_20090521", subWorkerId);
	}

	private String getSubWorkerId() {
		return (String) this.get("subWorkerId_20090521");
	}

	boolean isLegal(Map<String, SubWorker> docSubWorkerMap) {
		if (getSubWorkerId() != null) {
			return true;
		} else {
			if (getHandler(docSubWorkerMap) != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
