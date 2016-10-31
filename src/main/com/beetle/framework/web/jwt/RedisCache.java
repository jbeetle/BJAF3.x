package com.beetle.framework.web.jwt;

import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;

import com.beetle.framework.AppProperties;
import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.persistence.nosql.redis.RedisOperator;
import com.beetle.framework.util.cache.ICache;
import com.beetle.framework.util.queue.BlockQueue;
import com.beetle.framework.web.jwt.OpenApiProxy.UCDTO;

/**
 * 针对jwt的redis缓存做一点的优化，例如频繁获取从内存获取 put操作用异步处理等
 *
 */
class RedisCache implements ICache {
	private static final Logger logger = AppLogger.getLogger(Logger.class);
	private final RedisOperator ro;
	private final BlockQueue queue;
	private final int time;

	private static class KV {
		private String key;
		private Object value;
		private int optType;

		public KV(String key, Object value, int optType) {
			super();
			this.key = key;
			this.value = value;
			this.optType = optType;
		}

		public String getKey() {
			return key;
		}

		public Object getValue() {
			return value;
		}

		public int getOptType() {
			return optType;
		}

	}

	public RedisCache() {
		super();
		String ds = AppProperties.get("web_openApi_redis_datasource");
		if (ds == null || ds.trim().length() == 0) {
			throw new AppRuntimeException(
					"must set redist datasource[web_openApi_redis_datasource]in application.properties file");
		}
		this.ro = new RedisOperator(ds);
		this.queue = new BlockQueue();
		this.time = AppProperties.getAsInt("web_openApi_token_expire", 60);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					KV kv = (KV) queue.pop();
					try {
						if (kv.getOptType() == 1) {
							ro.put(kv.getKey(), kv.getValue());
							logger.debug("key:{},put redis", kv.getKey());
						} else if (kv.getOptType() == 2) {
							ro.del(kv.getKey());
							logger.debug("key:{},del redis", kv.getKey());
						}
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
				}
			}
		});
		t.setDaemon(true);
		t.start();
	}

	@Override
	public Object get(Object key) {
		// return ro.getAsDTO(key.toString());
		try {
			UCDTO uto = ro.getWithCache(key.toString(), UCDTO.class, this.time);
			return uto;
		} catch (Exception e) {
			logger.error(key.toString(), e);
			return null;
		}
	}

	@Override
	public void put(Object key, Object value) {
		// ro.put(key.toString(), value);
		KV kv = new KV(key.toString(), value, 1);
		this.queue.push(kv);
	}

	@Override
	public void clear() {
		try {
			ro.getJedisInstanceFromPool().flushDB();
		} catch (Exception e) {
			logger.error("clear", e);
		}
	}

	@Override
	public boolean containsKey(Object key) {
		return ro.exists(key.toString());
	}

	@Override
	public boolean isEmpty() {
		long i = ro.getJedisInstanceFromPool().dbSize();
		if (i > 0) {
			return true;
		}
		return false;
	}

	@Override
	public Set<?> keySet() {
		return ro.getJedisInstanceFromPool().keys("*");
	}

	@Override
	public Set<?> entrySet() {
		throw new AppRuntimeException("not supported yet");
	}

	@Override
	public void remove(Object key) {
		KV kv = new KV(key.toString(), null, 2);
		this.queue.push(kv);
		// ro.del(key.toString());
	}

	@Override
	public int size() {
		Long i = ro.getJedisInstanceFromPool().dbSize();
		return i.intValue();
	}

	@Override
	public Collection<?> values() {
		throw new AppRuntimeException("not supported yet");
	}

}
