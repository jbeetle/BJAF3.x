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
package com.beetle.framework.web.view;

import com.beetle.framework.web.common.CommonUtil;
import com.beetle.framework.web.common.WebUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Title: BeetleWeb
 * </p>
 * 
 * <p>
 * Description: 视图助手类
 * 
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: 甲壳虫软件
 * 
 * </p>
 * 
 * @author 余浩东(hdyu@beetlesoft.net)
 * @version 1.0
 */
public class ViewHelper {
	private HttpServletRequest request;

	public ViewHelper(HttpServletRequest request) {
		this.request = request;
		/*
		 * Object o = request.getAttribute("WEB_ENCODE_CHARSET"); if (o == null)
		 * { o = System.getProperty("file.encoding"); } try {
		 * this.request.setCharacterEncoding(o.toString()); } catch
		 * (UnsupportedEncodingException ex) { ex.printStackTrace(); }
		 */

	}

	/**
	 * 根据数据对象的标识获取数据对象
	 * 
	 * 
	 * @param dataId
	 *            String
	 * @return Object
	 */
	public Object getDataValue(String dataId) {
		return request.getAttribute(dataId);
	}

	public String getDataValueAsString(String dataId) {
		return (String) getDataValue(dataId);
	}

	public Integer getDataValueAsInteger(String dataId) {
		return (Integer) getDataValue(dataId);
	}

	public int getDataValueAsInt(String dataId) {
		return getDataValueAsInteger(dataId).intValue();
	}

	public long getDataValueAsLng(String dataId) {
		return getDataValueAsLong(dataId).longValue();
	}

	public Long getDataValueAsLong(String dataId) {
		return (Long) getDataValue(dataId);
	}

	public List<?> getDataValueAsList(String dataId) {
		return (List<?>) getDataValue(dataId);
	}

	public Map<?, ?> getDataValueAsMap(String dataId) {
		return (Map<?, ?>) getDataValue(dataId);
	}

	public Time getDataValueAsTime(String dataId) {
		return (Time) getDataValue(dataId);
	}

	public Double getDataValueAsDouble(String dataId) {
		return (Double) getDataValue(dataId);
	}

	public Float getDataValueAsFloat(String dataId) {
		return (Float) getDataValue(dataId);
	}

	public Character getDataValueAsCharacter(String dataId) {
		return (Character) getDataValue(dataId);
	}

	public char getDataValueAsChar(String dataId) {
		return getDataValueAsCharacter(dataId).charValue();
	}

	public float getDataValueAsFlt(String dataId) {
		return getDataValueAsFloat(dataId).floatValue();
	}

	public Boolean getDataValueAsBoolean(String dataId) {
		return (Boolean) getDataValue(dataId);
	}

	public boolean getDataValueAsBool(String dataId) {
		return getDataValueAsBoolean(dataId).booleanValue();
	}

	public double getDataValueAsDbl(String dataId) {
		return getDataValueAsDouble(dataId).doubleValue();
	}

	public java.util.Date getDataValueAsDate(String dataId) {
		return (java.util.Date) getDataValue(dataId);
	}

	public Timestamp getDataValueAsTimestamp(String dataId) {
		return (Timestamp) getDataValue(dataId);
	}

	public String dateFormat(java.util.Date date, String formatStr) {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				formatStr);
		return sdf.format(date);
	}

	public String doubleFormat(double dbnum, String formatStr) {
		DecimalFormat df = new DecimalFormat(formatStr);
		return df.format(dbnum);
	}

	public String floatFormat(float flnum, String formatStr) {
		DecimalFormat df = new DecimalFormat(formatStr);
		return df.format(flnum);
	}

	public Enumeration<?> getDataValueNames() {
		return request.getAttributeNames();
	}

	/**
	 * 是否存在Session
	 * 
	 * @return boolean
	 */
	public boolean existSession() {
		return request.getSession(false) != null;
	}

	/**
	 * 从request对象中返回页面参数
	 * 
	 * 
	 * @param paramName
	 *            String
	 * @return String
	 */
	public String getDataFromRequest(String paramName) {
		if (request.getMethod().toLowerCase().equals("get")) {
			String info = (String) request
					.getAttribute(CommonUtil.WEB_SERVER_INFO);
			if (info == null) {
				return request.getParameter(paramName);
			} else {
				if (info.indexOf("Tomcat") > 0) {
					String a = request.getParameter(paramName);
					try {
						if (a != null) {
							return new String(a.getBytes("8859_1"));
						} else {
							return a;
						}
					} catch (UnsupportedEncodingException ex) {
						return a;
					}
				}
			}
		}
		return request.getParameter(paramName);
	}

	/**
	 * 从Session获取对象
	 * 
	 * @param valueName
	 *            String
	 * @return Object
	 */
	public Object getDataFromSession(String valueName) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		} else {
			return session.getAttribute(valueName);
		}
	}

	/**
	 * 在异常发生时，获取异常错误代码
	 * 
	 * 如果错误代码不存在，返回为0
	 * 
	 * @return 错误代码(一般为负数)
	 */
	public int getErrCode() {
		WebUtil.ExceptionInfo ei = (WebUtil.ExceptionInfo) request
				.getAttribute(CommonUtil.WEB_EXCEPTION_INFO);
		return ei.errCode;
	}

	/**
	 * 在异常发生时，获取异常信息
	 * 
	 * @return
	 */
	public String getErrMessage() {
		WebUtil.ExceptionInfo ei = (WebUtil.ExceptionInfo) request
				.getAttribute(CommonUtil.WEB_EXCEPTION_INFO);
		return ei.errMessage;
	}

	/**
	 * 在异常发生时，获取异常堆信息
	 * 
	 * @return
	 */
	public String getErrStackTraceInfo() {
		WebUtil.ExceptionInfo ei = (WebUtil.ExceptionInfo) request
				.getAttribute(CommonUtil.WEB_EXCEPTION_INFO);
		return ei.stackTraceInfo;
	}
}
