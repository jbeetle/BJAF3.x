package com.beetle.framework.persistence.nosql.redis.pubsub;

import org.slf4j.Logger;

import com.beetle.framework.log.AppLogger;
import com.beetle.framework.persistence.nosql.redis.RedisOperator;
import com.beetle.framework.util.queue.BlockQueue;

import redis.clients.jedis.Jedis;

public class PubSubManager {
	private final RedisOperator rot;
	private final ClientListener clientListener;
	private static final Logger logger = AppLogger.getLogger(PubSubManager.class);
	private static BlockQueue queue = new BlockQueue();

	/**
	 * @param rot
	 * @param subscribeListenerImpl,如果此值为空，则会从RedisConfig.xml文件读取<br>
	 * subscribe-listener属性配置
	 */
	public PubSubManager(RedisOperator rot, String subscribeListenerImpl) {
		super();
		this.rot = rot;
		final PubSubManager psm = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					Object o = queue.pop();
					String topic = (String) o;
					logger.debug("topic:{},resubscribe", topic);
					psm.subscribe(topic);
				}
			}
		}).start();
		if (subscribeListenerImpl != null && subscribeListenerImpl.trim().length() > 0) {
			this.clientListener = new ClientListener(subscribeListenerImpl);
		} else {
			this.clientListener = new ClientListener(rot.getSubscribeListener());
		}
	}

	/**
	 * 订阅主题，此方法执行后会在后头新建立一个线程等待结果
	 * 
	 * @param topic
	 */
	public void subscribe(final String topic) {
		final PubSubManager psm = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				Jedis jd = null;
				try {
					jd = rot.getJedisInstanceFromPool();
					jd.subscribe(psm.clientListener, topic);// 会阻塞
				} catch (Exception e) {
					queue.push(topic);
					logger.debug("queue push:{}", topic);
					logger.error(topic, e);
				} finally {
					if (jd != null) {
						jd.close();
					}
					logger.info("subscribe:{},stop", topic);
				}
			}
		}).start();
		logger.info("subscribe:{},OK", topic);
	}

	public void publish(final String topic, String message) {
		Jedis jd = null;
		try {
			jd = rot.getJedisInstanceFromPool();
			jd.publish(topic, message);
		} catch (Exception e) {
			logger.error(topic, e);
		} finally {
			if (jd != null) {
				jd.close();
			}
		}
	}

	public RedisOperator getRedisOperator() {
		return rot;
	}

}
