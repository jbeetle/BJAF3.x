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

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * <p>
 * Title: J2EE框架核心工具包
 * </p>
 * 
 * <p>
 * Description:SMTP服务器信息类
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
public class SmtpServerInfo {
	// ----------------------------------------------------------------
	// construct

	/**
	 * SmtpServer default constructor.
	 */
	public SmtpServerInfo() {
	}

	/**
	 * SMTP server defined with its host.
	 * 
	 * @param host
	 *            SMTP host address
	 */
	public SmtpServerInfo(String host) {
		this.host = host;
	}

	/**
	 * SMTP server defined with its host and authenitification.
	 * 
	 * @param host
	 *            SMTP host address
	 */
	public SmtpServerInfo(String host, String username, String password) {
		this.host = host;
		this.username = username;
		this.password = password;
	}

	// ---------------------------------------------------------------- data

	private String host;

	/**
	 * Sets SMTP host address.
	 * 
	 * @param host
	 *            SMTP host address
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Returns SMTP host address.
	 * 
	 * @return SMTP host address
	 */
	public String getHost() {
		return host;
	}

	private String username;

	/**
	 * Sets SMTP authentication username. If username is not set, no
	 * authentification is needed.
	 * 
	 * @param username
	 *            Sets SMTP authentication username.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Returns SMTP authentification username
	 * 
	 * @return authentification SMTP username
	 */
	public String getUsername() {
		return username;
	}

	private String password;

	/**
	 * Sets SMTP authentication password.
	 * 
	 * @param password
	 *            SMTP authentication password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Returns SMTP authentication password.
	 * 
	 * @return SMTP authentication password
	 */
	public String getPassword() {
		return password;
	}

	public SmtpAuthenticator getAuthenticator() {
		return new SmtpAuthenticator(this.username, this.password);
	}

	static class SmtpAuthenticator extends Authenticator {
		private String username;
		private String password;

		public SmtpAuthenticator(String username, String password) {
			super();
			this.username = username;
			this.password = password;
		}

		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username, password);
		}

	}
}
