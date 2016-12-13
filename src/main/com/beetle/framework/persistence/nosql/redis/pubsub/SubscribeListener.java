package com.beetle.framework.persistence.nosql.redis.pubsub;

public interface SubscribeListener {
	void onMessage(String channel, String message);

	void onSubscribe(String channel, int subscribedChannels);

	void onUnsubscribe(String channel, int subscribedChannels);
}
