package com.beetle.framework.web.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.beetle.framework.AppProperties;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.web.common.CommonUtil;
import com.beetle.framework.web.controller.ControllerHelper.MethodEx;
import com.beetle.framework.web.view.ModelData;

public abstract class WebServiceController extends AbnormalViewControlerImp {
	private static final AppLogger logger = AppLogger
			.getInstance(WebServiceController.class);

	private static class JacksonCreator {
		private volatile org.codehaus.jackson.map.ObjectMapper objectMapper;
		private static volatile JacksonCreator instance = null;

		private JacksonCreator() {
			if (objectMapper == null) {
				objectMapper = new org.codehaus.jackson.map.ObjectMapper();
			}
		}

		public static JacksonCreator getInstance() {
			if (instance == null) {
				instance = new JacksonCreator();
			}
			return instance;
		}

		public void toJSONJackson(OutputStream outputStream,
				final ModelData md, String ecode) throws ControllerException {
			org.codehaus.jackson.JsonGenerator jsonGenerator = null;
			try {
				jsonGenerator = objectMapper.getJsonFactory()
						.createJsonGenerator(outputStream, getEncoding(ecode));
				if (md.isDataBeanFormatReturn()) {
					objectMapper.writeValue(jsonGenerator, md.getData());
				} else {
					objectMapper.writeValue(jsonGenerator, md.getDataMap());
				}
				logger.debug("toJSONJackson ok");
			} catch (Exception e) {
				throw new ControllerException(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
			} finally {
				if (jsonGenerator != null) {
					try {
						jsonGenerator.flush();
						jsonGenerator.close();
					} catch (IOException e) {
						logger.error(e);
					}
				}
			}
		}

		private org.codehaus.jackson.JsonEncoding getEncoding(String ecode) {
			for (org.codehaus.jackson.JsonEncoding encoding : org.codehaus.jackson.JsonEncoding
					.values()) {
				if (ecode.equals(encoding.getJavaName())) {
					return encoding;
				}
			}
			return org.codehaus.jackson.JsonEncoding.UTF8;
		}
	}

	private static class XstreamCreator {
		private static XstreamCreator instance = new XstreamCreator();

		private XstreamCreator() {

		}

		public static XstreamCreator getInstance() {
			return instance;
		}

		public void toJSONXtream(OutputStream outputStream, final ModelData md,
				String ecode) {
			com.thoughtworks.xstream.XStream xtm = new com.thoughtworks.xstream.XStream(
					new com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver());
			xtm.setMode(com.thoughtworks.xstream.XStream.NO_REFERENCES);
			final String dd;
			if (md.isDataBeanFormatReturn()) {
				dd = xtm.toXML(md.getData());
			} else {
				dd = xtm.toXML(md.getDataMap());
			}
			logger.debug("toJSONXtream:{}", dd);
			PrintWriter out = new PrintWriter(outputStream);
			try {
				out.print(dd);
			} finally {
				out.flush();
				out.close();
			}
		}

		public void toXml(OutputStream outputStream, final ModelData md,
				String ecode) {
			com.thoughtworks.xstream.XStream xtm = new com.thoughtworks.xstream.XStream();
			final String dd;
			if (md.isDataBeanFormatReturn()) {
				dd = xtm.toXML(md.getData());
			} else {
				dd = xtm.toXML(md.getDataMap());
			}
			logger.debug("toXML:{}", dd);
			PrintWriter out = new PrintWriter(outputStream);
			try {
				out.println("<?xml version=\"1.0\" encoding=\"" + ecode
						+ "\"?>");
				out.print(dd);
			} finally {
				out.flush();
				out.close();
			}
		}
	}

	@Override
	public void performX(WebInput webInput, OutputStream outputStream)
			throws ControllerException {
		this.setContentType("text/html");
		final ModelData md;
		String actionName = webInput.getParameter("$action");
		logger.debug("actionName:{}", actionName);
		logger.debug("controllerName:{}", webInput.getControllerName());
		Object wso = webInput.getRequest().getAttribute("WS_CTRL_IOBJ");
		logger.debug("WS_CTRL_IOBJ:{}", wso);
		if (wso != null || actionName == null || actionName.length() == 0) {
			md = defaultAction(webInput);
		} else {
			MethodEx method = ControllerHelper.getActionMethod(
					webInput.getControllerName(), actionName, this,
					WebInput.class);
			try {
				md = (ModelData) method.getMethod().invoke(this, webInput);
			} catch (Exception e) {
				throw new ControllerException(e);
			}
		}
		if (md == null || outputStream == null) {
			throw new ControllerException(
					"service return is null,service must have data to return!");
		}
		String ecode = webInput.getCharacterEncoding();
		if (md.getDataType().equals(ModelData.DataType.XML)) {
			XstreamCreator.getInstance().toXml(outputStream, md, ecode);
		} else if (md.getDataType().equals(ModelData.DataType.JSON)) {
			String jflag = AppProperties.get("web_ws_jsonProcessor", "Jackson");
			if (jflag.equalsIgnoreCase("XStream")) {
				XstreamCreator.getInstance().toJSONXtream(outputStream, md,
						ecode);
			} else {
				JacksonCreator.getInstance().toJSONJackson(outputStream, md,
						ecode);
			}
		} else {
			dealOtherCase(webInput, outputStream, md, ecode);
		}
	}

	private void dealOtherCase(WebInput webInput, OutputStream outputStream,
			final ModelData md, String ecode) throws ControllerException {
		String selfDef = webInput.getParameter("$dataFormat", "");
		if (selfDef.equalsIgnoreCase("xml")) {
			XstreamCreator.getInstance().toXml(outputStream, md, ecode);
		} else if (selfDef.equalsIgnoreCase("json")) {
			String jflag = AppProperties.get("web_ws_jsonProcessor", "Jackson");
			if (jflag.equalsIgnoreCase("XStream")) {
				XstreamCreator.getInstance().toJSONXtream(outputStream, md,
						ecode);
			} else {
				JacksonCreator.getInstance().toJSONJackson(outputStream, md,
						ecode);
			}
		} else if (selfDef.equals("")) {
			String defaultFormat = (String) webInput.getRequest().getAttribute(
					CommonUtil.WEB_SERVICE_DATA_DEFAULT_FORMAT);
			if (defaultFormat.equalsIgnoreCase("xml")) {
				XstreamCreator.getInstance().toXml(outputStream, md, ecode);
			} else if (defaultFormat.equalsIgnoreCase("json")) {
				String jflag = AppProperties.get("web_ws_jsonProcessor",
						"Jackson");
				if (jflag.equalsIgnoreCase("XStream")) {
					XstreamCreator.getInstance().toJSONXtream(outputStream, md,
							ecode);
				} else {
					JacksonCreator.getInstance().toJSONJackson(outputStream,
							md, ecode);
				}
			} else {
				throw new ControllerException("not support [" + defaultFormat
						+ "] format yet!");
			}
		} else {
			throw new ControllerException("not support [" + selfDef
					+ "] format yet!");
		}
	}

	/**
	 * 默认执行动作（若$action没有设置，则会执行此方法）
	 * 
	 * @param webInput
	 * @return ModelData--返回给客户端的数据对象
	 * @throws ControllerException
	 */
	public abstract ModelData defaultAction(WebInput webInput)
			throws ControllerException;
}
