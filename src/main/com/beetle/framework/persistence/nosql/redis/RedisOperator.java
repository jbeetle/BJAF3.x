package com.beetle.framework.persistence.nosql.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.util.ObjectUtil;
import com.beetle.framework.util.cache.LifeCycleCache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/*
 * Redist客户操作者<br>
 * 1，透明处理连接管理<br>
 * 2,最常见操作进行封装，简化使用<br>
 * 3,最常见的读写处理进行优化，提高使用性能
 */
public class RedisOperator {
	private String dataSourceName = "default";
	private final static Map<String, JedisPool> dsPool = new HashMap<String, JedisPool>();
	private final static LifeCycleCache localCache = new LifeCycleCache(1000 * 60 * 60);
	private static final Logger logger = AppLogger.getLogger(RedisOperator.class);
	private final JedisPool thePool;
	private final String db;

	public RedisOperator(String dataSourceName) {
		super();
		this.dataSourceName = dataSourceName;
		this.thePool = initPool(this.dataSourceName);
		this.db = RedisConfig.getFrameworkDS(dataSourceName, "database");
	}

	public static void initializeDataSourcesPool() {
		List<String> dbs = RedisConfig.getAllDatasourcename();
		for (String ds : dbs) {
			initPool(ds);
		}
	}

	public static void shutdownDataSourcesPool() {
		List<String> dbs = RedisConfig.getAllDatasourcename();
		for (String ds : dbs) {
			JedisPool pool = dsPool.get(ds);
			if (pool != null)
				pool.destroy();
		}
		logger.info("redis ds pool destory!");
	}

	private static JedisPool initPool(String dataSourceName) {
		JedisPool pool = dsPool.get(dataSourceName);
		if (pool == null) {
			synchronized (logger) {
				pool = dsPool.get(dataSourceName);
				if (pool == null) {
					String server = RedisConfig.getFrameworkDS(dataSourceName, "server-address");
					String port = RedisConfig.getFrameworkDS(dataSourceName, "server-port");
					String db = RedisConfig.getFrameworkDS(dataSourceName, "database");
					String password = RedisConfig.decodeDatasourcePassword(dataSourceName);
					if (server == null || port == null || db == null || password == null) {
						throw new AppRuntimeException("config item set error!");
					}
					JedisPoolConfig config = new JedisPoolConfig();
					String minidle = RedisConfig.getFrameworkDS(dataSourceName, "min-idle");
					if (minidle != null && minidle.trim().length() > 0) {
						config.setMinIdle(Integer.parseInt(minidle));
					}
					String maxidle = RedisConfig.getFrameworkDS(dataSourceName, "max-idle");
					if (maxidle != null && maxidle.trim().length() > 0) {
						config.setMaxIdle(Integer.parseInt(maxidle));
					}
					String maxactive = RedisConfig.getFrameworkDS(dataSourceName, "max-total");
					if (maxactive != null && maxactive.trim().length() > 0) {
						config.setMaxTotal(Integer.parseInt(maxactive));
					}
					String maxwait = RedisConfig.getFrameworkDS(dataSourceName, "max-wait");
					if (maxwait != null && maxwait.trim().length() > 0) {
						config.setMaxWaitMillis(Integer.parseInt(maxwait));
					}
					String testonborrow = RedisConfig.getFrameworkDS(dataSourceName, "test-on-borrow");
					if (testonborrow != null && testonborrow.trim().length() > 0) {
						config.setTestOnBorrow(Boolean.parseBoolean(testonborrow));
						if (!config.getTestOnBorrow()) {
							config.setTestWhileIdle(true);
						}
					}
					pool = new JedisPool(config, server, Integer.parseInt(port), 1000 * 30, password,
							Integer.parseInt(db), "bjaf-jedis-client");
					dsPool.put(dataSourceName, pool);
					logger.info("redis pool[{}] ok!", dataSourceName);
				}
			}
		}
		return pool;
	}

	/**
	 * 使用默认数据源初始化
	 */
	public RedisOperator() {
		super();
		this.thePool = initPool(dataSourceName);
		this.db = RedisConfig.getFrameworkDS(dataSourceName, "database");
	}

	/*
	 * 从连接池中获取一个Jedis实例，<br> 提供此方法主要是为了当常见操作不满足的时候调用最原始的操作<br>
	 * 记住获取Jedis使用完毕后要手动close
	 * 
	 * @throws RedisOperatorException
	 */
	public Jedis getJedisInstanceFromPool() throws RedisOperatorException {
		try {
			return thePool.getResource();
		} catch (Exception e) {
			throw new RedisOperatorException(e);
		}
	}

	/**
	 * 根据key批量删除数据
	 * 
	 * @param keys
	 * @return 如果是true说明只是有1条及其以上数据被删除，如果为false则说明没有数据被删除，可能key不存在
	 * @throws RedisOperatorException
	 */
	public boolean del(String... keys) throws RedisOperatorException {
		Jedis jedis = null;
		try {
			jedis = thePool.getResource();
			long x = jedis.del(keys);
			if (x > 0) {
				return true;
			}
			return false;
		} catch (Exception e) {
			throw new RedisOperatorException(e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public boolean del(String key) throws RedisOperatorException {
		Jedis jedis = null;
		try {
			jedis = thePool.getResource();
			long x = jedis.del(key);
			if (x > 0) {
				return true;
			}
			return false;
		} catch (Exception e) {
			throw new RedisOperatorException(e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public boolean put(String key, List<String> value, int expiredTime) throws RedisOperatorException {
		Jedis jedis = null;
		try {
			jedis = thePool.getResource();
			jedis.del(key);
			String ss[] = new String[value.size()];
			value.toArray(ss);
			long x = jedis.lpush(key, ss);
			if (x > 0) {
				jedis.expire(key, expiredTime);
				return true;
			}
			return false;
		} catch (Exception e) {
			throw new RedisOperatorException(e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public boolean put(String key, List<String> value) throws RedisOperatorException {
		Jedis jedis = null;
		try {
			jedis = thePool.getResource();
			jedis.del(key);
			String ss[] = new String[value.size()];
			value.toArray(ss);
			long x = jedis.lpush(key, ss);
			if (x > 0) {
				return true;
			}
			return false;
		} catch (Exception e) {
			throw new RedisOperatorException(e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public boolean put(String key, Map<String, String> value) throws RedisOperatorException {
		Jedis jedis = null;
		try {
			jedis = thePool.getResource();
			String x = jedis.hmset(key, value);
			if (x.equalsIgnoreCase("ok")) {
				return true;
			}
			return false;
		} catch (Exception e) {
			throw new RedisOperatorException(e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * @param key
	 * @param value
	 * @param expiredTime
	 *            过期时间，单位秒
	 * @return
	 * @throws RedisOperatorException
	 */
	public boolean put(String key, Map<String, String> value, int expiredTime) throws RedisOperatorException {
		Jedis jedis = null;
		try {
			jedis = thePool.getResource();
			String x = jedis.hmset(key, value);
			if (x.equalsIgnoreCase("ok")) {
				jedis.expire(key, expiredTime);
				return true;
			}
			return false;
		} catch (Exception e) {
			throw new RedisOperatorException(e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * put自定义的DTO对象，此对象必须实现序列号接口 java.io.Serializable
	 * 
	 * @param key
	 * @param dto
	 * @return
	 * @throws RedisOperatorException
	 */
	public boolean put(String key, Object dto) throws RedisOperatorException {
		Jedis jedis = null;
		try {
			jedis = thePool.getResource();
			byte bb[] = ObjectUtil.objToBytes(dto);
			String x = jedis.set(key.getBytes(), bb);
			if (x.equalsIgnoreCase("ok")) {
				return true;
			}
			return false;
		} catch (Exception e) {
			throw new RedisOperatorException(e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public boolean put(String key, Object dto, int expiredTime) throws RedisOperatorException {
		Jedis jedis = null;
		try {
			jedis = thePool.getResource();
			byte bb[] = ObjectUtil.objToBytes(dto);
			byte kk[] = key.getBytes();
			String x = jedis.set(kk, bb);
			if (x.equalsIgnoreCase("ok")) {
				jedis.expire(kk, expiredTime);
				return true;
			}
			return false;
		} catch (Exception e) {
			throw new RedisOperatorException(e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public boolean put(String key, String value) throws RedisOperatorException {
		Jedis jedis = null;
		try {
			jedis = thePool.getResource();
			String x = jedis.set(key, value);
			if (x.equalsIgnoreCase("ok")) {
				return true;
			}
			return false;
		} catch (Exception e) {
			throw new RedisOperatorException(e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/*
	 * 如果用了getWithCache方法回在本地内存中缓存值提高性能，有时候我们需要人为的去掉这个缓存值，<br>
	 * 则可采取这个方法
	 */
	public void removeLocalCache(String key) {
		String key_ = key + "-" + db;
		localCache.remove(key_);
	}

	/**
	 * 第一次从服务器获取值回存储在本地内存中，这个值在本地内存中没有过期之前都会从本地内存中获取<br>
	 * 以提高性能，减少服务器调用；在本地过期后，又重新从服务器端获取，以此类推
	 * 
	 * @param key
	 * @param valueType
	 *            值对应类型的类
	 * @param cacheExpiredTime
	 *            本地缓存过期时间，单位：秒，如：30秒，即从当前时间算30秒后过期
	 * @return
	 * @throws RedisOperatorException
	 */
	@SuppressWarnings("unchecked")
	public <T> T getWithCache(String key, Class<T> valueType, int cacheExpiredTime) throws RedisOperatorException {
		String key_ = key + "-" + db;
		Object obj = localCache.get(key_);
		if (obj == null) {
			String typeStr = valueType.toString();
			if (typeStr.equals(String.class.toString())) {
				String v = this.getAsString(key);
				if (v == null)
					return null;
				localCache.put(key_, v, cacheExpiredTime * 1000);
				return (T) v;
			} else if (typeStr.equals(Map.class.toString())) {
				Map<String, String> v = this.getAsMap(key);
				if (v == null)
					return null;
				localCache.put(key_, v, cacheExpiredTime * 1000);
				return (T) v;
			} else if (typeStr.equals(List.class.toString())) {
				List<String> v = this.getAsList(key);
				if (v == null)
					return null;
				localCache.put(key_, v, cacheExpiredTime * 1000);
				return (T) v;
			} else {
				Object v = this.getAsDTO(key);
				if (v == null)
					return null;
				localCache.put(key_, v, cacheExpiredTime * 1000);
				return (T) v;
			}
		} else {
			return (T) obj;
		}
	}

	public Object getAsDTO(String key) throws RedisOperatorException {
		Jedis jedis = null;
		try {
			jedis = thePool.getResource();
			byte bb[] = jedis.get(key.getBytes());
			if (bb == null)
				return null;
			return ObjectUtil.bytesToObj(bb);
		} catch (Exception e) {
			throw new RedisOperatorException(e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public List<String> getAsList(String key) throws RedisOperatorException {
		Jedis jedis = null;
		try {
			jedis = thePool.getResource();
			return jedis.lrange(key, 0, -1);
		} catch (Exception e) {
			throw new RedisOperatorException(e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Map<String, String> getAsMap(String key) throws RedisOperatorException {
		Jedis jedis = null;
		try {
			jedis = thePool.getResource();
			return jedis.hgetAll(key);
		} catch (Exception e) {
			throw new RedisOperatorException(e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public String getAsString(String key) throws RedisOperatorException {
		Jedis jedis = null;
		try {
			jedis = thePool.getResource();
			return jedis.get(key);
		} catch (Exception e) {
			throw new RedisOperatorException(e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * put一个数据到服务器，如果key已存在则覆盖
	 * 
	 * @param key
	 * @param value
	 * @param expiredTime
	 *            过期时间，单位秒
	 * @return
	 * @throws RedisOperatorException
	 */
	public boolean put(String key, String value, int expiredTime) throws RedisOperatorException {
		Jedis jedis = null;
		try {
			jedis = thePool.getResource();
			String x = jedis.setex(key, expiredTime, value);
			if (x.equalsIgnoreCase("ok")) {
				return true;
			}
			return false;
		} catch (Exception e) {
			throw new RedisOperatorException(e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 检查数据是否存在
	 * 
	 * @param key
	 * @return
	 * @throws RedisOperatorException
	 */
	public Boolean exists(String key) throws RedisOperatorException {
		Jedis jedis = null;
		try {
			jedis = thePool.getResource();
			return jedis.exists(key);
		} catch (Exception e) {
			throw new RedisOperatorException(e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
}
