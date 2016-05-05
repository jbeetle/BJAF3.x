package com.beetle.dwzdemo.valueobject;


public class XxFriend extends VOBase {

	private static final long serialVersionUID = 1L;
	private Long friendid;
	private String phone;
	private String address;
	private String email;
	private Long userid;
	private String friendname;

	public XxFriend() {
	}

	public Long getFriendid() {
		return this.friendid;
	}

	public String getPhone() {
		return this.phone;
	}

	public String getAddress() {
		return this.address;
	}

	public String getEmail() {
		return this.email;
	}

	public Long getUserid() {
		return this.userid;
	}

	public String getFriendname() {
		return this.friendname;
	}

	public void setFriendid(Long friendid) {
		this.friendid = friendid;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public void setFriendname(String friendname) {
		this.friendname = friendname;
	}
}