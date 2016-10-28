package com.beetle.framework.resource.define;

import java.util.HashMap;
import java.util.Map;

/*
 * 定义一个公共的结果返回的DTO，数据采取Map形式传输，
 * code为结果标记，message为code标记的辅助说明
 */
public final class ResultDTO implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1260567944229584570L;
	private int code;
	private String message;
	private final Map<String, Object> data;

	public ResultDTO() {
		super();
		this.data = new HashMap<String, Object>();
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void put(String key, Object value) {
		data.put(key, value);
	}

	public Object get(String key) {
		return data.get(key);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
