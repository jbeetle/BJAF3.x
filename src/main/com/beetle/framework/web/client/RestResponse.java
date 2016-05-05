package com.beetle.framework.web.client;

import java.util.Map;

public final class RestResponse {
	private int statusCode;
	private String content;
	private Map<String, String> headers;

	/**
	 * 请求响应状态码（与http协议标准状态码相一致）
	 * 
	 * @return
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * 响应内容
	 * 
	 * @return
	 */
	public String getContent() {
		return content;
	}

	/**
	 * 响应的http header
	 * 
	 * @return
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	public RestResponse(int statusCode, String content,
			Map<String, String> headers) {
		super();
		this.statusCode = statusCode;
		this.content = content;
		this.headers = headers;
	}

	@Override
	public String toString() {
		return "RestResponse [statusCode=" + statusCode + ", content="
				+ content + ", headers=" + headers + "]";
	}

}
