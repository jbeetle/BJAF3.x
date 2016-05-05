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
package com.beetle.framework.web;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.beetle.framework.AppContext;
import com.beetle.framework.AppProperties;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.web.common.CommonUtil;
import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.ControllerFactory;
import com.beetle.framework.web.controller.ControllerHelper;
import com.beetle.framework.web.controller.document.DocFactory;
import com.beetle.framework.web.controller.draw.DrawFactory;
import com.beetle.framework.web.controller.upload.UploadFactory;
import com.beetle.framework.web.onoff.ICloseUp;
import com.beetle.framework.web.onoff.IStartUp;
import com.beetle.framework.web.onoff.OnOffFactory;
import com.beetle.framework.web.view.ViewFactory;

final public class GlobalDispatchServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static volatile boolean init_f = false;

	private static volatile boolean destory_f = false;
	private final static Object locker = new Object();

	private static class Config {
		private String charset = null;
		private String contentType = null;
		private String ctrlPrefix = null;
		private String disabledSessionView = null;
		private String ctrl_view_map_enabled = null;
		private String web_service_data_default_format = null;
		private static Config instance = new Config();

		public static Config getInstance() {
			return instance;
		}

		private Config() {

		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doMainController(req, resp);
	}

	/**
	 * init
	 * 
	 * @throws ServletException
	 * @todo Implement this javax.servlet.GenericServlet method
	 */
	public void init() throws ServletException {
		synchronized (locker) {
			if (!init_f) {
				init_f = true;
				checkConfig();
				AppLogger logger = AppLogger
						.getInstance(GlobalDispatchServlet.class);
				logger.info("===Start up the ["
						+ this.getServletContext().getServletContextName()
						+ "] web application");
				logger.info("appHomePath:[{}]", AppProperties.getAppHome());
				IStartUp su = OnOffFactory.getStartUp(this.getServletContext());
				if (su != null) {
					su.startUp(this.getServletContext());
				}
				// 读取配置数据到内存
				ControllerFactory.getStandartControllerConfigs(this
						.getServletContext());
				DrawFactory.getDrawConfig(this.getServletContext());
				UploadFactory.getUploadConfig(this.getServletContext());
				DocFactory.getDocConfig(this.getServletContext());
				ControllerFactory.getModuleItem(getServletContext());
				ViewFactory.loadViewConfigInfo(getServletContext()); // 加载视图数据以便初始化
				Config cf = Config.getInstance();
				cf.charset = this.getServletContext().getInitParameter(
						"WEB_ENCODE");
				if (cf.charset == null) {
					cf.charset = System.getProperty("file.encoding");
				}
				cf.ctrlPrefix = this.getServletContext().getInitParameter(
						"CTRL_PREFIX");
				if (cf.ctrlPrefix == null) {
					cf.ctrlPrefix = "";
				} else {// 格式化前缀
					cf.ctrlPrefix = CommonUtil
							.addLastBeveltoPatch(cf.ctrlPrefix);
				}
				if (cf.ctrl_view_map_enabled == null) {
					cf.ctrl_view_map_enabled = this.getServletContext()
							.getInitParameter("CTRL_VIEW_MAP_ENABLED");
				}
				cf.web_service_data_default_format = this.getServletContext()
						.getInitParameter("WEB_SERVICE_DATA_DEFAULT_FORMAT");
				if (cf.web_service_data_default_format == null) {
					cf.web_service_data_default_format = "xml";
				}
				if (cf.disabledSessionView == null) {
					cf.disabledSessionView = this.getServletContext()
							.getInitParameter("DISABLED_SESSION_VIEW");
					logger.debug("disabledSessionView={}",
							cf.disabledSessionView);
					if (cf.disabledSessionView == null) {
						cf.disabledSessionView = "";
					}
					// 如果WebView.xml定义了，那么与它为准
					if (!ViewFactory.getViewCache().containsKey(
							CommonUtil.DISABLED_SESSION_VIEW)) {
						ViewFactory.getViewCache().put(
								CommonUtil.DISABLED_SESSION_VIEW,
								cf.disabledSessionView);
					}
				}
				//
				boolean baflag = AppProperties.getAsBoolean(
						"web_businessAppSrv_enabled", false);
				logger.debug("web_businessAppSrv_enabled:{}", baflag);
				if (baflag) {
					logger.debug("BusinessAppSrv.start....");
					com.beetle.framework.business.server.BusinessAppSrv.start();
					logger.debug("BusinessAppSrv.started!");
				}
				//
				cf.contentType = "text/html; charset=" + cf.charset;
				logger.info("charset:" + cf.charset);
				logger.info("ctrlPrefix:" + cf.ctrlPrefix);
				logger.info("webServiceDataDefaultFormat:"
						+ cf.web_service_data_default_format);
				logger.info("ctrlViewMapEnabled:" + cf.ctrl_view_map_enabled);
				logger.info("GlobalDispatchServlet has initialized!");
			}
		}
	}

	private void checkConfig() {
		try {
			String cp = "beetle.application.home.path";
			URL url = this.getServletContext().getResource(
					"/WEB-INF/config/application.properties");
			if (url != null) {
				String ptl = url.getProtocol();
				String fp = System.getProperty(cp);
				if (fp == null || fp.trim().length() == 0) {
					String p = url.getPath();
					int x = p.lastIndexOf('/');
					p = p.substring(0, x);
					// System.setProperty(cp, "jndi:" + p);设置全局，对于多个war包发布的场景会冲突
					// 放在appContext里面
					AppContext.getInstance().setAppHomePath(ptl + ":" + p);
					AppContext.getInstance().bind("web.app.servlet.context",
							this.getServletContext());
					AppContext.getInstance()
							.bind("web.app.config.home.path", p);
					AppContext.getInstance().bind(
							"web.app.config.home.path.protocol", ptl);
				}
			}
		} catch (Exception e) {
			AppLogger logger = AppLogger
					.getInstance(GlobalDispatchServlet.class);
			logger.error(e);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doMainController(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doMainController(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doMainController(req, resp);
		// super.doGet(req, resp);
	}

	private void doMainController(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		Config cf = Config.getInstance();
		response.setContentType(cf.contentType);
		request.setCharacterEncoding(cf.charset);
		request.setAttribute(CommonUtil.WEB_ENCODE_CHARSET, cf.charset);
		request.setAttribute(CommonUtil.WEB_SERVER_INFO, this
				.getServletContext().getServerInfo());
		request.setAttribute(CommonUtil.WEB_CTRL_PREFIX, cf.ctrlPrefix);
		request.setAttribute(CommonUtil.CTRL_VIEW_MAP_ENABLED,
				cf.ctrl_view_map_enabled);
		request.setAttribute(CommonUtil.DISABLED_SESSION_VIEW,
				cf.disabledSessionView);
		request.setAttribute(CommonUtil.WEB_SERVICE_DATA_DEFAULT_FORMAT,
				cf.web_service_data_default_format);
		request.setAttribute(CommonUtil.app_Context, this.getServletContext());
		try {
			ControllerHelper.doService(request, response,
					this.getServletContext());
		} catch (ControllerException e) {
			if (e.getErrCode() > 0) {
				response.setStatus(e.getErrCode());
				response.setHeader("STATUS_CODE_INFO", e.getMessage());
			} else {
				throw new ServletException(e);
			}
		}
	}

	/**
	 * destroy
	 * 
	 * @todo Implement this javax.servlet.Servlet method
	 */
	public void destroy() {
		synchronized (locker) {
			if (!destory_f) {
				destory_f = true;
				AppLogger logger = AppLogger
						.getInstance(GlobalDispatchServlet.class);
				logger.info("===Closs up the ["
						+ this.getServletContext().getServletContextName()
						+ "] web application");
				ICloseUp cu = OnOffFactory.getCloseUp(this.getServletContext());
				if (cu != null) {
					cu.closeUp(this.getServletContext());
				}
				//
				if (AppProperties.getAsBoolean("web_businessAppSrv_enabled",
						false)) {
					com.beetle.framework.business.server.BusinessAppSrv.stop();
					logger.debug("BusinessAppSrv.stop");
				}
				//
			}
		}
	}

}
