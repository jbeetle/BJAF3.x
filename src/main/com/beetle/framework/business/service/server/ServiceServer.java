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

import com.beetle.framework.AppProperties;
import com.beetle.framework.business.service.common.codec.CodecFactory;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.util.thread.task.NamedThreadFactory;
import com.beetle.framework.util.thread.task.TaskThreadPool;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

public class ServiceServer {
	private final int port;
	private static final AppLogger logger = AppLogger
			.getInstance(RpcServerHandler.class);
	private final ServerBootstrap bootstrap;
	private InetSocketAddress isa;
	private final TaskThreadPool taskThreadPool;
	private final ChannelGroup channelGroup;

	public ServiceServer(InetSocketAddress isa) {
		this(0);
		this.isa = isa;
	}

	public ServiceServer(int port) {
		this.port = port;
		this.channelGroup = new DefaultChannelGroup();
		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(new NamedThreadFactory(
						"ServiceServer-bossExecutor-", false)),
				Executors.newCachedThreadPool(new NamedThreadFactory(
						"ServiceServer-workerExecutor-", true))));
		bootstrap.setOption("tcpNoDelay", Boolean.parseBoolean(AppProperties
				.get("rpc_server_tcpNoDelay", "true")));
		bootstrap.setOption("reuseAddress", Boolean.parseBoolean(AppProperties
				.get("rpc_server_reuseAddress", "true")));
		String c1 = AppProperties.get("rpc_server_child_tcpNoDelay");
		if (c1 != null && c1.trim().length() > 0) {
			bootstrap.setOption("child.tcpNoDelay", Boolean.parseBoolean(c1));
		}
		c1 = AppProperties.get("rpc_server_child_receiveBufferSize");
		if (c1 != null && c1.trim().length() > 0) {
			bootstrap
					.setOption("child.receiveBufferSize", Integer.parseInt(c1));
		}
		this.taskThreadPool = new TaskThreadPool(AppProperties.getAsInt(
				"rpc_server_workThreadPool_coreSize", 50),
				AppProperties
						.getAsInt("rpc_server_workThreadPool_MaxSize", 200),
				AppProperties.getAsInt(
						"rpc_server_workThreadPool_keepAliveTime",
						60 * 1000 * 5), true, new CallerRunsPolicy());
	}

	public void start() {
		// Set up the pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(CodecFactory.createEncoder(),
						CodecFactory.createDecoder(), new RpcServerHandler(
								taskThreadPool));
			}
		});
		// Bind and start to accept incoming connections.
		if (this.port > 0) {
			Channel cn = bootstrap.bind(new InetSocketAddress(port));
			channelGroup.add(cn);
			logger.info("ServiceServer started (at port[" + port + "])");
		} else {
			Channel cn = bootstrap.bind(this.isa);
			channelGroup.add(cn);
			logger.info("ServiceServer started (at port[" + isa.getPort()
					+ "])");
		}

	}

	public void stop() {
		// this.channelGroup.close().awaitUninterruptibly();
		// this.channelGroup.unbind();
		// this.channelGroup.disconnect();
		this.channelGroup.close();
		// bootstrap.releaseExternalResources();
		logger.info("ServiceServer stopped");
	}
}
