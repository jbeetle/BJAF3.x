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

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.beetle.framework.AppProperties;
import com.beetle.framework.business.service.common.RpcConst;
import com.beetle.framework.business.service.common.RpcRequest;
import com.beetle.framework.business.service.common.RpcResponse;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.resource.dic.def.AsyncMethodCallback;

public class RpcClientHandler extends SimpleChannelUpstreamHandler {
	private static final AppLogger logger = AppLogger
			.getInstance(RpcClientHandler.class);
	private volatile Channel channel;
	private final int timeout;
	private final Map<Long, BlockingQueue<RpcResponse>> RsMatcher;

	public RpcClientHandler() {
		super();
		timeout = AppProperties.getAsInt("rpc_client_invoke_max_waitForTime",
				1000 * 60 * 10);
		this.RsMatcher = new ConcurrentHashMap<Long, BlockingQueue<RpcResponse>>(
				2000);
	}

	public void asyncInvoke(final RpcRequest req) {
		channel.write(req);
	}

	public RpcResponse invoke(final RpcRequest req) {
		return docall(req);
	}

	private RpcResponse docall(final RpcRequest req) {
		BlockingQueue<RpcResponse> resultQueue = new LinkedBlockingQueue<RpcResponse>();
		long reqId = req.getId();
		RsMatcher.put(reqId, resultQueue);
		try {
			channel.write(req);
			RpcResponse res;
			// resultQueue.clear();??
			boolean interrupted = false;
			for (;;) {
				try {
					// res = resultQueue.take();
					res = resultQueue.poll(timeout, TimeUnit.MILLISECONDS);
					if (res == null) {
						res = new RpcResponse();
						res.setReturnFlag(RpcConst.ERR_CODE_CLIENT_INVOKE_TIMEOUT_EXCEPTION);
						res.setReturnMsg("client invoke timeout[" + timeout
								+ "ms]");
						channel.close();
					}
					break;
				} catch (InterruptedException e) {
					channel.close();// 超时关闭链路，以防服务端执行完毕后通过此通过返回
					interrupted = true;
				} finally {
					resultQueue.clear();// 以防有垃圾
				}
			}
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
			return res;
		} finally {
			RsMatcher.remove(reqId);
		}
	}

	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		this.channel = e.getChannel();
		super.channelOpen(ctx, e);
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		try {
			logger.debug("channelDisconnected:{}", e);
			repair("server shutdwon raise err");
		} finally {
			super.channelDisconnected(ctx, e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("message:{}", e.getMessage());
		}
		// logger.info(e.getMessage());
		if (e.getMessage() instanceof RpcResponse) {
			// ServiceClient.putResultIntoCache((RpcResponse) e.getMessage());
			RpcResponse rrs = (RpcResponse) e.getMessage();
			if (!rrs.isAsync()) {
				BlockingQueue<RpcResponse> resultQueue = RsMatcher.get(rrs
						.getId());
				if (resultQueue == null) {
					if (logger.isDebugEnabled()) {
						logger.debug("id:{} can't found blockqueue,return",
								rrs.getId());
					}
					return;
				}
				boolean f = resultQueue.offer(rrs);
				if (logger.isDebugEnabled()) {
					logger.debug("insert into queue state:{}", f);
				}
			} else {
				if (rrs.getResult() != null) {
					@SuppressWarnings("rawtypes")
					AsyncMethodCallback amcbObj = (AsyncMethodCallback) rrs
							.getResult();
					logger.debug("callback work:{}", amcbObj);
					if (rrs.getReturnFlag() < 0) {
						amcbObj.onError(rrs.getReturnFlag(),
								rrs.getReturnMsg(),
								(Throwable) rrs.getException());
					} else {
						amcbObj.onComplete(amcbObj.getResult());
					}
				}
			}
		} else {
			// ctx.getChannel().write(req);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		try {
			logger.error("Unexpected exception {}", e.getCause());
			repair(logger.getStackTraceInfo(e.getCause()));
		} finally {
			e.getChannel().close();
		}
	}

	private void repair(String info) {
		Iterator<BlockingQueue<RpcResponse>> it = RsMatcher.values().iterator();
		while (it.hasNext()) {
			BlockingQueue<RpcResponse> resultQueue = it.next();
			if (resultQueue.isEmpty()) {
				RpcResponse res = new RpcResponse();
				res.setReturnFlag(RpcConst.ERR_CODE_REMOTE_CALL_EXCEPTION);
				res.setReturnMsg(info);
				boolean f = resultQueue.offer(res);
				if (logger.isDebugEnabled()) {
					logger.debug(
							"insert exception response into queue state:{}", f);
				}
			}
		}
	}

}
