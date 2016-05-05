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
package com.beetle.framework.web.cache.imp;

import com.beetle.framework.web.cache.ControllerCacheFilter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;

public class CacheHttpServletResponseWrapper extends HttpServletResponseWrapper {

	/**
	 * We cache the printWriter so we can maintain a single instance of it no
	 * matter how many times it is requested.
	 */
	private PrintWriter cachedWriter;
	private ResponseContent result = null;
	private SplitServletOutputStream cacheOut = null;
	private int status = SC_OK;

	/**
	 * Constructor
	 * 
	 * @param response
	 *            The servlet response
	 */
	public CacheHttpServletResponseWrapper(HttpServletResponse response) {
		super(response);
		result = new ResponseContent();
	}

	/**
	 * Get a response content
	 * 
	 * @return The content
	 */
	public ResponseContent getContent() {
		// Create the byte array
		result.commit();

		// Return the result from this response
		return result;
	}

	/**
	 * Set the content type
	 * 
	 * @param value
	 *            The content type
	 */
	public void setContentType(String value) {
		super.setContentType(value);
		result.setContentType(value);
	}

	/**
	 * Set the date of a header
	 * 
	 * @param name
	 *            The header name
	 * @param value
	 *            The date
	 */
	public void setDateHeader(String name, long value) {

		if (ControllerCacheFilter.HEADER_LAST_MODIFIED.equalsIgnoreCase(name)) {
			result.setLastModified(value);
		}

		super.setDateHeader(name, value);
	}

	/**
	 * Set a header field
	 * 
	 * @param name
	 *            The header name
	 * @param value
	 *            The header value
	 */
	public void setHeader(String name, String value) {
		if (ControllerCacheFilter.HEADER_CONTENT_TYPE.equalsIgnoreCase(name)) {
			result.setContentType(value);
		}

		if (ControllerCacheFilter.HEADER_CONTENT_ENCODING
				.equalsIgnoreCase(name)) {
			result.setContentEncoding(value);
		}

		super.setHeader(name, value);
	}

	/**
	 * Add a header field
	 * 
	 * @param name
	 *            The header name
	 * @param value
	 *            The header value
	 */
	public void addHeader(String name, String value) {

		if (ControllerCacheFilter.HEADER_CONTENT_TYPE.equalsIgnoreCase(name)) {
			result.setContentType(value);
		}

		if (ControllerCacheFilter.HEADER_CONTENT_ENCODING
				.equalsIgnoreCase(name)) {
			result.setContentEncoding(value);
		}

		super.addHeader(name, value);
	}

	/**
	 * Set the int value of the header
	 * 
	 * @param name
	 *            The header name
	 * @param value
	 *            The int value
	 */
	public void setIntHeader(String name, int value) {

		super.setIntHeader(name, value);
	}

	/**
	 * We override this so we can catch the response status. Only responses with
	 * a status of 200 (<code>SC_OK</code>) will be cached.
	 */
	public void setStatus(int status) {
		super.setStatus(status);
		this.status = status;
	}

	/**
	 * We override this so we can catch the response status. Only responses with
	 * a status of 200 (<code>SC_OK</code>) will be cached.
	 */
	public void sendError(int status, String string) throws IOException {
		super.sendError(status, string);
		this.status = status;
	}

	/**
	 * We override this so we can catch the response status. Only responses with
	 * a status of 200 (<code>SC_OK</code>) will be cached.
	 */
	public void sendError(int status) throws IOException {
		super.sendError(status);
		this.status = status;
	}

	/**
	 * We override this so we can catch the response status. Only responses with
	 * a status of 200 (<code>SC_OK</code>) will be cached.
	 */
	@SuppressWarnings("deprecation")
	public void setStatus(int status, String string) {
		super.setStatus(status, string);
		this.status = status;
	}

	public void sendRedirect(String location) throws IOException {
		this.status = SC_MOVED_TEMPORARILY;
		super.sendRedirect(location);
	}

	/**
	 * Retrieves the captured HttpResponse status.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Set the locale
	 * 
	 * @param value
	 *            The locale
	 */
	public void setLocale(Locale value) {
		super.setLocale(value);
		result.setLocale(value);
	}

	/**
	 * Get an output stream
	 * 
	 * @throws IOException
	 */
	public ServletOutputStream getOutputStream() throws IOException {
		// Pass this faked servlet output stream that captures what is sent
		if (cacheOut == null) {
			cacheOut = new SplitServletOutputStream(result.getOutputStream(),
					super.getOutputStream());
		}

		return cacheOut;
	}

	/**
	 * Get a print writer
	 * 
	 * @throws IOException
	 */
	public PrintWriter getWriter() throws IOException {
		if (cachedWriter == null) {
			String encoding = getCharacterEncoding();
			if (encoding != null) {
				cachedWriter = new PrintWriter(new OutputStreamWriter(
						getOutputStream(), encoding));
			} else { // using the default character encoding
				cachedWriter = new PrintWriter(new OutputStreamWriter(
						getOutputStream()));
			}
		}

		return cachedWriter;
	}

	public void flushBuffer() throws IOException {
		super.flushBuffer();

		if (cacheOut != null) {
			cacheOut.flush();
		}

		if (cachedWriter != null) {
			cachedWriter.flush();
		}
	}
}
