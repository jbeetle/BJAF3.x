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
package com.beetle.framework.business.service.client;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.beetle.framework.AppProperties;
import com.beetle.framework.business.service.common.RpcConst;
import com.beetle.framework.business.service.common.RpcRequest;
import com.beetle.framework.business.service.common.RpcResponse;
import com.beetle.framework.business.service.common.codec.CodecFactory;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.resource.dic.def.AsyncMethodCallback;
import com.beetle.framework.util.ClassUtil;
import com.beetle.framework.util.OtherUtil;
import com.beetle.framework.util.thread.task.NamedThreadFactory;

public final class ServiceClient {
	private final String host;
	private final int port;
	private final int connAmout;
	private final ClientBootstrap bootstrap;
	private final Map<Integer, Channel> channelMap;
	private final static Map<String, ServiceClient> clients = new ConcurrentHashMap<String, ServiceClient>();
	private static final AppLogger logger = AppLogger
			.getInstance(ServiceClient.class);
	// https://issues.jboss.org/browse/NETTY-424
	private static final ChannelFactory channelFactory = new NioClientSocketChannelFactory(
			Executors.newCachedThreadPool(new NamedThreadFactory(
					"ServiceClient-bossExecutor-", true)),
			Executors.newCachedThreadPool(new NamedThreadFactory(
					"ServiceClient-workerExecutor-", true)));

	private static class RpcClientPipelineFactory implements
			ChannelPipelineFactory {

		@Override
		public ChannelPipeline getPipeline() throws Exception {
			ChannelPipeline p = Channels.pipeline();
			p.addLast("objectDecoder", CodecFactory.createDecoder());
			p.addLast("objectEncoder", CodecFactory.createEncoder());
			p.addLast("rpcHander", new RpcClientHandler());
			return p;
		}
	}

	public static ServiceClient getInstance(String host, int port) {
		String key = host + port;
		ServiceClient client = clients.get(key);
		if (client == null) {
			synchronized (clients) {
				if (!clients.containsKey(key)) {
					client = new ServiceClient(host, port);
					clients.put(key, client);
				} else {
					client = clients.get(key);
				}
			}
		}
		return client;
	}

	public static void releaseAllResources() {
		Iterator<ServiceClient> it = clients.values().iterator();
		while (it.hasNext()) {
			it.next().clear();
		}
		clients.clear();
	}

	private ServiceClient(String host, int port) {
		this.host = host;
		this.port = port;
		// Configure the client.
		bootstrap = new ClientBootstrap(channelFactory);
		bootstrap.setOption("tcpNoDelay", Boolean.parseBoolean(AppProperties
				.get("rpc_client_tcpNoDelay", "true")));
		bootstrap.setOption("keepAlive", Boolean.parseBoolean(AppProperties
				.get("rpc_client_keepAlive", "true")));
		bootstrap.setOption("connectTimeoutMillis", AppProperties.getAsInt(
				"rpc_client_connectTimeoutMillis", 1000 * 30));
		bootstrap.setOption("receiveBufferSize", AppProperties.getAsInt(
				"rpc_client_receiveBufferSize", 1024 * 1024));
		bootstrap.setOption("soLinger",
				AppProperties.getAsInt("rpc_client_soLinger", -1));
		bootstrap.setPipelineFactory(new RpcClientPipelineFactory());
		connAmout = AppProperties.getAsInt("rpc_client_connectionAmount", 1);
		this.channelMap = new ConcurrentHashMap<Integer, Channel>();
		initConns();
	}

	private static final String rpcHanderName = "rpcHander";

	public Object invokeWithShortConnect(final RpcRequest req) throws Throwable {
		if (isAsyncReq(req)) {
			throw new RpcClientException(
					"Short connection mode does not support asynchronous callback");
		}
		Channel channel = null;
		// RpcClientHandler rpcHander = channel.getPipeline().get(
		// RpcClientHandler.class);
		try {
			channel = open();
			RpcClientHandler rpcHander = (RpcClientHandler) channel
					.getPipeline().get(rpcHanderName);
			RpcResponse res = rpcHander.invoke(req);
			// channel.close().awaitUninterruptibly();
			// logger.debug("invokeWithShortConnect res:{}", res);
			if (res != null) {
				if (res.getReturnFlag() >= 0) {
					return res.getResult();
				}
				dealErrException(req, res);
			}
			return null;
		} finally {
			if (channel != null) {
				channel.close();
			}
		}
	}

	private static boolean isAsyncReq(final RpcRequest req) {
		Class<?> cc[] = req.getParameterTypes();
		for (Class<?> c : cc) {
			if (c.equals(AsyncMethodCallback.class)) {
				return true;
			}
		}
		return false;
	}

	private AtomicBoolean initFlag = new AtomicBoolean(false);

	private void initConns() {
		if (!initFlag.compareAndSet(false, true)) {
			return;
		}
		for (int i = 0; i < this.connAmout; i++) {
			// channelList.add(open());
			channelMap.put(i, new MockChannel());
		}
	}

	private Channel pickUpChannelFromPool() {
		int i = OtherUtil.randomInt(0, channelMap.size());
		if (logger.isDebugEnabled()) {
			logger.debug("channel {} is selected,size: {}", i,
					channelMap.size());
		}
		try {
			Channel channel = (Channel) channelMap.get(i);
			if (channel == null || !channel.isOpen()) {
				synchronized (channelMap) {
					if (channel != null) {
						if (channel.isOpen()) {
							return channel;
						}
						channel.close();
						channelMap.remove(i);
					}
					channel = open();
					if (channelMap.size() < connAmout) {
						channelMap.put(i, channel);
					}
					logger.debug("channelList {}", channelMap);
				}
			}
			return channel;
		} catch (Exception e) {
			logger.error("pickUpChannelFromPool err", e);
			return channelMap.get(0);
		}
	}

	public Object invokeWithLongConnect(final RpcRequest req) throws Throwable {
		Channel channel = pickUpChannelFromPool();
		final RpcClientHandler rpcHander = (RpcClientHandler) channel
				.getPipeline().get(rpcHanderName);
		try {
			// logger.debug("rpcHander:{}", rpcHander);
			if (isAsyncReq(req)) {
				req.setAsync(true);
			}
			logger.debug("invokeWithLongConnect req:{}", req);
			if (req.isAsync()) {
				rpcHander.asyncInvoke(req);
				return null;
			} else {
				RpcResponse res = rpcHander.invoke(req);
				logger.debug("invokeWithLongConnect res:{}", res);
				if (res != null) {
					if (res.getReturnFlag() >= 0) {
						return res.getResult();
					}
					dealErrException(req, res);
				}
				return null;
			}
		} finally {
			// ..
		}
	}

	private void dealErrException(final RpcRequest req, RpcResponse res)
			throws Exception, Throwable {
		if (req.getExceptionTypes() != null
				&& req.getExceptionTypes().length > 0) {
			if (res.getReturnFlag() == RpcConst.ERR_CODE_CLIENT_INVOKE_TIMEOUT_EXCEPTION) {
				throwNewException(req, res);
			} else {
				if (res.getException() != null)
					throw (Throwable) res.getException();
				throwNewException(req, res);
			}
		} else {
			if (res.getReturnFlag() == RpcConst.ERR_CODE_CLIENT_INVOKE_TIMEOUT_EXCEPTION)
				throw new RpcClientException(res.getReturnFlag(),
						res.getReturnMsg());
			throw new RpcClientException(
					RpcConst.ERR_CODE_REMOTE_CALL_EXCEPTION,
					"remote call err：{" + res.getReturnMsg() + "["
							+ res.getReturnFlag() + "]}");
		}
	}

	private void throwNewException(final RpcRequest req, RpcResponse res)
			throws Throwable {
		Class<?> t = req.getExceptionTypes()[0];
		@SuppressWarnings("rawtypes")
		Class[] constrParamTypes = new Class[] { String.class };
		Object[] constrParamValues = new Object[] { res.getReturnMsg() + "["
				+ res.getReturnFlag() + "]" };
		Throwable tb = (Throwable) ClassUtil.newInstance(t.getName(),
				constrParamTypes, constrParamValues);
		throw tb;
	}

	public boolean checkServerConnection() {
		boolean f = true;
		Channel c = null;
		try {
			c = open();
		} catch (RpcClientException e) {
			f = false;
			logger.error(e.getMessage() + "[" + e.getErrCode() + "]");
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return f;
	}

	private Channel open() {
		ChannelFuture connectFuture = bootstrap.connect(new InetSocketAddress(
				host, port));
		Channel channel = connectFuture.awaitUninterruptibly().getChannel();
		if (channel.isConnected()) {
			logger.debug("connect[" + host + "(" + port + ") OK]");
			logger.debug("channel:{}", channel);
			return channel;
		} else {
			throw new RpcClientException(RpcConst.ERR_CODE_CONN_EXCEPTION,
					"connecting to the server[" + host + "," + port + "] error");
		}
	}

	public void clear() {
		// bootstrap.releaseExternalResources();
		try {
			Iterator<Channel> it = channelMap.values().iterator();
			while (it.hasNext()) {
				Channel c = it.next();
				if (c != null) {
					c.close();
					logger.info("release connect[" + host + "(" + port + ")]");
				} else {
					break;
				}
			}
		} finally {
			channelMap.clear();
			this.bootstrap.releaseExternalResources();
		}
	}
}
