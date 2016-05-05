package com.beetle.framework.business.service.common;

/**
 * 
 * 方法回调接口
 * 
 * @param <T>回调结果对象定义
 */
public abstract class AsyncMethodCallback<T> implements java.io.Serializable {
	@Override
	public String toString() {
		return "AsyncMethodCallback [rst=" + rst + "]";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private T rst;

	/**
	 * 设置结果，以便返回给客户端。 服务端调用
	 * 
	 * @param result
	 */
	public void setResultToReturnClient(T result) {
		this.rst = result;
	}

	/**
	 * 服务器端调用
	 * 
	 * @return
	 */
	public T getResult() {
		return rst;
	}

	/**
	 * 当服务端端处理完结果，并把数据返回给客户端时，触发此事件
	 * 
	 * @param result
	 */
	public abstract void onComplete(T result);

	/**
	 * 当服务器端处理出现异常时，会把处理异常返回给客户端，并触发此事件
	 * 
	 * @param errCode
	 *            --状态码
	 * @param message
	 *            --状态说明
	 * @param exception
	 *            --出错异常，如果没有的话，则为null
	 */
	public abstract void onError(int errCode, String message,
			Throwable exception);
}
