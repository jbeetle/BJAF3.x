package com.beetle.framework.web.jwt;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.beetle.framework.AppProperties;
import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.resource.dic.def.HttpService;
import com.beetle.framework.util.ClassUtil;
import com.beetle.framework.web.common.CommonUtil;
import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.WebInput;
import com.beetle.framework.web.controller.WebServiceController;
import com.beetle.framework.web.view.ModelData;

/*
 * 为了方面业务服务层的Service直接暴露为web服务，直接省去一个个编写控制器
 */
public class WebRPCService extends WebServiceController {

	private static final String web_ws_servicePrefix = "web_openApi_ws_servicePrefix";
	private final static AppLogger logger = AppLogger.getInstance(WebRPCService.class);
	private final static Map<String, Cola> colaCache = new HashMap<String, Cola>();

	private static class Cola {
		boolean isWebService;
		boolean isJson;
		boolean isEnjectUserId;
		boolean isAuthentication;
		Object serviceInstace;
		Method method;

		public Cola() {
			super();
			this.isEnjectUserId = false;
			this.isWebService = false;
			this.isJson = false;
			this.isAuthentication = false;
		}
	}

	public WebRPCService() {
		super();

	}

	@Override
	public ModelData defaultAction(WebInput wi) throws ControllerException {
		// queryString:$interface=system&$method=UserService/queryById
		// queryString:$interface=system&$method=yyy/UserService/queryById
		// 处理rewrite的restful的/xxx/xxx/xxx/
		// 原则上最后一个参数为方法名称，前面都是类的包名
		String face = wi.getParameter(CommonUtil.PRC_SERVICE_FACE_STR, "");
		if (face.length() == 0) {
			throw new ControllerException("must set $interface parameter!");
		}
		String action = wi.getParameter(CommonUtil.PRC_SERVICE_METHOD_STR, "");
		if (action.length() == 0) {
			throw new ControllerException("must set $method parameter!");
		}
		String serviePack = AppProperties.get(web_ws_servicePrefix, "");
		if (serviePack.length() > 0) {
			face = serviePack + "." + face;
		}
		//
		if (action.indexOf('/') > 0) {
			String ss[] = action.split("/");
			StringBuilder packName_ = new StringBuilder();
			String interfaceName_ = "";
			String methodName_ = "";
			for (int i = 0; i < ss.length; i++) {
				if (i == ss.length - 2) {
					interfaceName_ = ss[i];
				} else if (i == ss.length - 1) {
					methodName_ = ss[i];
				} else {
					packName_.append(ss[i]);
					if (i < ss.length - 2) {
						packName_.append('.');
					}
				}
			}
			if (packName_.toString().length() == 0) {
				face = face + "." + interfaceName_;
			} else {
				face = face + "." + packName_.toString() + interfaceName_;
			}
			action = methodName_;
		}
		//
		logger.debug("face:{}", face);
		logger.debug("action:{}", action);
		String userid = null;
		String callkey = face + "." + action;
		Cola cola = colaCache.get(callkey);
		if (cola != null) {
			logger.debug("deal from cache");
			if (!cola.isWebService) {
				throw new ControllerException(face + " is not declare as a webservice ");
			}
			if (cola.isAuthentication) {
				userid = this.verify(wi);
				logger.debug("JwtTokenLoginUserId:{}", userid);
				if (userid != null) {
					wi.bindJwtTokenLoginUserIdInRequest(userid);
				}
			}
			Object[] paramObjs = fillParamters(wi, face, action);
			if (cola.isEnjectUserId) {// 只有验证的才有userid，否则为null
				paramObjs[0] = userid;
				logger.debug("deal {},firstParam:{}", action, paramObjs[0]);
			}
			try {
				Object value = cola.method.invoke(cola.serviceInstace, paramObjs);
				ModelData md = new ModelData();
				md.setData(value);
				if (cola.isJson) {
					return md.asJSON();
				} else {
					return md.asXML();
				}
			} catch (Exception e) {
				//
				throw new ControllerException("lookup face[" + face + "] err", e);
			}
		} else {// 本身线程安全，没有适合粒度的锁，这里不做锁处理
			try {
				logger.debug("deal by new");
				cola = new Cola();
				Class<?> faceC = Class.forName(face);
				Method method = ClassUtil.getClassMethod(faceC, action);
				logger.debug("method:{}", method);
				if (!method.isAnnotationPresent(HttpService.class)) {
					cola.isWebService = false;
					throw new ControllerException(face + " is not declare as a webservice ");
				}
				cola.isWebService = true;
				HttpService.Authentication authc = method.getAnnotation(HttpService.class).authentication();
				if (authc.value() == HttpService.Authentication.YES.value()) {
					cola.isAuthentication = true;
					userid = this.verify(wi);
					logger.debug("JwtTokenLoginUserId:{}", userid);
					if (userid != null) {
						wi.bindJwtTokenLoginUserIdInRequest(userid);
					}
				}
				HttpService.ReturnDataFormat rf = method.getAnnotation(HttpService.class).returnDataFormat();
				Object faceImpObj = this.serviceLookup(faceC);
				cola.serviceInstace = faceImpObj;
				Object[] paramObjs = fillParamters(wi, face, action);
				// 为了方便编程，约定[...ByCurrentUserId]结尾命名的方法（服务），第一个参数为当前用户UserId
				if (action.endsWith("ByCurrentUserId")) {
					paramObjs[0] = userid;
					cola.isEnjectUserId = true;
					logger.debug("deal {},firstParam:{}", action, paramObjs[0]);
				}
				logger.debug("faceImpObj:{}", faceImpObj);
				logger.debug("paramObjs:{}", paramObjs);
				// Object value = ClassUtil.invoke(faceImpObj, action,
				// paramObjs);
				// Object value = ClassUtil.invokeSimple(faceImpObj, action,
				// paramObjs);
				Method methodReal = ClassUtil.getClassMethod(faceImpObj.getClass(), action);
				cola.method = methodReal;
				Object value = methodReal.invoke(faceImpObj, paramObjs);
				ModelData md = new ModelData();
				md.setData(value);
				colaCache.put(callkey, cola);
				logger.debug("cache:{},{}", callkey, cola);
				if (rf.value() == HttpService.ReturnDataFormat.JSON.value()) {
					cola.isJson = true;
					return md.asJSON();
				} else {
					return md.asXML();
				}
			} catch (ControllerException e) {
				throw e;
			} catch (Exception e) {
				throw new ControllerException("lookup face[" + face + "] err", e);
			} finally {
				// 在异常情况下不要缓存，例如第一次验证不过的时候，一定等有一次正常无异常操作以后再缓存，
				// 这样就可以包装缓存的对象都是正确的
				// colaCache.put(callkey, cola);
				// logger.debug("cache:{},{}", callkey, cola);
			}
		}
	}

	/*
	 * 此方法没有实现逻辑，如需要需重载 成功返回userid
	 */
	protected String verify(WebInput wi) throws ControllerException {
		return null;
	}

	/**
	 * 填充方法参数，如果默认的不支持所有的类型，可重载自己实现
	 * 
	 * @param wi
	 *            页面参数输入对象<br>
	 *            页面要输入约定参数：<br>
	 *            $interface--类接口名称<br>
	 *            $method--方面名称<br>
	 *            $parameter--参数名及类型描述字符串，例如：<br>
	 *            // {xxx:Integer,yyy:String,zzz:Date}<br>
	 * @param interfaceName
	 *            服务接口名称
	 * @param mothodName
	 *            服务调用的方法名
	 * @return
	 */
	protected Object[] fillParamters(WebInput wi, String interfaceName, String mothodName) {
		String paramStr = wi.getParameter(CommonUtil.PRC_SERVICE_PARAMS_STR, "");
		logger.debug("paramStr:{}", paramStr);
		Object paramObjs[];
		if (paramStr.length() > 0) {
			// {xxx:Integer,yyy:String,zzz:Date}
			paramStr = paramStr.substring(1, paramStr.length() - 1);
			if (paramStr.trim().length() > 0) {
				String names[] = paramStr.split(",");
				paramObjs = new Object[names.length];
				for (int i = 0; i < names.length; i++) {
					String kv[] = names[i].split(":");
					//
					String tstr = kv[1];
					if (tstr.equalsIgnoreCase(String.class.getSimpleName())) {
						paramObjs[i] = wi.getParameterWithoutTrim(kv[0]);
					} else if (tstr.equalsIgnoreCase(Integer.class.getSimpleName())) {
						paramObjs[i] = wi.getParameterAsInteger(kv[0]);
					} else if (tstr.equalsIgnoreCase(Long.class.getSimpleName())) {
						paramObjs[i] = wi.getParameterAsLong(kv[0]);
					} else if (tstr.equalsIgnoreCase(Float.class.getSimpleName())) {
						paramObjs[i] = wi.getParameterAsFloat(kv[0]);
					} else if (tstr.equalsIgnoreCase(Double.class.getSimpleName())) {
						paramObjs[i] = wi.getParameterAsDouble(kv[0]);
					} else if (tstr.equalsIgnoreCase(Timestamp.class.getSimpleName())) {
						paramObjs[i] = wi.getParameterAsTimestamp(kv[0]);
					} else if (tstr.equalsIgnoreCase(java.sql.Date.class.getSimpleName())) {
						paramObjs[i] = wi.getParameterAsDate(kv[0]);
					} else if (tstr.equalsIgnoreCase(BigDecimal.class.getSimpleName())) {
						paramObjs[i] = wi.getParameterAsBigDecimal(kv[0]);
					} else {
						throw new AppRuntimeException("[" + kv[1]
								+ "]type not support,please Override 'fillParamters' method,deal it yourselef!");
					}
					//
				}
			} else {
				paramObjs = new Object[0];
			}
		} else {
			paramObjs = new Object[0];
		}
		return paramObjs;
	}

}
