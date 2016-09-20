package com.beetle.component.security.session;

import java.io.Serializable;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.slf4j.Logger;

import com.beetle.framework.AppProperties;
import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.persistence.nosql.redis.RedisOperator;

public class RedisSessionDao extends EnterpriseCacheSessionDAO {
	private String ds;
	private static final Logger logger = AppLogger.getLogger(RedisSessionDao.class);

	public RedisSessionDao() {
		super();
		this.ds = AppProperties.get("security_session_redis_datasource");
		if (ds == null || ds.trim().length() == 0) {
			String msg = "redis datasourece not setted![security_session_redis_datasource]";
			logger.error(msg);
			throw new AppRuntimeException(msg);
		}
	}

	@Override
	protected Serializable doCreate(Session session) {
		Serializable sessionId = super.doCreate(session);
		RedisOperator ro = new RedisOperator(ds);
		boolean f = ro.put(sessionId.toString(), session);
		if (logger.isDebugEnabled()) {
			logger.debug("sessionid:{},created[{}].", sessionId, f);
		}
		return sessionId;
	}

	@Override
	protected void doDelete(Session session) {
		super.doDelete(session);
		RedisOperator ro = new RedisOperator(ds);
		boolean f = ro.del(session.getId().toString());
		if (logger.isDebugEnabled()) {
			logger.debug("sessionid:{},del[{}]", session.getId(), f);
		}
	}

	@Override
	protected Session doReadSession(Serializable sessionId) {
		Session session = super.doReadSession(sessionId);
		if (session == null) {
			RedisOperator ro = new RedisOperator(ds);
			Object o = ro.getAsDTO(sessionId.toString());
			if (o != null) {
				session = (Session) o;
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("session:{},read", session);
		}
		return session;
	}

	@Override
	protected void doUpdate(Session session) {
		super.doUpdate(session);
		RedisOperator ro = new RedisOperator(ds);
		boolean f = ro.put(session.getId().toString(), session);
		if (logger.isDebugEnabled()) {
			logger.debug("sessionid:{},update[{}]", session.getId(), f);
		}
	}

}
