/*
 * BJAF - Beetle J2EE Application Framework
 * 甲壳虫J2EE企业应用开发框架
 * 版权所有2003-2015 余浩东 (www.beetlesoft.net)
 * 
 * 这是一个免费开源的软件，您必须在
 *<http://www.apache.org/licenses/LICENSE-2.0>
 *协议下合法使用、修改或重新发布。
 *
 * 感谢您使用、推广本框架，若有建议或问题，欢迎您和我联系。
 * 邮件： <yuhaodong@gmail.com/>.
 */
package com.beetle.framework.business.service;

/**
 * 
 * Service服务工厂,服务必须在ServiceConfig.xml文件中注册
 * 
 */
public final class ServiceFactory {
	private ServiceFactory() {

	}

	/**
	 * 从本地容器中查找
	 * 
	 * @param interfaceClass
	 * @return
	 */

	public static <T> T localServiceLookup(final Class<T> interfaceClass) {
		return RpcProxyClient.localLookup(interfaceClass);
	}

	/**
	 * 查找服务接口<br>
	 * 首先根据application.properties配置文件中的参数“rpc_client_proxyInvoke=jvm”
	 * 是否定义来优先在本地查找；<br>
	 * 如果上述参数未定义，则会从“rpc_client_remoteAddress”定义的地址去远程查找，远程访问采取连接池的方式<br>
	 * 池大小通过参数"rpc_client_connectionAmount"定义
	 * 
	 * @param <T>
	 * @param interfaceClass
	 * @return
	 */
	public static <T> T serviceLookup(final Class<T> interfaceClass) {
		return RpcProxyClient.lookup(interfaceClass);
	}

	/**
	 * 查找服务接口<br>
	 * 首先根据application.properties配置文件中的参数“rpc_client_proxyInvoke=jvm”
	 * 是否定义来优先在本地查找；<br>
	 * 如果上述参数未定义，则会从“rpc_client_remoteAddress”定义的地址去远程查找。<br>
	 * 如果参数"withShortConnection"为true，则表示使用远程访问采取短连接方式；如果参数为false，则方法与
	 * "serviceLookup(final Class<T> interfaceClass)"等价<br>
	 * 
	 * @param interfaceClass
	 * @param withShortConnection
	 * @return
	 */
	public static <T> T serviceLookup(final Class<T> interfaceClass,
			boolean withShortConnection) {
		return RpcProxyClient.lookup(interfaceClass, withShortConnection);
	}

	/**
	 * 远程查找服务接口
	 * 
	 * @param <T>
	 * @param interfaceClass
	 *            --服务接口定义类
	 * @param host
	 *            --远程地址
	 * @param port
	 *            --远程地址监听端口
	 * @param withShortConnection
	 *            --是否采取短连接方式（就是接口方法每次调用使用一条连接）<br>
	 *            默认为false，即采取连接池长连接访问的方式，连接池的大小通过“rpc_client_connectionAmount”
	 *            参数定义
	 * @return
	 */
	public static <T> T rpcServiceLookup(final Class<T> interfaceClass,
			final String host, final int port, boolean withShortConnection) {
		return RpcProxyClient.remoteLookup(interfaceClass, host, port,
				withShortConnection);
	}

	/**
	 * 释放RPC相关的资源
	 */
	public static void releaseRpcResources() {
		RpcProxyClient.clearAll();
	}
}
