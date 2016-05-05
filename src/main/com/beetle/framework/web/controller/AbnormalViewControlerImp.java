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

import com.beetle.framework.web.common.CommonUtil;
import com.beetle.framework.web.view.View;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>
 * Title: BeetleWeb
 * </p>
 * 
 * <p>
 * Description: MVC Web Framework
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: 甲壳虫软件

 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public abstract class AbnormalViewControlerImp extends ControllerImp {
	static final String abnormalViewName = "BEETLE_ABNORMAL_VIEW_NAME_760224";
	private HttpServletResponse response;
	private ServletContext servletContext;

	protected abstract void performX(WebInput webInput, OutputStream outputStream)
			throws ControllerException;

	public View perform(WebInput webInput) throws ControllerException {
		this.response = webInput.getResponse();
		this.servletContext = (ServletContext) webInput.getRequest()
				.getAttribute(CommonUtil.app_Context);
		View view = new View(abnormalViewName);
		OutputStream out;
		try {
			out = webInput.getResponse().getOutputStream();
			performX(webInput, out);
		} catch (IOException ex) {
			throw new ControllerException(ex);
		}
		return view;
	}

	public void setContentType(String contentType) {
		if (response != null) {
			response.setContentType(contentType);
		}
	}

	public void addHeader(String key, String value) {
		if (response != null) {
			response.addHeader(key, value);
		}
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setContentLength(int size) {
		if (response != null) {
			response.setContentLength(size);
		}
	}
}
