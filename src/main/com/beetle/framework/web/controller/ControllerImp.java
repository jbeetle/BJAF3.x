/*
 * BJAF - Beetle J2EE Application Framework
 * 甲壳虫J2EE企业应用开发框架
 * 版权所有2003-2015 余浩东 (www.beetlesoft.net)
 * 
 * 这是一个免费开源的软件，您必须在
 *<http://www.apache.org/licenses/LICENSE-2.0>
 *协议下合法使用、修改或重新发布。
 *
 * 感谢您使用、推广本框架，若有建议或问题，欢迎您和我联系。
 * 邮件： <yuhaodong@gmail.com/>.
 */
package com.beetle.framework.web.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.beetle.framework.AppProperties;

import com.beetle.framework.web.common.CommonUtil;
import com.beetle.framework.web.view.View;

/**
 * <p>
 * Title: Beetle J2EE Application FrameWork
 * </p>
 * <p>
 * Description: 子控制器抽象类，每个控制器必须继承这个抽象类，同时需要在WebController.xml文件中注册(零配置除外)
 * <p/>
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: 甲壳虫软件
 * <p/>
 * </p>
 * 
 * @author 余浩东
 * @version 1.0
 */

public abstract class ControllerImp {
	public ControllerImp() {
	}

	private boolean globalBackCallFlag = true;

	private boolean globalFrontCallFlag = true; // 默认处理PreCall提前回调

	private boolean requireSession = false; // 默认不需要做session检查

	private boolean disableGetMethodFlag = false;// 禁止get方法请求，默认允许

	private int cacheSeconds = -1;

	private boolean instanceCacheFlag = true; // 默认都需要缓存

	private int avoidSubmitSeconds = 0; // 避免控制器多次提交

	private String className = null;

	private final static Map<String, Object> cache = new ConcurrentHashMap<String, Object>();

	public void setAvoidSubmitSeconds(int seconds) {
		this.avoidSubmitSeconds = seconds;
	}

	View dealRequest(HttpServletRequest request, HttpServletResponse response)
			throws ControllerException {
		if (this.disableGetMethodFlag) {
			if (request.getMethod().equalsIgnoreCase("get")) {
				throw new ControllerException(HttpServletResponse.SC_FORBIDDEN,
						"the get method for this request had been forbided! ");
			}
		}
		// 设置是否通过header缓存
		if (this.cacheSeconds <= 0) {
			noCache(response);
		} else if (this.cacheSeconds > 0) {
			cacheForSeconds(response, this.cacheSeconds);
		}
		// 防止控制器多次提交
		if (avoidSubmitSeconds > 0) {
			avoidSubmit(request, response);
		}
		// ////////////////==设置front回调==//////////////////
		WebInput webInput = new WebInput(request, response);
		if (this.globalFrontCallFlag) {
			ICutFrontAction preCall = ControllerFactory
					.getControllerGlobalPreCall();
			if (preCall != null) {
				View ve = preCall.act(webInput);
				if (ve != null) {
					return ve;
				}
			}
		}
		// 检查session
		if (this.requireSession) {
			HttpSession session = request.getSession(false);
			if (session == null) {
				noCache(response);
				if (webInput.getRequest().getAttribute(
						CommonUtil.CANCEL_SESSION_CHECK_FLAG) == null) {
					String dv = AppProperties
							.get("web_view_DisabledSessionView");
					if (dv != null && dv.trim().length() > 0) {
						return new View(dv);
					}
					return new View(CommonUtil.DISABLED_SESSION_VIEW);
				}
			}
		}
		// 最大并发数检查
		if (className != null && cache.containsKey(className)) {
			ParallelValue pv = (ParallelValue) cache.get(this.className);
			int max = pv.getMax();
			int cur = pv.getCur();
			if (cur > max) {
				throw new ControllerException(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"exceeding this controller's request parallel amount limit,do it later,please!");
			} else {
				cur = cur + 1;
				pv.setCur(cur);
			}
		}
		// ///////////////////==执行正常控制代码==//////////////////////////////
		View view;
		try {
			view = perform(webInput);
		} finally {
			// 更新计数器
			if (className != null && cache.containsKey(className)) {
				ParallelValue pv = (ParallelValue) cache.get(this.className);
				int cur = pv.getCur();
				cur = cur - 1;
				pv.setCur(cur);
			}
		}
		// ////////////////////////==设置back回调==//////////////////////////
		if (this.globalBackCallFlag) {
			ICutBackAction backCall = ControllerFactory
					.getControllerGlobalBackCall();
			if (backCall != null) {
				View ve = backCall.act(webInput);
				if (ve != null) {
					if (view != null) {
						view.clear();
					}
					return ve;
				}
			}
		}
		return view;
	}

	private void avoidSubmit(HttpServletRequest request,
			HttpServletResponse response) throws ControllerException {
		String ckname = CommonUtil.analysePath(request.getServletPath().trim());
		if (ckname.indexOf('$') == 0) {
			ckname = ckname.substring(1);
		}
		Cookie cookie = CommonUtil.getCookie(ckname, request);
		if (cookie != null) {
			long old = Long.parseLong(cookie.getValue());
			long now = System.currentTimeMillis();
			long cmp = this.avoidSubmitSeconds * 1000;
			if (now - old <= cmp) { // 如果是在设置的时间内提交请求的话，则直接中断请求
				throw new ControllerException(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Don't submit the same request repeating！");
			} else {
				cookie.setMaxAge(-1); // 删除请求
			}
		} else {
			cookie = new Cookie(ckname, String.valueOf(System
					.currentTimeMillis()));
			cookie.setMaxAge(this.avoidSubmitSeconds);
			response.addCookie(cookie);
		}
	}

	final void cacheForSeconds(HttpServletResponse response, int seconds) {
		String hval = "max-age=" + seconds;
		response.setHeader("Cache-Control", hval);
	}

	private void noCache(HttpServletResponse response) {
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 1L);
	}

	/**
	 * 从本地容器查找业务层的Service,与ServiceFactory相应的方法功能一致
	 * 
	 * @param interfaceClass
	 *            --服务接口类
	 * @return 服务实例
	 */
	public <T> T localServiceLookup(final Class<T> interfaceClass) {
		return com.beetle.framework.business.service.ServiceFactory
				.localServiceLookup(interfaceClass);
	}

	/**
	 * 从业务层查找服务，与ServiceFactory相应的方法功能一致<br>
	 * 首先根据application.properties配置文件中的参数“rpc_client_proxyInvoke=jvm”
	 * 是否定义来优先在本地查找；<br>
	 * 如果上述参数未定义，则会从“rpc_client_remoteAddress”定义的地址去远程查找，远程访问采取连接池的方式<br>
	 * 池大小通过参数"rpc_client_connectionAmount"定义
	 * 
	 * @param <T>
	 * @param interfaceClass
	 * @return
	 */
	public <T> T serviceLookup(final Class<T> interfaceClass) {
		return com.beetle.framework.business.service.ServiceFactory
				.serviceLookup(interfaceClass);
	}

	/**
	 * 从业务层远程查找服务接口，与ServiceFactory相应的方法功能一致
	 * 
	 * @param <T>
	 * @param interfaceClass
	 *            --服务接口定义类
	 * @param host
	 *            --远程地址
	 * @param port
	 *            --远程地址监听端口
	 * @param withShortConnection
	 *            --是否采取短连接方式（就是接口方法每次调用使用一条连接）<br>
	 *            默认为false，即采取连接池长连接访问的方式，连接池的大小通过“rpc_client_connectionAmount”
	 *            参数定义
	 * @return
	 */
	public <T> T rpcServiceLookup(final Class<T> interfaceClass,
			final String host, final int port, boolean withShortConnection) {
		return com.beetle.framework.business.service.ServiceFactory
				.rpcServiceLookup(interfaceClass, host, port,
						withShortConnection);
	}

	/**
	 * 控制逻辑执行方法，系统框架主控制器（MainControllerServlet）会根据请求的url来找到此控制类， 并执行此方法完成任务
	 * 
	 * @param webInput
	 *            Web页面输入参数对象，对request对象封装，基本上保留request的方法，屏蔽到一些不利于开发的方法
	 * @return 视图对象（视图的名称[WebView.xml]，以及相关的数据）
	 * @throws ControllerException
	 */
	public abstract View perform(WebInput webInput) throws ControllerException;

	/**
	 * 启动此控制器在执行逻辑之前进行Session检查，框架默认不做检查 (注:必须在构造函数内调用才有效)
	 * 如果session不存在，则主控制器会不不处理此控制器，直接挑转到NoSessionView视图
	 */
	public void enableSessionCheck() {
		this.requireSession = true;
	}

	private static class ParallelValue {
		int max;

		int cur;

		public ParallelValue(int max, int cur) {
			this.max = max;
			this.cur = cur;
		}

		public int getMax() {
			return this.max;
		}

		public int getCur() {
			return this.cur;
		}

		public void setCur(int cur) {
			this.cur = cur;
		}
	}

	/**
	 * 设置此控制器最大支持并发请求数，默认为负数，即无限制。 此方法在对此控制器做并发控制时候，才需要设置，其它情况，框架不会
	 * <p/>
	 * 对控制器进行任何并发数量限制。(注:必须在构造函数内调用才有效)
	 * 
	 * @param amount
	 */
	public void setMaxParallelAmount(int amount) {
		if (amount > 0) {// 只作一次初始化设置
			if (this.className == null) {
				this.className = this.getClass().getName();
			}
			if (!cache.containsKey(className)) {
				cache.put(className, new ParallelValue(amount, 0));
			}
		}
	}

	/**
	 * 利用http协议的header缓存生产的view (注:必须在构造函数内调用才有效)
	 * 
	 * @param cacheSeconds
	 *            单位为秒
	 */
	public void setCacheSeconds(int cacheSeconds) {
		this.cacheSeconds = cacheSeconds;
	}

	boolean isInstanceCacheFlag() {
		return instanceCacheFlag;
	}

	/**
	 * 设置此控制器是否需要缓存在内容中，默认，所有的控制器都被缓存 (注:必须在构造函数内调用才有效)
	 * 
	 * @param instanceCacheFlag
	 *            如果为true表示需要缓存，为false，此控制器实例不被缓存，默认为true，如果你的控制器为线程不安全的，
	 *            则设置为false
	 */
	public void setInstanceCacheFlag(boolean instanceCacheFlag) {
		this.instanceCacheFlag = instanceCacheFlag;
	}

	/**
	 * 禁止此控制器参与全局“前置”回调。 前置回调－－是指在系统执行此控制器之前会先执行此回调。 (注:必须在构造函数内调用才有效)
	 */
	public void disableFrontAction() {
		this.globalFrontCallFlag = false;
	}

	/**
	 * 禁止此控制器参与全局“后置”回调，后置回调－－是指在系统执行此控制器Perform后会执行此回调。 (注:必须在构造函数内调用才有效)
	 */
	public void disableBackAction() {
		this.globalBackCallFlag = false;
	}

	/**
	 * 禁止此控制器处理http协议的get请求(注:必须在构造函数内调用才有效)
	 */
	public void disableGetMethod() {
		this.disableGetMethodFlag = true;
	}
}
