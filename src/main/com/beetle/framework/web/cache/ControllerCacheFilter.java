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
package com.beetle.framework.web.cache;

import com.beetle.framework.log.AppLogger;
import com.beetle.framework.web.cache.imp.*;
import com.beetle.framework.web.common.WebConst;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.Map;

public class ControllerCacheFilter extends HttpServlet implements Filter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Header
	public static final String HEADER_LAST_MODIFIED = "Last-Modified";

	public static final String HEADER_CONTENT_TYPE = "Content-Type";

	public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";

	public static final String HEADER_EXPIRES = "Expires";

	public static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";

	public static final String HEADER_CACHE_CONTROL = "Cache-control";

	public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";

	private static boolean initedFlag = false;
	private static final String classname = ControllerCacheFilter.class
			.getName();
	private transient ServletCacheAdministrator admin = null;

	private String characterEncoding = "GBK";

	private Map<String, CacheAttr> cacheUrls = null;

	private final static String analysePath(String path) {
		int i = path.lastIndexOf('/');
		if (i == -1) {
			return path;
		} else {
			return path.substring(i + 1);
		}
	}

	// Process the request/response pair
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		if (this.cacheUrls.isEmpty()) {
			filterChain.doFilter(request, response);
			return;
		}
		request.setCharacterEncoding(this.characterEncoding);
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String path = analysePath(httpRequest.getServletPath().trim()); // only
																		// name
		if (this.cacheUrls.containsKey(path)) {
			docache(request, response, filterChain, path);
		} else {
			filterChain.doFilter(request, response);
		}
	}

	private int toScope(String scopeString) {
		int cacheScope = PageContext.APPLICATION_SCOPE;
		if (scopeString.equals("session")) {
			cacheScope = PageContext.SESSION_SCOPE;
		} else if (scopeString.equals("application")) {
			cacheScope = PageContext.APPLICATION_SCOPE;
		} else if (scopeString.equals("request")) {
			cacheScope = PageContext.REQUEST_SCOPE;
		} else if (scopeString.equals("page")) {
			cacheScope = PageContext.PAGE_SCOPE;
		}
		return cacheScope;
	}

	private void docache(ServletRequest request, ServletResponse response,
			FilterChain filterChain, String urlpath) throws ServletException,
			IOException {
		AppLogger log = AppLogger.getInstance(classname);
		if (log.isDebugEnabled()) {
			log.debug("cache url:" + urlpath);
			log.debug(response.getCharacterEncoding());
		}
		CacheAttr attr = (CacheAttr) cacheUrls.get(urlpath);
		int cacheScope = toScope(attr.getScope());
		int time = attr.getTime(); // in sec
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		ServletCache cache = admin.getCache(httpRequest, cacheScope);
		if (cache == null) {
			return;
		}
		String key = admin.generateEntryKey(null, httpRequest, cacheScope);
		if (cache.getType() == PageContext.SESSION_SCOPE) {
			String sessionid = httpRequest.getSession().getId();// 此时肯定存在session
			key = "[" + sessionid + "]" + key;
		}
		try {
			ResponseContent respContent = (ResponseContent) cache.getFromCache(
					key, time);
			if (log.isDebugEnabled()) {
				log.debug("<cache>: Using cached entry for " + key);
				log.debug(response.getCharacterEncoding());
			}
			long clientLastModified = httpRequest
					.getDateHeader("If-Modified-Since"); // will return -1 if no
															// header...
			if ((clientLastModified != -1)
					&& (clientLastModified >= respContent.getLastModified())) {
				((HttpServletResponse) response)
						.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				return;
			}
			respContent.writeTo(response);
		} catch (NeedsRefreshException nre) {
			boolean updateSucceeded = false;
			try {
				if (log.isDebugEnabled()) {
					log.debug("<cache>: New cache entry, cache stale or cache scope flushed for "
							+ key);
				}
				CacheHttpServletResponseWrapper cacheResponse = new CacheHttpServletResponseWrapper(
						(HttpServletResponse) response);
				filterChain.doFilter(request, cacheResponse);
				cacheResponse.flushBuffer();
				// Only cache if the response was 200
				if (cacheResponse.getStatus() == HttpServletResponse.SC_OK) {
					// Store as the cache content the result of the response
					cache.putInCache(key, cacheResponse.getContent(), time);
					updateSucceeded = true;
				}
			} finally {
				if (!updateSucceeded) {
					cache.cancelUpdate(key);
				}
			}
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		if (!initedFlag) {
			initedFlag = true;
			this.characterEncoding = System.getProperty("file.encoding");
			cacheUrls = CacheConfig.getCacheURLs(filterConfig
					.getServletContext().getResourceAsStream(
							WebConst.WEB_CONTROLLER_FILENAME));
			admin = ServletCacheAdministrator.getInstance(filterConfig
					.getServletContext());
		}
	}
}
