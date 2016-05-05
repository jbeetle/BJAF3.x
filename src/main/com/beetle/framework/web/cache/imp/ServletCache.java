package com.beetle.framework.web.cache.imp;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;

public class ServletCache {
	private int type;
	private CacheManager cacheManager;
	private Cache cache;
	private static ServletCache appCache;
	private static ServletCache sessionCache;

	private ServletCache(String name, int type) {
		this.type = type;
		Configuration managerConfig = new Configuration();
		CacheConfiguration mqCf = new CacheConfiguration(name, CacheConfig
				.getConfig().getMaxElementsInMemory());
		mqCf.setEternal(true);
		DiskStoreConfiguration dsCf = new DiskStoreConfiguration();
		dsCf.setPath(CacheConfig.getConfig().getDiskStorePath());
		managerConfig.addDiskStore(dsCf);
		mqCf.setMaxElementsOnDisk(0);
		mqCf.setMaxEntriesLocalHeap(CacheConfig.getConfig()
				.getMaxElementsInMemory());
		mqCf.persistence(new PersistenceConfiguration()
				.strategy(PersistenceConfiguration.Strategy.LOCALTEMPSWAP));
		mqCf.setTransactionalMode("OFF");
		mqCf.setMemoryStoreEvictionPolicy(CacheConfig.getConfig()
				.getMemoryStoreEvictionPolicy());
		managerConfig.addCache(mqCf);
		cacheManager = new CacheManager(managerConfig);
		cache = cacheManager.getCache(name);
	}

	public synchronized static ServletCache getSessionServletCache(String name,
			int type) {
		if (sessionCache == null) {
			sessionCache = new ServletCache(name, type);
		}
		return sessionCache;
	}

	public synchronized static ServletCache getAppServletCache(String name,
			int type) {
		if (appCache == null) {
			appCache = new ServletCache(name, type);
		}
		return appCache;
	}

	public int getType() {
		return type;
	}

	public void shutdown() {
		cacheManager.shutdown();
	}

	public Object getFromCache(String key, int time)
			throws NeedsRefreshException {
		Element e = cache.get(key);
		if (null == e) {
			throw new NeedsRefreshException("not found!");
		}
		return e.getObjectValue();
	}

	public void putInCache(String key, Object obj, int timeToLiveSec) {
		Element e = new Element(key, obj);
		e.setTimeToLive(timeToLiveSec);
		cache.put(e);
	}

	public void cancelUpdate(String key) {
		cache.remove(key);
	}

}
