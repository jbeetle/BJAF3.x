package com.beetle.framework.web.common;

import com.beetle.framework.web.controller.ControllerException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * <p>
 * Title: BeetleWeb
 * </p>
 * 
 * <p>
 * Description: Web������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: �׿ǳ����
 * </p>
 * 
 * @author ��ƶ�(hdyu@beetlesoft.net)
 * @version 1.0
 */
public class WebUtil {
	public static String getDecodeValueByKeyName(HttpServletRequest request,
			String keyName) {
		String qs = decodeURL(request.getQueryString(),
				(String) request.getAttribute("WEB_ENCODE_CHARSET"));
		StringTokenizer st = new StringTokenizer(qs, "&");
		HashMap<String, String> m = new HashMap<String, String>();
		while (st.hasMoreTokens()) {
			String b = st.nextToken();
			int i = b.indexOf("=");
			String key = b.substring(0, i);
			String value = b.substring(i + 1);
			m.put(key, value);
		}
		String v = (String) m.get(keyName);
		m.clear();
		return v;
	}

	public static String decodeURL(String url, String charset) {
		try {
			return URLDecoder.decode(url, charset);
		} catch (UnsupportedEncodingException u) {
			u.printStackTrace();
			return url;
		}
	}

	public static String encodeURL(String url, String charset) {
		try {
			return URLEncoder.encode(url, charset);
		} catch (UnsupportedEncodingException u) {
			u.printStackTrace();
			return url;
		}
	}

	public static Object getParameter(String paramterName,
			HttpServletRequest request) {
		Object o = request.getAttribute(paramterName);
		if (o == null) {
			o = request.getParameter(paramterName);
		}
		return o;
	}

	public static Object getValueFromSession(String sessionName,
			HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			return session.getAttribute(sessionName);
		} else {
			return null;
		}
	}

	private static String ReplaceChar(String aSour, char aFind, String aRep) {
		int aLength = aSour.length();
		if (aLength == 0) {
			return aSour;
		}
		StringBuffer aStrBuf = new StringBuffer();
		for (int i = 0; i < aLength; i++) {
			char aChar = aSour.charAt(i);
			if (aChar == aFind) {
				aStrBuf.append(aRep);
			} else {
				aStrBuf.append(aChar);
			}
		}
		return aStrBuf.toString();
	}

	public static String dealTags(String pStr) {
		String aStr = ReplaceChar(pStr, '<', "&lt;");
		aStr = ReplaceChar(aStr, '>', "&gt;");
		return aStr;
	}

	public static String returnToBR(String pStr) {
		String aStr = ReplaceChar(pStr, '\n', "<br>");
		return aStr;
	}

	public static String dealSpace(String pStr) {
		String aStr = ReplaceChar(pStr, ' ', "&nbsp;");
		return aStr;
	}

	public static String doPre(String pStr) {
		String aStr = returnToBR(dealSpace(dealTags(pStr)));
		return aStr;
	}

	public static String decodeURL(String string) {
		return decodeURL(string, System.getProperty("file.encoding"));
		// return URLDecoder.decode(string);
	}

	public static String encodeURL(String string) {
		// return encodeUrl(string, "ISO-8859-1");
		// return URLEncoder.encode(string);
		return encodeURL(string, System.getProperty("file.encoding"));
	}

	public static class ExceptionInfo {
		public int errCode;

		public String errMessage;

		public String stackTraceInfo;
	}

	public static ExceptionInfo analyseException(Throwable e) {
		ExceptionInfo ei = new ExceptionInfo();
		ei.errCode = -1000;
		if (e instanceof ControllerException) {
			ei.errCode = ((ControllerException) e).getErrCode();
			ei.errMessage = e.getMessage();
		}
		java.io.CharArrayWriter cw = new java.io.CharArrayWriter();
		java.io.PrintWriter pw = new java.io.PrintWriter(cw, true);
		try {
			e.printStackTrace(pw);
			ei.stackTraceInfo = cw.toString();
			return ei;
		} finally {
			cw.close();
			pw.close();
			cw = null;
			pw = null;
		}
	}

}
