package com.beetle.dwzdemo.web.common;

public class WsStatus implements java.io.Serializable {
	// {"statusCode":"200", "message":"操作成功", "navTabId":"navNewsLi",
	// "forwardUrl":"", "callbackType":"closeCurrent"}
	private static final long serialVersionUID = 1L;
	private int statusCode;
	private String message;
	private String navTabId;
	private String forwardUrl;
	private String callbackType;

	public WsStatus() {
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getNavTabId() {
		return navTabId;
	}

	public void setNavTabId(String navTabId) {
		this.navTabId = navTabId;
	}

	public String getForwardUrl() {
		return forwardUrl;
	}

	public void setForwardUrl(String forwardUrl) {
		this.forwardUrl = forwardUrl;
	}

	public String getCallbackType() {
		return callbackType;
	}

	public void setCallbackType(String callbackType) {
		this.callbackType = callbackType;
	}

	@Override
	public String toString() {
		return "WsStatus [statusCode=" + statusCode + ", message=" + message
				+ ", navTabId=" + navTabId + ", forwardUrl=" + forwardUrl
				+ ", callbackType=" + callbackType + "]";
	}

}
