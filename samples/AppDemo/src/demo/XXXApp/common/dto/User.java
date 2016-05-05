package demo.XXXApp.common.dto;

public class User implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long userid;
	private String username;
	private String passwd;
	private String email;
	private int sex;
	private java.sql.Date birthday;

	public User() {
	}

	@Override
	public String toString() {
		return "User [userid=" + userid + ", username=" + username
				+ ", passwd=" + passwd + ", email=" + email + ", sex=" + sex
				+ ", birthday=" + birthday + "]";
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public java.sql.Date getBirthday() {
		return birthday;
	}

	public void setBirthday(java.sql.Date birthday) {
		this.birthday = birthday;
	}

}
