package demo.valueobject;

import java.io.Serializable;

public class ExpFriend implements Serializable {

	private static final long serialVersionUID = 1L;
	private String phone;
	private Long friendid;
	private String address;
	private String email;
	private String friendname;

	public ExpFriend() {
	}

	public String getPHONE() {
		return this.phone;
	}

	public Long getFRIENDID() {
		return this.friendid;
	}

	public String getADDRESS() {
		return this.address;
	}

	public String getEMAIL() {
		return this.email;
	}

	public String getFRIENDNAME() {
		return this.friendname;
	}

	public void setPHONE(String phone) {
		this.phone = phone;
	}

	public void setFRIENDID(Long friendid) {
		this.friendid = friendid;
	}

	public void setADDRESS(String address) {
		this.address = address;
	}

	public void setEMAIL(String email) {
		this.email = email;
	}

	public void setFRIENDNAME(String friendname) {
		this.friendname = friendname;
	}

	@Override
	public String toString() {
		return "TestFriend [phone=" + phone + ", friendid=" + friendid
				+ ", address=" + address + ", email=" + email + ", friendname="
				+ friendname + "]";
	}

}