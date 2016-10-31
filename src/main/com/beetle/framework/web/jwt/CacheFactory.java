package com.beetle.framework.web.jwt;

import com.beetle.framework.AppProperties;
import com.beetle.framework.util.cache.ConcurrentCache;
import com.beetle.framework.util.cache.ICache;

class CacheFactory {
	static ICache createCache() {
		String flag = AppProperties.get("web_openApi_token_cache_type", "jvm");
		if (flag.equalsIgnoreCase("redis")) {
			return new RedisCache();
		}
		return new ConcurrentCache(1024);
	}
}
