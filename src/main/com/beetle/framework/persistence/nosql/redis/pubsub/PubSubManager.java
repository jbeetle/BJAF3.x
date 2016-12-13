package com.beetle.framework.persistence.nosql.redis.pubsub;

import org.slf4j.Logger;

import com.beetle.framework.log.AppLogger;
import com.beetle.framework.persistence.nosql.redis.RedisOperator;
import com.beetle.framework.util.queue.BlockQueue;

import redis.clients.jedis.Jedis;

public class PubSubManager {
	private final RedisOperator rot;
	private static final Logger logger = AppLogger.getLogger(PubSubManager.class);
	private static BlockQueue queue = new BlockQueue();

	public PubSubManager(RedisOperator rot) {
		super();
		this.rot = rot;
		final PubSubManager psm=this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					Object o=queue.pop();
					String topic=(String)o;
					logger.debug("topic:{},resubscribe",topic);
					psm.subscribe(topic);
				}
			}
		}).start();
	}

	/**
	 * 订阅主题，此方法执行后会在后头新建立一个线程等待结果
	 * 
	 * @param topic
	 */
	public void subscribe(final String topic) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Jedis jd = null;
				try {
					jd = rot.getJedisInstanceFromPool();
					jd.subscribe(ClientListener.getInstance(rot.getSubscribeListener()), topic);// 会阻塞
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
}
