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

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Locale;

public class ResponseContent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient ByteArrayOutputStream bout; // = new
													// ByteArrayOutputStream(1000);

	private Locale locale;

	private String contentEncoding;

	private String contentType = null;

	private byte[] content = null;

	private long lastModified = -1;

	public ResponseContent() {
		bout = new ByteArrayOutputStream(1000);
		// contentType = "text/html; charset=" +
		// System.getProperty("file.encoding");
		// contentEncoding = System.getProperty("file.encoding");
		// locale = Locale.getDefault();
	}

	/**
	 * Set the content type. We capture this so that when we serve this data
	 * from cache, we can set the correct content type on the response.
	 */
	public void setContentType(String value) {
		contentType = value;
	}

	public String getContentEncoding() {
		return contentEncoding;
	}

	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long value) {
		lastModified = value;
	}

	/**
	 * Set the Locale. We capture this so that when we serve this data from
	 * cache, we can set the correct locale on the response.
	 */
	public void setLocale(Locale value) {
		locale = value;
	}

	/**
	 * Get an output stream. This is used by the
	 * {@link SplitServletOutputStream} to capture the original (uncached)
	 * response into a byte array.
	 */
	public OutputStream getOutputStream() {
		return bout;
	}

	/**
	 * Gets the size of this cached content.
	 * 
	 * @return The size of the content, in bytes. If no content exists, this
	 *         method returns <code>-1</code>.
	 */
	public int getSize() {
		return (content != null) ? content.length : (-1);
	}

	/**
	 * Called once the response has been written in its entirety. This method
	 * commits the response output stream by converting the output stream into a
	 * byte array.
	 */
	public void commit() {
		/*
		 * System.out.println("--------->>>");
		 * System.out.println(bout.toString()); try {
		 * System.out.println(bout.toString("8859_1")); } catch
		 * (UnsupportedEncodingException ex) { ex.printStackTrace(); } String
		 * a=bout.toString(); System.out.println("--------->>>");
		 * 
		 * try { content = a.getBytes("8859_1"); } catch
		 * (UnsupportedEncodingException ex1) { }
		 */
		content = bout.toByteArray();
	}

	/**
	 * Writes this cached data out to the supplied <code>ServletResponse</code>.
	 * 
	 * @param response
	 *            The servlet response to output the cached content to.
	 * @throws IOException
	 */
	public void writeTo(ServletResponse response) throws IOException {
		// Send the content type and data to this response
		if (contentType != null) {
			response.setContentType(contentType);
		}
		if ((lastModified != -1) && (response instanceof HttpServletResponse)) {
			((HttpServletResponse) response).setDateHeader(
					ControllerCacheFilter.HEADER_LAST_MODIFIED, lastModified);
		}
		response.setContentLength(content.length);
		if (locale != null) {
			response.setLocale(locale);
		} else {
			locale = Locale.getDefault();
			response.setLocale(locale);
		}
		OutputStream out = new BufferedOutputStream(response.getOutputStream());
		out.write(content);
		out.flush();
		out.close(); // do it 2006-3-10
	}
}
