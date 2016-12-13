package com.beetle.framework.persistence.nosql.redis.pubsub;

/*
 * 一个redis数据库（index）只能配一个SubscribeListener
 */
public interface SubscribeListener {
	void onMessage(String channel, String message);

	void onSubscribe(String channel, int subscribedChannels);

	void onUnsubscribe(String channel, int subscribedChannels);
}
