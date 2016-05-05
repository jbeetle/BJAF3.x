package com.beetle.framework.business.service.common;

import java.util.Arrays;

public class RpcRequest implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String iface;// 服务接口类名
	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] arguments;
	private Class<?>[] exceptionTypes;
	private boolean async;
	private String methodNameKey;
	private long id;

	public RpcRequest() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMethodNameKey() {
		return methodNameKey;
	}

	public void setMethodNameKey(String methodNameKey) {
		this.methodNameKey = methodNameKey;
	}

	public boolean isAsync() {
		return async;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	public String getIface() {
		return iface;
	}

	public void setIface(String iface) {
		this.iface = iface;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	public Class<?>[] getExceptionTypes() {
		return exceptionTypes;
	}

	public void setExceptionTypes(Class<?>[] exceptionTypes) {
		this.exceptionTypes = exceptionTypes;
	}

	@Override
	public String toString() {
		return "RpcRequest [iface=" + iface + ", methodName=" + methodName
				+ ", parameterTypes=" + Arrays.toString(parameterTypes)
				+ ", arguments=" + Arrays.toString(arguments)
				+ ", exceptionTypes=" + Arrays.toString(exceptionTypes)
				+ ", async=" + async + ", methodNameKey=" + methodNameKey
				+ ", id=" + id + "]";
	}

}
