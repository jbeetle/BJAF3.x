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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.beetle.framework.AppProperties;
import com.beetle.framework.business.service.client.RpcClientException;
import com.beetle.framework.business.service.client.ServiceClient;
import com.beetle.framework.business.service.common.RpcConst;
import com.beetle.framework.business.service.common.RpcRequest;
import com.beetle.framework.resource.dic.DIContainer;
import com.beetle.framework.util.ClassUtil;
import com.beetle.framework.util.OtherUtil;
import com.beetle.framework.util.thread.Counter;
import com.beetle.framework.util.thread.Locker;

/**
 * 
 * RPC服务工厂，根据BJAF开发框架的层次界定，RPC服务属于业务层对外暴露的服务，提供给表示层或其他进程去调用，业务层内部组件(类)
 * 不能使用此本厂lockup出相关的Serive来使用。
 * 
 * 
 */
public class RpcProxyClient {

	private static class Host {
		private int port;
		private String hostname;
		private boolean die;
		private long dieTime;
		private volatile boolean initflag;

		public Host(int port, String hostname) {
			super();
			this.port = port;
			this.hostname = hostname;
			this.die = false;
			this.dieTime = 0;
			this.initflag = false;
		}

		public boolean isInitflag() {
			return initflag;
		}

		public void setInitflag(boolean initflag) {
			this.initflag = initflag;
		}

		public boolean isDie() {
			return die;
		}

		public void setDie(boolean die) {
			this.die = die;
		}

		public long getDieTime() {
			return dieTime;
		}

		public void setDieTime(long dieTime) {
			this.dieTime = dieTime;
		}

		public int getPort() {
			return port;
		}

		public String getHostname() {
			return hostname;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((hostname == null) ? 0 : hostname.hashCode());
			result = prime * result + port;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Host other = (Host) obj;
			if (hostname == null) {
				if (other.hostname != null)
					return false;
			} else if (!hostname.equals(other.hostname))
				return false;
			if (port != other.port)
				return false;
			return true;
		}
	}

	private final static List<Host> hosts = new ArrayList<RpcProxyClient.Host>();
	private final static String rpc_client_proxyInvoke = "rpc_client_proxyInvoke";
	private final static Map<String, Object> serviceProxyCache = new ConcurrentHashMap<String, Object>();

	public static <T> T lookup(final Class<T> interfaceClass,
			boolean withShortConnection) {
		String pi = AppProperties.get(rpc_client_proxyInvoke);
		if (pi != null && pi.equalsIgnoreCase("jvm")) {
			// return lookupAop(interfaceClass);
			return localLookup(interfaceClass);
		} else {
			if (hosts.isEmpty()) {
				initHosts();
			}
			if (hosts.size() == 1) {
				Host host = hosts.get(0);//
				return remoteLookup(interfaceClass, host.getHostname(),
						host.getPort(), withShortConnection);
			} else if (hosts.size() > 1) {
				return remoteLookup(interfaceClass, null, -1000,
						withShortConnection);
			} else {
				throw new RpcClientException(
						"Please check the [rpc_client_remoteAddress] value is correct or not");
			}
		}
	}

	private static Host getOkHost() {
		List<Host> tmp = new ArrayList<Host>();
		for (int i = 0; i < hosts.size(); i++) {
			Host host = hosts.get(i);
			if (!host.isInitflag()) {
				host.setInitflag(true);
				boolean ff = ServiceClient.getInstance(host.hostname,
						host.getPort()).checkServerConnection();
				if (!ff) {
					host.setDie(true);
					host.setDieTime(System.currentTimeMillis());
				}
			}
			if (!host.isDie()) {
				tmp.add(host);
			} else {
				long lx = System.currentTimeMillis() - host.getDieTime();
				if (lx >= RpcConst.CLIENT_HOST_CHECK_TIME) {
					host.setDie(false);
					host.setDieTime(0);
					tmp.add(host);
				}
			}
		}
		int x = OtherUtil.randomInt(0, tmp.size());
		if (tmp.isEmpty()) {
			throw new RpcClientException(RpcConst.ERR_CODE_HOST_DIE_EXCEPTION,
					"all host server has died!");
		}
		Host h = tmp.get(x);
		try {
			return h;
		} finally {
			tmp.clear();
		}
	}

	private static void initHosts() {
		synchronized (hosts) {
			if (hosts.isEmpty()) {
				String addr = AppProperties.get("rpc_client_remoteAddress");
				if (addr == null || addr.trim().length() == 0) {
					throw new RpcClientException(
							"rpc_client_remoteAddress value must be setted in application.properties");
				}
				addr = addr.substring(1, addr.length() - 1);
				String h[] = addr.split(";");
				for (int i = 0; i < h.length; i++) {
					String hh[] = h[i].split(":");
					Host host = new Host(Integer.parseInt(hh[1]), hh[0]);
					hosts.add(host);
				}
			}
		}
	}

	/**
	 * 查找服务接口<br>
	 * 首先根据参数“rpc_client_proxyInvoke=jvm”是否定义来优先在本地查找；<br>
	 * 如果上述参数未定义，则会从“rpc_client_remoteAddress”定义的地址去远程查找，远程访问采取连接池的方式<br>
	 * 池大小通过参数"rpc_client_connectionAmount"定义
	 * 
	 * @param <T>
	 * @param interfaceClass
	 * @return
	 */
	public static <T> T lookup(final Class<T> interfaceClass) {
		return lookup(interfaceClass, false);
	}

	static <T> T localLookup(final Class<T> interfaceClass) {
		// return lookupProxy(interfaceClass);
		// return BusinessContext.serviceLookup(interfaceClass);
		return DIContainer.getInstance().retrieve(interfaceClass);
	}

	/**
	 * 释放与服务代理相关的所有资源（包括网络资源，如果有的话）
	 */
	public static void clearAll() {
		ServiceClient.releaseAllResources();
		hosts.clear();
		serviceProxyCache.clear();
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
	@SuppressWarnings("unchecked")
	public static <T> T remoteLookup(final Class<T> interfaceClass,
			final String host, final int port, boolean withShortConnection) {
		final String key = genKey(interfaceClass.getName(), 'r',
				withShortConnection);
		if (serviceProxyCache.containsKey(key)) {
			return (T) serviceProxyCache.get(key);
		}
		synchronized (serviceProxyCache) {
			T t = (T) serviceProxyCache.get(key);
			if (t == null) {
				if (!interfaceClass.isInterface())
					throw new IllegalArgumentException("The "
							+ interfaceClass.getName()
							+ " must be interface class!");
				t = (T) Proxy.newProxyInstance(
						interfaceClass.getClassLoader(),
						new Class<?>[] { interfaceClass },
						new ServiceProxyHandler(host, port, interfaceClass
								.getName(), withShortConnection));
				serviceProxyCache.put(key, t);
			}
			return t;
		}
	}

	private static String genKey(String classname, char callflag,
			boolean withShortConnection) {
		StringBuilder sb = new StringBuilder();
		sb.append(classname);
		sb.append(callflag);
		sb.append(withShortConnection);
		return sb.toString();
	}

	private static class ServiceProxyHandler implements InvocationHandler {
		private String host;
		private int port;
		private String interfacename;
		private boolean withShortConnection;
		private static final Counter COUNTER = new Counter(2009521l);

		public ServiceProxyHandler(String host, int port, String interfacename,
				boolean withShortConnection) {
			super();
			this.host = host;
			this.port = port;
			this.interfacename = interfacename;
			this.withShortConnection = withShortConnection;
		}

		private void lockForTime(long timeout) {
			try {
				new Locker().lockForTime(timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			RpcRequest req = new RpcRequest();
			req.setId(COUNTER.increaseAndGet());
			req.setIface(interfacename);
			req.setArguments(args);
			req.setMethodNameKey(ClassUtil.genMethodKey(method));
			req.setMethodName(method.getName());
			req.setParameterTypes(method.getParameterTypes());
			req.setExceptionTypes(method.getExceptionTypes());
			ServiceClient client;
			if (port > 0) {
				client = ServiceClient.getInstance(host, port);
			} else {
				try {
					Host host = getOkHost();
					client = ServiceClient.getInstance(host.getHostname(),
							host.getPort());
				} catch (RpcClientException e) {
					if (e.getErrCode() == RpcConst.ERR_CODE_HOST_DIE_EXCEPTION) {// 所有连接都是死光了
						Locker lock = new Locker();
						lock.lockForTime(RpcConst.CLIENT_HOST_CHECK_TIME);
						Host host = getOkHost();
						client = ServiceClient.getInstance(host.getHostname(),
								host.getPort());
					} else {
						throw e;
					}
				}
			}
			if (withShortConnection) {
				try {
					return client.invokeWithShortConnect(req);
				} catch (RpcClientException rce) {
					if (rce.getErrCode() == RpcConst.ERR_CODE_CONN_EXCEPTION) {
						lockForTime(AppProperties.getAsInt(
								"rpc_client_retry_waitForTime", 1000 * 10));
						return client.invokeWithShortConnect(req);
					} else {
						throw rce;
					}
				}
			}
			try {
				return client.invokeWithLongConnect(req);
			} catch (RpcClientException rce) {
				if (rce.getErrCode() == RpcConst.ERR_CODE_CONN_EXCEPTION) {
					lockForTime(AppProperties.getAsInt(
							"rpc_client_retry_waitForTime", 1000 * 10));
					return client.invokeWithLongConnect(req);
				} else {
					throw rce;
				}
			}
		}
	}
}
