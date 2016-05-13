package com.beetle.component.security.dto;

public class SecUsers implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String password;
	private String salt;
	private Integer locked;
	private Long userid;
	private String username;
	private Integer trycount;
	private java.sql.Timestamp createTime;

	public SecUsers() {
	}

	public SecUsers(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public java.sql.Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(java.sql.Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getCredentialsSalt() {
		return username + salt;
	}

	public Integer getTrycount() {
		return trycount;
	}

	public void setTrycount(Integer trycount) {
		this.trycount = trycount;
	}

	public String getPassword() {
		return this.password;
	}

	public String getSalt() {
		return this.salt;
	}

	public Integer getLocked() {
		return this.locked;
	}

	public Long getUserId() {
		return this.userid;
	}

	public String getUsername() {
		return this.username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public void setLocked(Integer locked) {
		this.locked = locked;
	}

	public void setUserId(Long userid) {
		this.userid = userid;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "SecUsers [password=" + password + ", salt=" + salt + ", locked=" + locked + ", userid=" + userid
				+ ", username=" + username + ", trycount=" + trycount + ", createTime=" + createTime + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((locked == null) ? 0 : locked.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((salt == null) ? 0 : salt.hashCode());
		result = prime * result + ((trycount == null) ? 0 : trycount.hashCode());
		result = prime * result + ((userid == null) ? 0 : userid.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SecUsers other = (SecUsers) obj;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!createTime.equals(other.createTime))
			return false;
		if (locked == null) {
			if (other.locked != null)
				return false;
		} else if (!locked.equals(other.locked))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (salt == null) {
			if (other.salt != null)
				return false;
		} else if (!salt.equals(other.salt))
			return false;
		if (trycount == null) {
			if (other.trycount != null)
				return false;
		} else if (!trycount.equals(other.trycount))
			return false;
		if (userid == null) {
			if (other.userid != null)
				return false;
		} else if (!userid.equals(other.userid))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}