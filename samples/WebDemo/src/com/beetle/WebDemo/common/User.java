package com.beetle.WebDemo.common;

import java.io.Serializable;

public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer userId;

	private String name;

	private String phone;

	private String sex;

	private int year;

	public User() {
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public String getSex() {
		return sex;
	}

	public Integer getUserId() {
		return userId;
	}

	public int getYear() {
		return year;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void setYear(int year) {
		this.year = year;
	}
}
