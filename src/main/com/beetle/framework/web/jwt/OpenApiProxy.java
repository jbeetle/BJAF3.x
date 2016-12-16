package com.beetle.framework.web.jwt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import com.beetle.framework.AppProperties;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.util.ObjectUtil;
import com.beetle.framework.util.cache.ICache;
import com.beetle.framework.util.encrypt.Coder;
import com.beetle.framework.util.thread.ThreadImp;
import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.UploadController;
import com.beetle.framework.web.controller.WebInput;
import com.beetle.framework.web.view.ModelData;
import com.beetle.framework.web.view.View;

/**
 * 采取JWT模式代替Session的API的验证模式<br>
 * 1，客户首先调用login登陆进行用户身份验证，通过了返回一个唯一的token<br>
 * 2，后续每次请求都要http的header属性[X-JWT-Token]设置此token，相当于每次都要提交此Token到服务器校验<br>
 * 3,调用logout注销，销毁token<br>
 * 同时，OpenApi类也是服务代理，支持@HttpService注解，在接口上标记即可直接暴露为WebService<br>
 * 本代理支持服务参数的Java的基本类型，如果非基本类型的话，可重载fillParamters自己填充<br>
 * 另外，为了方便编程和安全性，如果服务要使用到登录的userid的值，如果服务命名以[ByCurrentUserId]结尾，在第一个参数注入userid值
 * <br>
 * 注：本代理拒绝了http的get方法，只能采取post方法 功能：<br>
 * 1,支持业务服务层的Service直接暴露为web服务，直接省去一个个编写控制器<br>
 * 服务需要声明HttpService注解<br>
 * 2,如果添加请求处理动作，直接添加一个动作就可以，参考webservice控制器
 * 
 * @author yuhaodong@gmail.com
 *
 */
public abstract class OpenApiProxy extends WebRPCService {
	private static final Logger logger = AppLogger.getLogger(OpenApiProxy.class);
	private static final String web_openApi_token_expire_enabled = "web_openApi_token_expire_enabled";
	private static final long localCacheTime = AppProperties.getAsInt("web_openApi_redis_local_cache", 5) * 1000;
	private static final boolean clientSrcCheckFlag = AppProperties.getAsBoolean("web_openApi_token_client_enabled",
			false);

	public static class UCDTO implements java.io.Serializable {
		private static final long serialVersionUID = 1L;
		private Claims claims;
		private String token;
		private long timeOut;
		private long updateTime;

		public Claims getClaims() {
			return claims;
		}

		public String getToken() {
			return token;
		}

		public UCDTO(Claims claims, String token, long timeOut) {
			super();
			this.claims = claims;
			this.token = token;
			this.timeOut = timeOut;
		}

		public void setUpdateTime(long updateTime) {
			this.updateTime = updateTime;
		}

		public long getUpdateTime() {
			return updateTime;
		}

		public boolean checkExipred() {
			boolean cf = AppProperties.getAsBoolean(web_openApi_token_expire_enabled, true);
			if (!cf) {
				return false;// 永远不过期
			}
			long x = System.currentTimeMillis() - updateTime;
			if (x >= this.timeOut) {
				return true;
			}
			return false;
		}
	}

	// <String, UCDTO>
	private static final ICache tokenCache = CacheFactory.createCache();

	private static final class Monitor extends ThreadImp {

		public Monitor(String threadName, long interval) {
			super(threadName, interval);
		}

		@Override
		protected void routine() throws Throwable {
			List<String> keyList = new ArrayList<String>();
			// Iterator<UCDTO> it = tokenCache.values().iterator();
			// while (it.hasNext()) {
			// UCDTO dto = it.next();
			// if (dto.checkExipred()) {
			// keyList.add(dto.getClaims().getIss());
			// }
			// }
			Set<?> keys = tokenCache.keySet();
			Iterator<?> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next().toString();
				try {
					UCDTO dto = (UCDTO) tokenCache.get(key);
					if (dto.checkExipred()) {
						keyList.add(dto.getClaims().getIss());
					}
				} catch (Exception e) {
					// 处理库有可能出现的其缓存对象的问题
				}
			}
			for (String key : keyList) {
				tokenCache.remove(key);
				logger.info("remove Exipred token:{}", key);
			}
		}

	}

	private static int monitorFlag = 0;

	private static void start() {
		boolean cf = AppProperties.getAsBoolean(web_openApi_token_expire_enabled, true);
		if (!cf) {
			return;
		}
		if (monitorFlag == 0) {
			synchronized (tokenCache) {
				if (monitorFlag == 0) {
					monitorFlag = 1;
					Monitor m = new Monitor("OpenApi-Monitor",
							AppProperties.getAsInt("web_openApi_token_check", 30 * 1000));
					m.startAsDaemon();
					logger.info("OpenApi-Monitor started!");
				}
			}
		}
	}

	public OpenApiProxy() {
		super();
		this.disableGetMethod();
		start();
	}

	private static String[] parseStr(String token) throws ControllerException {
		try {
			String[] xx = new String[2];
			int i = token.lastIndexOf('.');
			String y = token.substring(0, i);
			String z = token.substring(i + 1);
			xx[0] = y;
			xx[1] = z;
			return xx;
		} catch (Exception e) {
			throw new ControllerException(401, "token can not verify through");
		}
	}

	/**
	 * 获取客户端浏览器的用户Agent信息，此信息会写入Token，作为识别客户端身份的一个因素，参与校验<br>
	 * 如果不希望校验或更好的条件可重载此方法返回一个固定的值
	 * 
	 * @param wi
	 * @return
	 */
	protected String getUserAgent(WebInput wi) {
		String userAgent = wi.getHeader("User-Agent");
		if (userAgent == null || userAgent.trim().length() == 0) {
			userAgent = wi.getHeader("user-agent");
		}
		return userAgent;
	}

	/**
	 * 本方法会尝试识别出一个唯一的客户端标示，但不保证百分百唯一<br>
	 * 此信息会写入Token，作为识别客户端身份的一个因素，参与校验<br>
	 * 如果不希望校验或更好的条件可重载此方法返回一个固定的值或按照自己的逻辑生成一个识别<br>
	 * 注，本方法从这个变量获取值并串起来作为唯一标示<br>
	 * req.getHeader("X-Forwarded-For");<br>
	 * req.getHeader("x-real-ip");<br>
	 * req.getRemoteAddr();<br>
	 * req.getHeader("X-Client-Id");<br>
	 * 
	 * @param req
	 * @return
	 */
	protected String getClientId(WebInput req) {
		if (!clientSrcCheckFlag) {
			return "127.0.0.1";
		}
		String ip1 = req.getHeader("X-Forwarded-For");
		String ip2 = req.getHeader("x-real-ip");
		String ip3 = req.getRemoteAddr();
		String ip4 = req.getHeader("X-Client-Id");
		StringBuilder sb = new StringBuilder();
		if (ip1 == null) {
			ip1 = "";
		}
		if (ip2 == null) {
			ip2 = "";
		}
		if (ip3 == null) {
			ip3 = "";
		}
		if (ip4 == null) {
			ip4 = "";
		}
		sb.append(ip1);
		sb.append(ip2);
		sb.append(ip3);
		sb.append(ip4);
		if (sb.toString().length() <= 1) {
			return null;
		}
		return Coder.md5(sb.toString());
	}

	/**
	 * 检验用户名和密码
	 * 
	 * @param userName
	 * @param password
	 * @return true成功，false失败
	 */
	protected abstract boolean verifyUser(String userName, String password);

	/**
	 * 获取用户唯一标识
	 * 
	 * @param userName
	 * @return
	 */
	protected abstract String getUserId(String userName);

	/**
	 * 用户身份验证<br>
	 * 
	 * 请求数据必须包括以下输入参数：<br>
	 * 用户名变量$username<br>
	 * 密码变量$password<br>
	 * 客户类型$clientType<br>
	 * 登录成功返回一个token和一个状态码code，成功为1001，失败为非1001
	 * 
	 * @param wi
	 * @return {"token":"xxxxxxx","serverTime":7758885,"code":1001,"msg":"OK"}
	 * @throws ControllerException
	 */
	public ModelData login(WebInput wi) throws ControllerException {
		String clientId = getClientId(wi);
		if (clientId == null) {
			throw new ControllerException(406, "Unable to identify the source of the request");
		}
		String clientType = wi.getParameter("$clientType");
		if (clientType == null || clientType.trim().length() == 0) {
			throw new ControllerException(406, "Unable to identify the source of the request");
		}
		if (!clientType.equalsIgnoreCase(Claims.ClientType.BROWSER.toString())
				&& !clientType.equalsIgnoreCase(Claims.ClientType.APP.toString())) {
			throw new ControllerException(406, "Unable to identify the source of the request");
		}
		String userAgent = getUserAgent(wi);
		if (clientType.equalsIgnoreCase(Claims.ClientType.BROWSER.toString())) {
			if (userAgent == null || userAgent.trim().length() == 0) {
				throw new ControllerException(406, "Unable to identify the source of the request");
			}
		}
		String username = wi.getParameter("$username");
		String password = wi.decryptFieldValueByRsaPrivateKey("$password");
		if (password == null || password.trim().length() == 0) {
			throw new ControllerException(401, "Users can not verify through");
		}
		boolean f = verifyUser(username, password);
		if (!f) {
			throw new ControllerException(401, "Users can not verify through");
		}
		logger.info("user[{}]verify OK", username);
		String uid = this.getUserId(username);
		Claims claims = new Claims();
		if (userAgent != null && userAgent.length() > 0) {
			claims.setUserAgent(Coder.md5(userAgent));
		}
		ModelData md = new ModelData();
		claims.setClientId(clientId);
		claims.setClientType(clientType);
		long timeout = AppProperties.getAsInt("web_openApi_token_expire", 60) * 1000;
		// long exp = System.currentTimeMillis() + timeout;
		// claims.setExp(exp);
		claims.setTime(System.currentTimeMillis());
		claims.setIss(uid);
		String claimsJson = ObjectUtil.objectToJsonWithJackson(claims);
		if (claimsJson == null) {
			md.put("token", "");
			md.put("code", -1000);
			md.put("msg", "err");
			logger.info("user[{}]login err", username);
			return md.asJSON();
		}
		logger.debug("claimsJson:{}", claimsJson);
		String claimsCiphertext = wi.encryptByRsaPublicKey(claimsJson);
		String claimsCiphertextMd5 = Coder.md5(claimsCiphertext);
		String tokenStr = claimsCiphertext + "." + claimsCiphertextMd5;
		// logger.debug("claimsJson:{},claimsCiphertext:{},claimsCiphertextMd5:{}",
		// claimsJson, claimsCiphertext,
		// claimsCiphertextMd5);
		//
		UCDTO udto = new UCDTO(claims, tokenStr, timeout);
		udto.setUpdateTime(claims.getTime());
		tokenCache.put(uid, udto);
		//
		md.put("token", tokenStr);
		md.put("serverTime", claims.getTime());
		md.put("code", 1001);
		md.put("msg", "OK");
		logger.info("user login OK[{},{}]", username, tokenStr);
		return md.asJSON();
	}

	/**
	 * 注销登录，销毁token token必须通过header属性[X-JWT-Token"]返回
	 * 
	 * @param wi
	 * @return {"code":1001,"msg":"OK"}
	 * @throws ControllerException
	 */
	public ModelData logout(WebInput wi) throws ControllerException {
		try {
			String uid = verify(wi);
			tokenCache.remove(uid);
			logger.info("logout[{}] remove ok", uid);
		} catch (ControllerException e) {
			// 已经过期了再调logout校验不过会抛异常
			logger.warn("logout err:code:{},msg:{}", e.getErrCode(), e.getMessage());
		}
		ModelData md = new ModelData();
		md.put("code", 1001);
		md.put("msg", "OK");
		return md.asJSON();
	}

	/**
	 * 上传文件接口，此接口需要登录验证（登录验证后才能使用）<br>
	 * 文件上传接口IUpload实现类必须在页面通过参数“$upload”注册
	 * 
	 * @param wi
	 * @return
	 * @throws ControllerException
	 */
	public ModelData upload(WebInput wi) throws ControllerException {
		String uid = verify(wi);
		wi.bindJwtTokenLoginUserIdInRequest(uid);
		logger.debug("uid:{}", uid);
		logger.debug("$upload:{}", wi.getParameter("$upload"));
		UploadController udc = new UploadController();
		View view = udc.perform(wi);
		return view.getMd().asJSON();
	}

	@Override
	protected String verify(WebInput wi) throws ControllerException {
		return verifyToken_(wi);
	}

	private String verifyToken_(WebInput wi) throws ControllerException {
		String token = wi.getHeader("X-JWT-Token");
		if (token == null || token.trim().length() == 0) {
			throw new ControllerException(401, "token can not verify through");
		}
		logger.debug("header[X-JWT-Token]:{}", token);
		String[] km = parseStr(token);
		String claimsCiphertext = km[0];
		String claimsCiphertextMd5 = km[1];
		String claimsCiphertextMd5_ = Coder.md5(claimsCiphertext);
		if (!claimsCiphertextMd5_.equals(claimsCiphertextMd5)) {
			throw new ControllerException(401, "token can not verify through");
		}
		String claimsJson = wi.decryptByRsaPrivateKey(claimsCiphertext);
		if (claimsJson == null) {
			throw new ControllerException(401, "token can not verify through");
		}
		Claims claims = ObjectUtil.jsonToObjectWithJackson(claimsJson, Claims.class);
		if (claims == null) {
			throw new ControllerException(401, "token can not verify through");
		}
		try {
			UCDTO udto = (UCDTO) tokenCache.get(claims.getIss());
			if (udto == null) {
				throw new ControllerException(401, "token can not verify through");
			}
			if (!udto.getToken().equals(token)) {
				throw new ControllerException(401, "token can not verify through");
			}
			String[] userkm = parseStr(udto.getToken());
			if (!userkm[1].equals(claimsCiphertextMd5_)) {
				throw new ControllerException(401, "token can not verify through");
			}
			// 比较当前请求信息与登录是的信息比较，如果存在不一致则可能请求客户端发生了变化
			Claims userClaims = udto.getClaims();
			if (userClaims.getClientType().equals(Claims.ClientType.BROWSER.toString())) {
				String userAgent = getUserAgent(wi);
				if (userAgent == null || userAgent.trim().length() == 0) {
					throw new ControllerException(406, "Unable to identify the source of the request");
				}
				if (!userClaims.getUserAgent().equals(Coder.md5(userAgent))) {
					throw new ControllerException(406, "Unable to identify the source of the request");
				}
			}
			String clientId = getClientId(wi);
			if (clientId == null) {
				throw new ControllerException(406, "Unable to identify the source of the request");
			}
			if (!userClaims.getClientId().equals(clientId)) {
				throw new ControllerException(406, "Unable to identify the source of the request");
			}
			if (udto.checkExipred()) {
				throw new ControllerException(408, "the request expired");
			} else {//
				long nowTime = System.currentTimeMillis();
				long txt = nowTime - udto.getUpdateTime();
				if (txt > localCacheTime) {
					udto.setUpdateTime(nowTime);
					tokenCache.put(claims.getIss(), udto);// 针对远程的，必须写回redis与jvm不用写不一样
				}
			}
		} catch (Exception e) {
			if (e instanceof ControllerException) {
				throw (ControllerException) e;
			} else {
				logger.error("logout err", e);
			}
			throw new ControllerException(401, "token can not verify through");
		}
		return claims.getIss();
	}
}
