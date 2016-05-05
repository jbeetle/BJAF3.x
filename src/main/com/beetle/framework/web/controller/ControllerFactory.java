package com.beetle.framework.web.controller;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.beetle.framework.log.AppLogger;
import com.beetle.framework.util.ClassUtil;
import com.beetle.framework.util.cache.ICache;
import com.beetle.framework.util.cache.StrongCache;
import com.beetle.framework.util.file.XMLReader;
import com.beetle.framework.web.common.CommonUtil;
import com.beetle.framework.web.common.WebConst;
import com.beetle.framework.web.controller.document.DocFactory;
import com.beetle.framework.web.controller.document.IDocument;
import com.beetle.framework.web.controller.draw.DrawFactory;
import com.beetle.framework.web.controller.draw.IDraw;
import com.beetle.framework.web.controller.upload.IUpload;
import com.beetle.framework.web.controller.upload.UploadFactory;

/**
 * <p>
 * Title: BeetleSoft Framework
 * </p>
 * <p/>
 * <p>
 * Description:
 * </p>
 * <p/>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p/>
 * <p>
 * Company: 甲壳虫软件
 * <p/>
 * </p>
 * 
 * @author 余浩东
 * @version 1.0
 */
public class ControllerFactory {
	private static ICache cacheCtrl = new StrongCache(134);
	private static Map<String, String> zeroConfig = new ConcurrentHashMap<String, String>();
	private static Map<String, String> moduleItemMap = new ConcurrentHashMap<String, String>(); // 存储模块项
	private static Map<String, String> standardTable = new ConcurrentHashMap<String, String>();
	private static Map<String, String> virtualTable = new ConcurrentHashMap<String, String>();
	private static Map<String, String> serviceTable = new ConcurrentHashMap<String, String>();
	private final static Object locker = new Object();

	private static boolean initFlag = false;
	private static Map<String, HashSet<String>> controllerViewConfig = new ConcurrentHashMap<String, HashSet<String>>();// 控制器与视图关系映射
	private static volatile ICutFrontAction preCall = null;
	private static volatile ICutBackAction backCall = null;
	private static String globalPreCallStr = null;
	private static String globalBackCallStr = null;
	private static AppLogger logger = AppLogger
			.getInstance(ControllerFactory.class);

	/**
	 * 返回系统所有控制（包括标准、虚拟、ajax等） 每次都是动态生产一个map
	 * 
	 * @return
	 */
	public static Map<String, String> getAllControllers() {
		Map<String, String> m = new ConcurrentHashMap<String, String>();
		m.putAll(standardTable);
		m.putAll(virtualTable);
		m.putAll(serviceTable);
		m.putAll(UploadFactory.getUploadConfig(null));
		m.putAll(DrawFactory.getDrawConfig(null));
		m.putAll(DocFactory.getDocConfig(null));
		m.putAll(zeroConfig);
		return m;
	}

	static ICutBackAction getControllerGlobalBackCall()
			throws ControllerException {
		if (backCall == null) {
			if (globalBackCallStr == null || globalBackCallStr.equals("")) {
				return null;
			}
			try {
				backCall = (ICutBackAction) Class.forName(
						globalBackCallStr.trim()).newInstance();
			} catch (Exception ex) {
				throw new ControllerException(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex);
			}
		}
		return backCall;
	}

	static ICutFrontAction getControllerGlobalPreCall()
			throws ControllerException {
		// logger.debug("globalPreCallStr:{}", globalPreCallStr);
		if (preCall == null) {
			if (globalPreCallStr == null || globalPreCallStr.equals("")) {
				return null;
			}
			try {
				preCall = (ICutFrontAction) Class.forName(
						globalPreCallStr.trim()).newInstance();
			} catch (Exception ex) {
				// logger.error(ex);
				throw new ControllerException(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex);
			}
		}
		return preCall;
	}

	private static String getGlobalBackCallStr(ServletContext application) {
		InputStream in2;
		in2 = application.getResourceAsStream(WebConst.WEB_CONTROLLER_FILENAME);
		String a = XMLReader.getTagContent(in2,
				"mappings.controllers.cutting.ctrlBackAction");
		return a;
	}

	private static String getGlobalPreCallStr(ServletContext application) {
		InputStream in2;
		in2 = application.getResourceAsStream(WebConst.WEB_CONTROLLER_FILENAME);
		String precallName = XMLReader.getTagContent(in2,
				"mappings.controllers.cutting.ctrlFrontAction");
		return precallName;
	}

	/**
	 * 根据request的servletpath获取其对应的实现类的实例
	 * 
	 * @param application
	 * @param request
	 * @return
	 * @throws ServletException
	 */
	public static ControllerImp findController(ServletContext application,
			HttpServletRequest request) throws ControllerException {
		// only name
		String ctlName = CommonUtil.analysePath(request.getServletPath());
		final String zerokey;
		if (!CommonUtil.isHaveExt(ctlName)) {// ws
			ctlName = request.getPathInfo();
			zerokey = CommonUtil.formatPath(ctlName);
			// zerokey = ctlName;
		} else {
			zerokey = CommonUtil.formatPath(request.getServletPath());// 零配置路径
		}
		request.setAttribute(CommonUtil.controllname, ctlName); // 保存已分析的控制器名称
		// 配置和零配置混合
		logger.debug("zerokey:{}", zerokey);
		logger.debug("controllname:{}", ctlName);
		// logger.debug("cacheCtrl:{}", cacheCtrl);
		if (cacheCtrl.containsKey(ctlName)) {// 配置型控制
			Object handler = cacheCtrl.get(ctlName);
			return (ControllerImp) handler;
		} else {
			if (cacheCtrl.containsKey(zerokey)) {// 零配置
				// request.setAttribute(CommonUtil.controllname, key);
				return (ControllerImp) cacheCtrl.get(zerokey);
			}
			String prefix = (String) request
					.getAttribute(CommonUtil.WEB_CTRL_PREFIX);
			if (prefix == null)
				prefix = "";
			String javaPath = (prefix + zerokey).replace(
					CommonUtil.RIGHT_SLASHDOT, CommonUtil.DOT);
			javaPath = CommonUtil.delLastDot(javaPath);
			request.setAttribute(CommonUtil.controllerimpclassname, javaPath);
			logger.debug("controllerimpclassname:{}", javaPath);
			synchronized (zerokey) {
				try {
					return newControllerFromClass(zerokey, javaPath);
				} catch (ClassNotFoundException nofe) {
					return lookForConfigFileToCreate(application, request,
							ctlName);
				}
			}
		}
	}

	private static ControllerImp lookForConfigFileToCreate(
			ServletContext application, HttpServletRequest request,
			String ctlname) throws ControllerException {
		// 从配置文件中寻找并新建
		// request.setAttribute(CommonUtil.controllname, path);
		// virtual
		if (isVirtualController(application, ctlname)) {
			return new VirtualController(getVirtualView(ctlname), request);
		}
		// upload case,check if upload controller
		if (UploadFactory.isUploadController(ctlname, application)) {
			ControllerImp upc = new UploadController();
			cacheCtrl.put(ctlname, upc);
			return upc;
		}
		// draw
		if (DrawFactory.isDrawController(ctlname, application)) {
			ControllerImp drawcontroller = new DrawController();
			cacheCtrl.put(ctlname, drawcontroller);
			return drawcontroller;
		}
		// document
		if (DocFactory.isDocController(ctlname, application)) {
			ControllerImp cdc = new DocumentController();
			cacheCtrl.put(ctlname, cdc);
			return cdc;
		}
		//
		String className = getControllerClassByUrlPath(application, ctlname);
		if (className == null) {
			throw new ControllerException(HttpServletResponse.SC_NOT_FOUND,
					"controller not found![" + ctlname
							+ "]please check your controller config file");
		}
		request.setAttribute(CommonUtil.controllerimpclassname, className);
		try {
			Object handler = Class.forName(className.trim()).newInstance();
			ControllerImp imp = (ControllerImp) handler;
			if (imp.isInstanceCacheFlag()) { // 判别此控制器对象是否需要缓存
				cacheCtrl.put(ctlname, handler);
				if (logger.isDebugEnabled()) {
					logger.debug("cache controller:" + ctlname);
				}
			}
			return imp;
		} catch (Exception ex) {
			logger.error(ex);
			throw new ControllerException(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex);
		}
	}

	private static ControllerImp newControllerFromClass(String key,
			String javaPath) throws ControllerException, ClassNotFoundException {
		if (logger.isDebugEnabled()) {
			logger.debug("loading new Controller class....{}", javaPath);
		}
		Class<?> ct = Class.forName(javaPath);
		if (!ClassUtil.isRootSubClassOf(ct, ControllerImp.class)) {// 安全保护
			throw new ControllerException(HttpServletResponse.SC_FORBIDDEN,
					"Illegal request, don't fooling around!");
		}
		try {
			ControllerImp ctrlImp;
			Object ctrlObj = ct.newInstance();
			if (ctrlObj instanceof IUpload) {
				ctrlImp = new UploadController();
			} else if (ctrlObj instanceof IDraw) {
				ctrlImp = new DrawController();
			} else if (ctrlObj instanceof IDocument) {
				ctrlImp = new DocumentController();
			} else {
				ctrlImp = (ControllerImp) ctrlObj;
			}
			if (ctrlImp.isInstanceCacheFlag()) {
				cacheCtrl.put(key, ctrlImp);
				zeroConfig.put(key, javaPath);// 记录相关零配置控制器
				if (logger.isDebugEnabled()) {
					logger.debug("cache controller:" + key);
				}
			}
			// request.setAttribute(CommonUtil.controllname, key);
			return ctrlImp;
		} catch (Exception e) {
			logger.error(e);
			throw new ControllerException(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
		}
	}

	/**
	 * 获取标准控制器的配置数据
	 * 
	 * @param app
	 *            ServletContext
	 * @return Map
	 */
	public static Map<String, String> getStandartControllerConfigs(
			ServletContext app) {
		if (!initFlag) {
			loadConfigInfo(app);
			initFlag = true;
		}
		return standardTable;
	}

	/**
	 * 获取模块项目
	 * 
	 * @param app
	 *            ServletContext
	 * @return Map
	 */
	public static Map<String, String> getModuleItem(ServletContext app) {
		if (!initFlag) {
			loadConfigInfo(app);
			initFlag = true;
		}
		return moduleItemMap;
	}

	private static synchronized void loadConfigInfo(ServletContext app) {
		if (logger.isDebugEnabled()) {
			logger.debug("try to load WebController.xml datas into cache...");
		}
		String filename = WebConst.WEB_CONTROLLER_FILENAME;
		// virtual
		CommonUtil.fill_DataMap(app, filename, "mappings.controllers.virtual",
				"vItem", "name", "view", virtualTable);
		// standard
		CommonUtil.fill_DataMap(app, filename, "mappings.controllers.standard",
				"sItem", "name", "class", standardTable);
		// ws
		CommonUtil.fill_DataMap(app, filename, "mappings.controllers.service",
				"wsItem", "name", "class", serviceTable);
		// ...获取全局回叫实现类

		globalPreCallStr = getGlobalPreCallStr(app);
		globalBackCallStr = getGlobalBackCallStr(app);
		// 获取module数据..
		CommonUtil.fill_DataMap(app, filename, "mappings.module", "mItem",
				"filename", "active", moduleItemMap);
		// 加载其它文件的数据

		if (!moduleItemMap.isEmpty()) {
			Set<?> s = moduleItemMap.entrySet();
			Iterator<?> it = s.iterator();
			while (it.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry e = (Map.Entry) it.next();
				String fn = (String) e.getKey();
				String active = (String) e.getValue();
				if (active.equalsIgnoreCase("true")) {
					// virtual
					CommonUtil.fill_DataMap(app, fn,
							"mappings.controllers.virtual", "vItem", "name",
							"view", virtualTable);
					// standard
					CommonUtil.fill_DataMap(app, fn,
							"mappings.controllers.standard", "sItem", "name",
							"class", standardTable);
					// ws
					CommonUtil.fill_DataMap(app, fn,
							"mappings.controllers.service", "wsItem", "name",
							"class", serviceTable);
				}
			}
		}
		initDefaultCtrl();
		initFlag = true;
		if (logger.isDebugEnabled()) {
			logger.debug("load done!");
		}
	}

	/**
	 * 初始化默认管理的控制器
	 */
	private static void initDefaultCtrl() {
		zeroConfig.put("framework.web.manage.ManageController",
				"com.beetle.framework.web.manage.ManageController");// 注册管理控制器

		try {
			cacheCtrl.put(
					"framework.web.manage.ManageController",
					Class.forName(
							"com.beetle.framework.web.manage.ManageController")
							.newInstance());
		} catch (Exception e) {
			logger.error(e);
		}
	}

	static boolean isVirtualController(ServletContext app, String urlPath) {
		if (!initFlag) {
			loadConfigInfo(app);
			initFlag = true;
		}
		// return virtualTable.containsKey(urlPath);
		boolean f = virtualTable.containsKey(urlPath);
		if (!f) {// 检查看是否为零配置虚拟写法eg:$|xpath|ypath|zcontroller.ctrl
			if (urlPath.indexOf('|') >= 0) {// 合法为零配置url
				virtualTable.put(urlPath, urlPath);
				f = true;
			}
		}
		return f;
	}

	static String getVirtualView(String urlPath) {
		return virtualTable.get(urlPath);
	}

	private static String getControllerClassByUrlPath(ServletContext app,
			String url) {
		if (!initFlag) {
			loadConfigInfo(app);
			initFlag = true;
		}
		String r = (String) standardTable.get(url);
		if (r == null) {
			r = (String) serviceTable.get(url);
		}
		return r;
	}

	/**
	 * 记录控制器与视图的使用关系
	 * 
	 * @param request
	 * @param viewName
	 */
	static void mapCtrlView(HttpServletRequest request, String viewName) {
		String flag = (String) request
				.getAttribute(CommonUtil.CTRL_VIEW_MAP_ENABLED);
		if (flag != null && flag.equalsIgnoreCase(CommonUtil.TRUE_STR)) {
			String ctrlname = (String) request
					.getAttribute(CommonUtil.controllname);
			if (!controllerViewConfig.containsKey(ctrlname)) {
				createTipSet(ctrlname);
			}
			HashSet<String> hs = controllerViewConfig.get(ctrlname);
			if (viewName != null) {
				hs.add(viewName);
			}
		}
	}

	public static Map<String, HashSet<String>> getCtrlViewMap() {
		return controllerViewConfig;
	}

	private static void createTipSet(String ctrlname) {
		if (!controllerViewConfig.containsKey(ctrlname)) {
			synchronized (locker) {
				controllerViewConfig.put(ctrlname, new HashSet<String>());
			}
		}
	}
}
