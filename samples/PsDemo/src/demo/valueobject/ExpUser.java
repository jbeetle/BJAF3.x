package demo.valueobject;

import java.io.Serializable;
import java.sql.Date;

public class ExpUser implements Serializable {

	private static final long serialVersionUID = 1L;
	private Date birthday;
	private Integer sex;
	private String passwd;
	private String email;
	private String username;
	private Long userid;

	public ExpUser() {
	}

	public Date getBIRTHDAY() {
		return this.birthday;
	}

	public Integer getSEX() {
		return this.sex;
	}

	public String getPASSWD() {
		return this.passwd;
	}

	public String getEMAIL() {
		return this.email;
	}

	public String getUSERNAME() {
		return this.username;
	}

	public Long getUSERID() {
		return this.userid;
	}

	public void setBIRTHDAY(Date birthday) {
		this.birthday = birthday;
	}

	public void setSEX(Integer sex) {
		this.sex = sex;
	}

	public void setPASSWD(String passwd) {
		this.passwd = passwd;
	}

	public void setEMAIL(String email) {
		this.email = email;
	}

	public void setUSERNAME(String username) {
		this.username = username;
	}

	public void setUSERID(Long userid) {
		this.userid = userid;
	}

	@Override
	public String toString() {
		return "TestUser [birthday=" + birthday + ", sex=" + sex + ", passwd="
				+ passwd + ", email=" + email + ", username=" + username
				+ ", userid=" + userid + "]";
	}

}