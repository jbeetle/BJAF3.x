package com.beetle.framework.persistence.nosql.redis.pubsub;

import org.slf4j.Logger;

import com.beetle.framework.log.AppLogger;

import redis.clients.jedis.JedisPubSub;

public class ClientListener extends JedisPubSub {
	private static final Logger logger = AppLogger.getLogger(ClientListener.class);
	private SubscribeListener listener;

	public ClientListener(String listenerImp) {
		try {
			Object o = Class.forName(listenerImp).newInstance();
			this.listener = (SubscribeListener) o;
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@Override
	public void onMessage(String channel, String message) {
		if (logger.isDebugEnabled()) {
			logger.debug("onMessage,channel:{},message:{}", channel, message);
		}
		listener.onMessage(channel, message);
	}

	@Override
	public void onPMessage(String pattern, String channel, String message) {
		if (logger.isDebugEnabled()) {
			logger.debug("onPMessage,channel:{},message:{}", channel, message);
		}
	}

	@Override
	public void onPSubscribe(String pattern, int subscribedChannels) {
		if (logger.isDebugEnabled()) {
			logger.debug("onPSubscribe,pattern:{},subscribedChannels:{}", pattern, subscribedChannels);
		}
	}

	@Override
	public void onPUnsubscribe(String pattern, int subscribedChannels) {
		if (logger.isDebugEnabled()) {
			logger.debug("onPUnsubscribe,pattern:{},subscribedChannels:{}", pattern, subscribedChannels);
		}
	}

	@Override
	public void onSubscribe(String channel, int subscribedChannels) {
		if (logger.isDebugEnabled()) {
			logger.debug("onSubscribe,channel:{},subscribedChannels:{}", channel, subscribedChannels);
		}
		listener.onSubscribe(channel, subscribedChannels);
	}

	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {
		if (logger.isDebugEnabled()) {
			logger.debug("onUnsubscribe,channel:{},subscribedChannels:{}", channel, subscribedChannels);
		}
		listener.onUnsubscribe(channel, subscribedChannels);
	}

}
