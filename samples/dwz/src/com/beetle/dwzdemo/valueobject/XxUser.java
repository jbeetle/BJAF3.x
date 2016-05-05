package com.beetle.dwzdemo.valueobject;

import java.sql.Timestamp;

public class XxUser extends VOBase {

	private static final long serialVersionUID = 1L;
	private Timestamp birthday;
	private String passwd;
	private Integer sex;
	private String username;
	private String email;
	private Long userid;

	public XxUser() {
	}

	public Timestamp getBirthday() {
		return this.birthday;
	}

	public String getPasswd() {
		return this.passwd;
	}

	public Integer getSex() {
		return this.sex;
	}

	public String getUsername() {
		return this.username;
	}

	public String getEmail() {
		return this.email;
	}

	public Long getUserid() {
		return this.userid;
	}

	public void setBirthday(Timestamp birthday) {
		this.birthday = birthday;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	@Override
	public String toString() {
		return "XxUser [birthday=" + birthday + ", passwd=" + passwd + ", sex="
				+ sex + ", username=" + username + ", email=" + email
				+ ", userid=" + userid + "]";
	}
	
}