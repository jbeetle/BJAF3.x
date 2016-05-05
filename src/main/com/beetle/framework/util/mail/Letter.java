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
package com.beetle.framework.util.mail;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.FileTypeMap;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Title: J2EE框架核心工具包

 * </p>
 * 
 * <p>
 * Description:邮件构造类
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
 * @author 余浩东（hdyu@beetlesoft.net）

 * @version 1.0
 */
public class Letter {

	private String from;

	/**
	 * Sets the FROM address
	 * 
	 * @param from
	 *            FROM addess
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * Returns FROM address.
	 * 
	 * @return FROM address
	 */
	public String getFrom() {
		return from;
	}

	private String[] to;

	/**
	 * Sets single TO address.
	 * 
	 * @param to
	 *            single TO address
	 */
	public void setTo(String to) {
		this.to = new String[1];
		this.to[0] = to;
	}

	/**
	 * Sets multiple TO addresses.
	 * 
	 * @param to
	 *            array of TO addresses
	 */
	public void setTo(String[] to) {
		this.to = to;
	}

	/**
	 * Returns the array of TO addresses.
	 * 
	 * @return array of TO addresses
	 */
	public String[] getTo() {
		return to;
	}

	private String[] cc;

	/**
	 * Sets single CC address
	 * 
	 * @param cc
	 *            single CC address
	 */
	public void setCc(String cc) {
		this.cc = new String[1];
		this.cc[0] = cc;
	}

	/**
	 * Sets multiple CC addresses.
	 * 
	 * @param cc
	 *            array of CC address.
	 */
	public void setCc(String[] cc) {
		this.cc = cc;
	}

	/**
	 * Returns array of CC addresses.
	 * 
	 * @return array of CC addresses
	 */
	public String[] getCc() {
		return cc;
	}

	private String[] bcc;

	/**
	 * Sets single BCC address.
	 * 
	 * @param bcc
	 *            single BCC address
	 */
	public void setBcc(String bcc) {
		this.bcc = new String[1];
		this.bcc[0] = bcc;
	}

	/**
	 * Sets multiple BCC addresses.
	 * 
	 * @param bcc
	 *            array of BCC address.
	 */
	public void setBcc(String[] bcc) {
		this.bcc = bcc;
	}

	/**
	 * Returns array of BCC addresses.
	 * 
	 * @return array of BCC addresses
	 */
	public String[] getBcc() {
		return bcc;
	}

	// ---------------------------------------------------------------- content

	private String subject;

	/**
	 * Sets message subject.
	 * 
	 * @param subject
	 *            subject of a message
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Returns message subject.
	 * 
	 * @return message subject
	 */
	public String getSubject() {
		return this.subject;
	}

	private boolean htmlMessage = false;

	private String message;

	/**
	 * Sets plain message text.
	 * 
	 * @param message
	 *            plain message text
	 */
	public void setMessage(String message) {
		this.htmlMessage = false;
		this.message = message;
	}

	/**
	 * Returns message text, either plain or HTML.
	 * 
	 * @return message text
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Sets HTML message text.
	 * 
	 * @param htmlMessage
	 *            HTML message text
	 */
	public void setHtmlMessage(String htmlMessage) {
		this.htmlMessage = true;
		this.message = htmlMessage;
	}

	/**
	 * Returns true if message is HTML, otherwise false.
	 * 
	 * @return true if message is HTML, otherwise false
	 */
	public boolean isHtmlMessage() {
		return this.htmlMessage;
	}

	// ----------------------------------------------------------------
	// attachments

	private ArrayList<MimeBodyPart> attachments = new ArrayList<MimeBodyPart>();

	/**
	 * Returns total number of attachements.
	 * 
	 * @return total number of attachements
	 */
	public int getTotalAttachments() {
		return attachments.size();
	}

	/**
	 * Returns one attachment body part, for javamail usage.
	 * 
	 * @param i
	 *            index of attachement
	 * 
	 * @return attachment body part
	 */
	public MimeBodyPart getAttachmentBodyPart(int i) {
		return (MimeBodyPart) attachments.get(i);
	}

	/**
	 * Adds a generic attachement.
	 * 
	 * @param fileName
	 *            file name of attachment
	 * @param dh
	 *            DataHandler
	 * 
	 * @exception MessagingException
	 */
	public void addAttachment(String fileName, DataHandler dh)
			throws MessagingException {
		MimeBodyPart attBodyPart = new MimeBodyPart();
		attBodyPart.setFileName(fileName);
		attBodyPart.setDataHandler(dh);
		attachments.add(attBodyPart);
	}

	/**
	 * Adds a HTML attachment.
	 * 
	 * @param fileName
	 *            attachment file name
	 * @param data
	 *            HTML data
	 * 
	 * @exception MessagingException
	 */
	public void addAttachment(String fileName, String data)
			throws MessagingException {
		DataHandler dh = new DataHandler(new ByteArrayDataSource(data,
				"text/html"));
		addAttachment(fileName, dh);
	}

	/**
	 * Adds an existing file as attachment.
	 * 
	 * @param fileName
	 *            local name of a file to attach
	 * 
	 * @exception MessagingException
	 */
	public void addAttachment(String fileName) throws MessagingException {
		FileDataSource fds = new FileDataSource(fileName);
		addAttachment(fds.getName(), new DataHandler(fds));
	}

	// ---------------------------------------------------------------- headers

	private HashMap<String, String> headers = new HashMap<String, String>();

	/**
	 * Return all headers as a HashMap
	 * 
	 * @return defined headers
	 */
	HashMap<String, String> getHeaders() {
		return headers;
	}

	/**
	 * Add single header.
	 * 
	 * @param name
	 *            header name
	 * @param value
	 *            header value
	 */
	public void addHeader(String name, String value) {
		headers.put(name, value);
	}

	/**
	 * Add map of headers.
	 * 
	 * @param map
	 *            headers map
	 */
	public void addHeaders(Map<String, String> map) {
		headers.putAll(map);

	}

	// ---------------------------------------------------------------- date

	private Date sentDate = null;

	/**
	 * Sets e-mails sent date. If input parameter is <code>null</code> then date
	 * will be when email is physically sent.
	 * 
	 * @param date
	 *            sent date value
	 */
	public void setSentDate(Date date) {
		sentDate = date;
	}

	/**
	 * Sets current date as e-mails sent date.
	 */
	public void setSentDate() {
		sentDate = new Date();
	}

	/**
	 * Returns e-mails sent date. If return value is <code>null</code> then date
	 * will be set during the process of sending.
	 * 
	 * @return emails sent date or null if it will be set later.
	 */
	public Date getSentDate() {
		return sentDate;
	}

	 static class ByteArrayDataSource implements DataSource {

		private byte[] data; // data
		private String type; // content-type

		/**
		 * Create a datasource from a File. If the Content-Type parameter is
		 * null, the type will be derived from the filename extension.
		 * 
		 * @param f
		 *            File object
		 * @param type
		 *            Content-Type
		 */
		public ByteArrayDataSource(File f, String type) throws IOException {
			this(new FileInputStream(f), type);
			if (this.type == null) {
				this.type = FileTypeMap.getDefaultFileTypeMap().getContentType(
						f);
			}
		}

		/**
		 * Create a datasource from an input stream.
		 * 
		 * @param is
		 *            InputStream
		 * @param type
		 *            Content-Type
		 */
		public ByteArrayDataSource(InputStream is, String type)
				throws IOException {
			this.type = type;

			ByteArrayOutputStream os = new ByteArrayOutputStream(4096);

			byte buf[] = new byte[4096];
			int len;
			while (true) {
				len = is.read(buf);
				if (len < 0) {
					break;
				}
				os.write(buf, 0, len);
			}
			data = os.toByteArray();
		}

		/**
		 * Create a datasource from a byte array.
		 * 
		 * @param data
		 *            byte array
		 * @param type
		 *            Content-Type
		 */
		public ByteArrayDataSource(byte[] data, String type) {
			this.type = type;
			this.data = data;
		}

		/**
		 * Create a datasource from a String. This method defaults to a String
		 * encoding of iso-8859-1. For a different encoding, specify a Mime
		 * "charset" in the Content-Type parameter.
		 * 
		 * @param data
		 *            byte array
		 * @param type
		 *            Content-Type
		 */
		public ByteArrayDataSource(String data, String type) {
			this.type = type;
			try {
				this.data = data.getBytes("ISO-8859-1");
			} catch (UnsupportedEncodingException uex) {
			}
		}

		/**
		 * Return an InputStream to read the content.
		 * 
		 * @return an InputStream with the content
		 */
		public InputStream getInputStream() throws IOException {
			if (data == null) {
				throw new IOException("No data.");
			}
			return new ByteArrayInputStream(data);
		}

		/**
		 * This DataSource cannot return an OutputStream, so this method is not
		 * implemented.
		 */
		public OutputStream getOutputStream() throws IOException {
			throw new IOException("getOutputStream() not supported.");
		}

		/**
		 * Get the content type.
		 * 
		 * @return Content-Type string
		 */
		public String getContentType() {
			return type;
		}

		/**
		 * Set the content type.
		 * 
		 * @param type
		 *            Content-Type string
		 */
		public void setContentType(String type) {
			this.type = type;
		}

		/**
		 * getName() is not implemented.
		 */
		public String getName() {
			return "";
		}

		/**
		 * Write the content to an OutputStream.
		 * 
		 * @param os
		 *            OutputStream to write the entire content to
		 */
		public void writeTo(OutputStream os) throws IOException {
			os.write(data);
		}

		/**
		 * Return the content as a byte array.
		 * 
		 * @return byte array with the content
		 */
		public byte[] toByteArray() {
			return data;
		}

		/**
		 * Return the number of bytes in the content.
		 * 
		 * @return size of the byte array, or -1 if not set.
		 */
		public int getSize() {
			if (data == null) {
				return -1;
			} else {
				return data.length;
			}
		}

		/**
		 * Return the content as a String. The Content-Type "charset" parameter
		 * will be used to determine the encoding, and if that's not available
		 * or invalid, "iso-8859-1".
		 * 
		 * @return a String with the content
		 */
		public String getText() {
			try {
				return new String(data, type);
			} catch (UnsupportedEncodingException uex) {
				try {
					return new String(data, "ISO-8859-1");
				} catch (UnsupportedEncodingException uex1) {
					return null;
				}
			}
		}
	}

}
