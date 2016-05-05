package com.beetle.framework.web.cache.imp;

import com.beetle.framework.AppRuntimeException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ServletCacheAdministrator {
	private final static String FILE_SEPARATOR = "/";
	private final static char FILE_SEPARATOR_CHAR = FILE_SEPARATOR.charAt(0);
	private static final String m_strBase64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	private static ServletCacheAdministrator instance = null;
	private transient ServletContext context;
	private final static String APP_CACHE_KEY = "ServletCacheAdministrator_App_20090521";
	private final static String SESSION_CACHE_KEY = "ServletCacheAdministrator_Session_20090521";

	private ServletCacheAdministrator(ServletContext context) {
		this.context = context;

	}

	public String generateEntryKey(String key, HttpServletRequest request,
			int scope) {
		StringBuffer cBuffer = new StringBuffer(30);
		if (key != null) {
			cBuffer.append(FILE_SEPARATOR).append(key);
		} else {
			String generatedKey = request.getRequestURI();

			if (generatedKey.charAt(0) != FILE_SEPARATOR_CHAR) {
				cBuffer.append(FILE_SEPARATOR_CHAR);
			}

			cBuffer.append(generatedKey);
			cBuffer.append("_").append(request.getMethod()).append("_");

			generatedKey = getSortedQueryString(request);

			if (generatedKey != null) {
				try {
					java.security.MessageDigest digest = java.security.MessageDigest
							.getInstance("MD5");
					byte[] b = digest.digest(generatedKey.getBytes());
					cBuffer.append('_');

					// Base64 encoding allows for unwanted slash characters.
					cBuffer.append(toBase64(b).replace('/', '_'));
				} catch (Exception e) {
					// Ignore query string
					e.printStackTrace();
				}
			}
		}
		return cBuffer.toString();
	}

	private static String toBase64(byte[] aValue) {
		int byte1;
		int byte2;
		int byte3;
		int iByteLen = aValue.length;
		StringBuffer tt = new StringBuffer();

		for (int i = 0; i < iByteLen; i += 3) {
			boolean bByte2 = (i + 1) < iByteLen;
			boolean bByte3 = (i + 2) < iByteLen;
			byte1 = aValue[i] & 0xFF;
			byte2 = (bByte2) ? (aValue[i + 1] & 0xFF) : 0;
			byte3 = (bByte3) ? (aValue[i + 2] & 0xFF) : 0;

			tt.append(m_strBase64Chars.charAt(byte1 / 4));
			tt.append(m_strBase64Chars.charAt((byte2 / 16)
					+ ((byte1 & 0x3) * 16)));
			tt.append(((bByte2) ? m_strBase64Chars.charAt((byte3 / 64)
					+ ((byte2 & 0xF) * 4)) : '='));
			tt.append(((bByte3) ? m_strBase64Chars.charAt(byte3 & 0x3F) : '='));
		}

		return tt.toString();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String getSortedQueryString(HttpServletRequest request) {
		Map paramMap = request.getParameterMap();
		if (paramMap.isEmpty()) {
			return null;
		}
		Set paramSet = new TreeMap(paramMap).entrySet();
		StringBuffer buf = new StringBuffer();
		boolean first = true;
		for (Iterator it = paramSet.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String[] values = (String[]) entry.getValue();
			for (int i = 0; i < values.length; i++) {
				String key = (String) entry.getKey();
				if ((key.length() != 10) || !"jsessionid".equals(key)) {
					if (first) {
						first = false;
					} else {
						buf.append('&');
					}
					buf.append(key).append('=').append(values[i]);
				}
			}
		}
		// We get a 0 length buffer if the only parameter was a jsessionid
		if (buf.length() == 0) {
			return null;
		} else {
			return buf.toString();
		}
	}

	public ServletCache getCache(HttpServletRequest request, int scope) {
		if (scope == PageContext.APPLICATION_SCOPE) {
			return getAppScopeCache(context);
		}
		if (scope == PageContext.SESSION_SCOPE) {
			return getSessionScopeCache(request.getSession(false));
		}
		throw new AppRuntimeException("The supplied scope value of " + scope
				+ " is invalid.");
	}

	private ServletCache getAppScopeCache(ServletContext context) {
		ServletCache cache;
		Object obj = context.getAttribute(APP_CACHE_KEY);
		if (obj == null) {
			cache = ServletCache.getAppServletCache("webRequestAppCache",
					PageContext.APPLICATION_SCOPE);
			context.setAttribute(APP_CACHE_KEY, cache);
		} else {
			cache = (ServletCache) obj;
		}
		return cache;
	}

	private ServletCache getSessionScopeCache(HttpSession session) {
		if (null == session) {
			return null;
		}
		ServletCache cache;
		Object obj = session.getAttribute(SESSION_CACHE_KEY);
		if (null == obj) {
			cache = ServletCache.getSessionServletCache(
					"webRequestSessionCache", PageContext.SESSION_SCOPE);
			// cache = createCache(PageContext.SESSION_SCOPE, session.getId());
			session.setAttribute(SESSION_CACHE_KEY, cache);
		} else {
			cache = (ServletCache) obj;
		}
		return cache;
	}

	public static ServletCacheAdministrator getInstance(ServletContext context) {
		if (instance == null) {
			instance = new ServletCacheAdministrator(context);
		}
		return instance;
	}
}
