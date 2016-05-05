package com.beetle.framework.util.queue;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;

import com.beetle.framework.AppRuntimeException;

/**
 * 缓存队列实现
 * 
 * @author HenryYu
 * 
 */
public final class CacheQueue implements IQueue {

	private IQueue tmpQueue;
	private CacheManager cacheManager;
	private Cache cache;
	private final ConcurrentLinkedQueue<Long> psKeys;
	private final AtomicLong count;
	private final String hcName;
	private final ReentrantLock writeLock = new ReentrantLock();
	private final ReentrantLock readLock = new ReentrantLock();
	private volatile static CacheQueue instance;

	/**
	 * 获取持久队列实例（单例：通过此方法获取，整个JVM只存在一个队列），其数据会保存在“java.io.tmpdir”目录下。
	 * 
	 * @param block
	 *            --是否为阻塞队列，true是阻塞队列,false是非阻塞队列
	 * @param cacheLength
	 *            --内存中队列长度，值>0
	 * @return
	 */
	public static CacheQueue getInstance(final boolean block,
			final int cacheLength) {
		if (instance == null) {
			instance = new CacheQueue(block, cacheLength);
		}
		return instance;
	}

	private CacheQueue(final boolean block, final int cacheLength) {
		this(block, cacheLength, System.getProperty("java.io.tmpdir"));
	}

	/**
	 * 构造函数
	 * 
	 * @param block
	 *            --是否为阻塞队列，true是阻塞队列,false是非阻塞队列
	 * @param cacheLength
	 *            --内存中队列长度，值>0；
	 * @param persistDirPath
	 *            --数据落地目录（<b>注意：一个队列对应一个目录路径，多个队列共享一个目录路径，是不允许的，会出现数据不一致的情况! <
	 *            /b>）
	 */
	public CacheQueue(final boolean block, final int cacheLength,
			final String persistDirPath) {
		if (cacheLength < 0) {
			throw new AppRuntimeException("cacheLength must >0!");
		}
		if (block) {
			this.tmpQueue = new BlockQueue();
		} else {
			this.tmpQueue = new NoBlockConcurrentQueue();
		}
		psKeys = new ConcurrentLinkedQueue<Long>();
		hcName = "cq-" + persistDirPath.hashCode();
		Configuration managerConfig = new Configuration();
		CacheConfiguration mqCf = new CacheConfiguration(hcName, cacheLength);
		mqCf.setEternal(true);
		// mqCf.setDiskStorePath(persistDirPath);
		mqCf.setMaxElementsOnDisk(0);
		mqCf.setTransactionalMode("OFF");
		mqCf.setMemoryStoreEvictionPolicy("LFU");
		// mqCf.setDiskPersistent(true);
		// mqCf.setMaxElementsInMemory(cacheLength);
		mqCf.setMaxEntriesLocalHeap(cacheLength);
		// mqCf.setOverflowToDisk(true);
		mqCf.persistence(new PersistenceConfiguration()
				.strategy(PersistenceConfiguration.Strategy.LOCALTEMPSWAP));
		managerConfig.addCache(mqCf);
		DiskStoreConfiguration dsCf = new DiskStoreConfiguration();
		dsCf.setPath(persistDirPath);
		managerConfig.addDiskStore(dsCf);
		managerConfig.setName(hcName);
		// cacheManager = new CacheManager(managerConfig);
		cacheManager = CacheManager.newInstance(managerConfig);
		cache = cacheManager.getCache(hcName);
		count = new AtomicLong(0);
	}

	public void push(Object obj) {
		try {
			writeLock.lock();
			long key = count.incrementAndGet();
			psKeys.add(key);
			Element e = new Element(key, obj);
			cache.put(e);
		} finally {
			writeLock.unlock();
		}
	}

	public Object pop() {
		if (psKeys.isEmpty()) {
			return tmpQueue.pop();
		}
		try {
			readLock.lock();
			return read();
		} finally {
			readLock.unlock();
		}
	}

	private Object read() {
		Long key = psKeys.poll();
		if (null == key) {
			return null;
		}
		Element e = cache.get(key);
		if (e != null) {
			Object o = e.getObjectValue();
			cache.remove(key);
			return o;
		}
		return null;
	}

	public boolean isEmpty() {
		if (!tmpQueue.isEmpty()) {
			return false;
		}
		if (!psKeys.isEmpty()) {
			return false;
		}
		return true;
	}

	public void clear() {
		cache.flush();
		tmpQueue.clear();
		cache.removeAll();
		cacheManager.clearAll();
	}

	public void flush() {
		cache.flush();
	}

	public void destory() {
		cacheManager.shutdown();
	}

	public int size() {
		return cache.getSize();
	}

}
