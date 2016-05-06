package com.beetle.framework.web.controller.service;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;

import com.beetle.framework.AppProperties;
import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.resource.dic.def.WebService;
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

	private static final String resource_DI_SERVICE_PACK_PATH = "resource_DI_SERVICE_PACK_PATH";
	private final static AppLogger logger = AppLogger.getInstance(WebRPCService.class);

	public WebRPCService() {
		super();

	}

	@Override
	public ModelData defaultAction(WebInput wi) throws ControllerException {
		String face = wi.getParameter(CommonUtil.PRC_SERVICE_FACE_STR, "");
		if (face.length() == 0) {
			throw new ControllerException("must set $interface parameter!");
		}
		String action = wi.getParameter(CommonUtil.PRC_SERVICE_METHOD_STR, "");
		if (action.length() == 0) {
			throw new ControllerException("must set $method parameter!");
		}
		String serviePack = AppProperties.get(resource_DI_SERVICE_PACK_PATH, "");
		if (serviePack.length() > 0) {
			face = serviePack + "." + face;
		}
		try {
			Class<?> faceC = Class.forName(face);
			logger.debug("faceC:{}", faceC);
			logger.debug("action:{}", action);
			Method method = ClassUtil.getClassMethod(faceC, action);
			logger.debug("method:{}", method);
			if (!method.isAnnotationPresent(WebService.class)) {
				throw new ControllerException(face + " is not declare as a webservice ");
			}
			WebService.ReturnDataFormat rf = method.getAnnotation(WebService.class).returnDataFormat();
			Object faceImpObj = this.serviceLookup(faceC);
			Object[] paramObjs = fillParamters(wi, face, action);
			Object value = ClassUtil.invoke(faceImpObj, action, paramObjs);
			ModelData md = new ModelData();
			md.setData(value);
			if (rf.value() == WebService.ReturnDataFormat.JSON.value()) {
				return md.asJSON();
			} else {
				return md.asXML();
			}
		} catch (ControllerException e) {
			throw e;
		} catch (Exception e) {
			throw new ControllerException("lookup face[" + face + "] err", e);
		}
	}


	/**
	 * 填充方法参数，如果默认的不支持所有的类型，可重载自己实现
	 * @param wi 页面参数输入对象
	 * @param interfaceName 服务接口名称
	 * @param mothodName 服务调用的方法名
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
