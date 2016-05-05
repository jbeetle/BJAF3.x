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

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.*;

/**
 * <p>
 * Title: J2EE框架核心工具包

 * </p>
 * 
 * <p>
 * Description:邮件发送类
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
public class SendLetter {
	private static SmtpServerInfo defaultSmtpServer = null;

	/**
	 * Sets default SMTP server.
	 * 
	 * @param server
	 *            default SMTP server
	 */
	public static void setDefaultSmtpServer(SmtpServerInfo server) {
		defaultSmtpServer = server;
	}

	/**
	 * Sends an email through default SMTP server.
	 * 
	 * @param email
	 *            email
	 * 
	 * @exception MessagingException
	 */
	public static void send(Letter letter) throws MessagingException {
		send(letter, null);
	}

	/**
	 * Sends an email through SMTP server.
	 * 
	 * @param email
	 *            email
	 * @param smtpServer
	 *            SMTP server or null for default one
	 * 
	 * @exception MessagingException
	 */
	public static void send(Letter letter, SmtpServerInfo smtpServer)
			throws MessagingException {
		if (smtpServer == null) {
			smtpServer = defaultSmtpServer;
		}

		Properties props = new Properties();
		props.put("mail.smtp.host", smtpServer.getHost());
		Authenticator auth = null;
		if (smtpServer.getUsername() != null) {
			props.put("mail.smtp.auth", "true");
			// auth = new SmtpAuthenticator(smtpServer.getUsername(),
			// smtpServer.getPassword());
			auth = smtpServer.getAuthenticator();
		}
		Session session = Session.getInstance(props, auth);

		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(letter.getFrom()));

		int totalTo = letter.getTo().length;
		InternetAddress[] address = new InternetAddress[totalTo];
		for (int i = 0; i < totalTo; i++) {
			address[i] = new InternetAddress(letter.getTo()[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, address);

		if (letter.getCc() != null) {
			int totalCc = letter.getCc().length;
			address = new InternetAddress[totalCc];
			for (int i = 0; i < totalCc; i++) {
				address[i] = new InternetAddress(letter.getCc()[i]);
			}
			msg.setRecipients(Message.RecipientType.CC, address);
		}
		if (letter.getBcc() != null) {
			int totalBcc = letter.getBcc().length;
			address = new InternetAddress[totalBcc];
			for (int i = 0; i < totalBcc; i++) {
				address[i] = new InternetAddress(letter.getBcc()[i]);
			}
			msg.setRecipients(Message.RecipientType.BCC, address);
		}

		msg.setSubject(letter.getSubject());
		Date date = letter.getSentDate();
		if (date == null) {
			date = new Date();
		}
		msg.setSentDate(date);
		HashMap<?, ?> headers = letter.getHeaders();
		// Iterator it = headers.keySet().iterator();
		Iterator<?> it = headers.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry kv = (Map.Entry) it.next();
			msg.setHeader((String) kv.getKey(), (String) kv.getValue());
		}

		MimeBodyPart messageData = new MimeBodyPart();

		if (letter.isHtmlMessage()) {
			// messageData.setContentLanguage();
			String charset = System.getProperty("file.encoding");
			messageData.setContent(letter.getMessage(), "text/html; charset=\""
					+ charset + "\"");
		} else {
			// messageBodyPart.setContent(letter.getMessage(),
			// "text/plain; charset=\"Windows-1250\";");
			// messageBodyPart.setText(letter.getMessage(), "Windows-1250");
			messageData.setText(letter.getMessage());
		}

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageData);

		int totalAttachments = letter.getTotalAttachments();
		for (int i = 0; i < totalAttachments; i++) {
			multipart.addBodyPart(letter.getAttachmentBodyPart(i));
		}

		msg.setContent(multipart);
		Transport.send(msg);
	}

}
