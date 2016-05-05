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

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SplitServletOutputStream extends ServletOutputStream {
	OutputStream captureStream = null;
	OutputStream passThroughStream = null;

	/**
	 * Constructs a split output stream that both captures and passes through
	 * the servlet response.
	 * 
	 * @param captureStream
	 *            The stream that will be used to capture the data.
	 * @param passThroughStream
	 *            The pass-through <code>ServletOutputStream</code> that will
	 *            write the response to the client as originally intended.
	 */
	public SplitServletOutputStream(OutputStream captureStream,
			OutputStream passThroughStream) {
		this.captureStream = captureStream;
		this.passThroughStream = passThroughStream;
	}

	/**
	 * Writes the incoming data to both the output streams.
	 * 
	 * @param value
	 *            The int data to write.
	 * @throws IOException
	 */
	public void write(int value) throws IOException {
		captureStream.write(value);
		passThroughStream.write(value);
	}

	/**
	 * Writes the incoming data to both the output streams.
	 * 
	 * @param value
	 *            The bytes to write to the streams.
	 * @throws IOException
	 */
	public void write(byte[] value) throws IOException {
		captureStream.write(value);
		passThroughStream.write(value);
	}

	/**
	 * Writes the incoming data to both the output streams.
	 * 
	 * @param b
	 *            The bytes to write out to the streams.
	 * @param off
	 *            The offset into the byte data where writing should begin.
	 * @param len
	 *            The number of bytes to write.
	 * @throws IOException
	 */
	public void write(byte[] b, int off, int len) throws IOException {
		captureStream.write(b, off, len);
		passThroughStream.write(b, off, len);
	}
}
