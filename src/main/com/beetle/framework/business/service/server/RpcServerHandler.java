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
package com.beetle.framework.business.service.server;

import java.lang.reflect.InvocationTargetException;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.beetle.framework.AppContext;
import com.beetle.framework.business.service.common.RpcConst;
import com.beetle.framework.business.service.common.RpcRequest;
import com.beetle.framework.business.service.common.RpcResponse;
import com.beetle.framework.business.service.server.ServiceConfig.ServiceDef;
import com.beetle.framework.business.service.server.ServiceConfig.ServiceDef.MethodEx;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.resource.define.Constant;
import com.beetle.framework.resource.dic.def.AsyncMethodCallback;
import com.beetle.framework.util.thread.task.TaskImp;
import com.beetle.framework.util.thread.task.TaskThreadPool;

class RpcServerHandler extends SimpleChannelUpstreamHandler {
	private static final AppLogger logger = AppLogger
			.getInstance(RpcServerHandler.class);
	private final TaskThreadPool taskThreadPool;

	public RpcServerHandler(TaskThreadPool taskThreadPool) {
		super();
		this.taskThreadPool = taskThreadPool;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		Throwable te = e.getCause();
		if (te instanceof java.io.IOException) {// 网络异常，也有可能是soLinger参数引起
			return;
		}
		logger.error("Unexpected exception from downstream.{}", e.getCause());
		Channel c = e.getChannel();
		try {
			if (c.isWritable()) {
				RpcResponse res = new RpcResponse();
				res.setReturnFlag(RpcConst.ERR_CODE_SERVER_CHANNEL_EXCEPTION);
				res.setReturnMsg(logger.getStackTraceInfo(e.getCause()));
				c.write(res);
			}
		} finally {
			c.close();
		}
		// super.exceptionCaught(ctx, e);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		if (!(e.getMessage() instanceof RpcRequest)) {
			return;
		}
		RpcRequest req = (RpcRequest) e.getMessage();
		if (logger.isDebugEnabled()) {
			logger.debug("from:{}", e.getRemoteAddress());
			logger.debug("messageReceived:{}", req);
		}
		this.taskThreadPool.runInPool(new DealReqTask(ctx, req));
	}

	private static Object getAsyncMethodCallbackObject(RpcRequest req) {
		if (req.isAsync()) {
			Object[] oo = req.getArguments();
			for (Object o : oo) {
				if (o instanceof AsyncMethodCallback) {
					return o;
				}
			}
		}
		return null;
	}

	private static class DealReqTask extends TaskImp {

		private ChannelHandlerContext ctx;
		private RpcRequest req;

		public DealReqTask(ChannelHandlerContext ctx, RpcRequest req) {
			super(-1);
			this.req = req;
			this.ctx = ctx;
		}

		@Override
		protected void routine() throws InterruptedException {
			RpcResponse res = new RpcResponse();
			res.setId(req.getId());
			if (req.isAsync()) {
				res.setAsync(true);
			}
			ServiceDef sdef = ServiceConfig.lookup(req.getIface());
			if (sdef != null) {
				Object serviceImp = sdef.getServiceImpInstanceRef();
				if (serviceImp != null) {
					long tid = Thread.currentThread().getId();
					try {
						Object result;
						// Method method = serviceImp.getClass().getMethod(
						// req.getMethodName(), req.getParameterTypes());
						AppContext.getInstance().bind(tid,
								Constant.WORK_IN_RPC_FLAG);
						final MethodEx mex = sdef.getMethodEx(
								req.getMethodNameKey(), req.getMethodName(),
								req.getParameterTypes());
						result = mex.getMethod().invoke(serviceImp,
								req.getArguments());
						res.setResult(result);
						res.setReturnFlag(0);
						res.setReturnMsg("ok");
						if (logger.isDebugEnabled()) {
							logger.debug("methodName:{}", req.getMethodName());
							logger.debug("result:{}", result);
						}
					} catch (Throwable t) {
						if (t instanceof InvocationTargetException) {
							InvocationTargetException tt = (InvocationTargetException) t;
							res.setException(tt.getTargetException());
						} else {
							res.setException(t);
						}
						res.setReturnFlag(RpcConst.ERR_CODE_SERVER_SERVICE_INVOKE_EXCEPTION);
						res.setReturnMsg(logger.getStackTraceInfo(t));
						logger.error(res.getReturnMsg(), t);
					} finally {
						AppContext.getInstance().unbind(tid);
					}
				} else {
					res.setReturnFlag(RpcConst.ERR_CODE_SERVER_SERVICE_NEW_INSTANCE_EXCEPTION);
					res.setReturnMsg("service imp class create instance err! ["
							+ req.getIface() + "] ");
				}
			} else {
				res.setReturnFlag(RpcConst.ERR_CODE_SERVER_SERVICE_NOTFOUND_EXCEPTION);
				res.setReturnMsg("can't not found [" + req.getIface()
						+ "] configuration data in server");
			}
			if (ctx.getChannel().isOpen()) {
				if (res.isAsync()) {
					res.setResult(getAsyncMethodCallbackObject(req));// 把callback传回去
				}
				ctx.getChannel().write(res);
			} else {
				logger.warn(
						"Communication channels have closed(maybe invoke timeout), service results data can not return to the client![{}]",
						res);
			}
		}
	}
}
