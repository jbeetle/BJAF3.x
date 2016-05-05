package com.beetle.framework.business.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.beetle.framework.AppProperties;
import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.appsrv.AppMainImp;
import com.beetle.framework.business.job.JobScheduler;
import com.beetle.framework.business.service.server.ServiceServer;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.persistence.access.ConnectionFactory;
import com.beetle.framework.persistence.dao.DaoFactory;
import com.beetle.framework.resource.dic.DIContainer;

public class BusinessAppSrv extends AppMainImp {
	private final static AppLogger logger = AppLogger
			.getInstance(BusinessAppSrv.class);
	private final ServiceServer rpcSrv;// 支持RPC对外服务暴露的Server

	public static interface BusinessAppEvent {
		/**
		 * App启动事件
		 */
		void starEvent();

		/**
		 * App停止事件
		 */
		void stopEvent();
	}

	public BusinessAppSrv(int cmdSrvPort, int rpcSrvPort) {
		super(cmdSrvPort);
		String ipbind = AppProperties.get("businessAppSrv_rpcServer_ipBind",
				"0.0.0.0");
		if (ipbind.equals("0.0.0.0")) {
			rpcSrv = new ServiceServer(rpcSrvPort);
			logger.info("ServiceServer bind all ip at {} port", rpcSrvPort);
		} else {
			try {
				rpcSrv = new ServiceServer(new InetSocketAddress(
						InetAddress.getByName(ipbind), rpcSrvPort));
				logger.info("ServiceServer bind ip {} at {} port", ipbind,
						rpcSrvPort);
			} catch (UnknownHostException e) {
				throw new AppRuntimeException(e);
			}
		}
	}

	@Override
	protected String dealCmd(String cmd) {
		// 接受外部命令，并处理命令（外部命令一般是指shell脚本发出的）
		logger.info("get the external command:{}", cmd);
		StringBuilder sb = new StringBuilder();
		if (cmd.equalsIgnoreCase("shutdown")) {
			this.shutDownServer();
			sb.append("shutdownOK");
		} else if (cmd.equalsIgnoreCase("restartRpcServer")) {
			this.rpcSrv.stop();
			this.rpcSrv.start();
		} else if (cmd.equalsIgnoreCase("help")) {
			sb.append("-->help info:\n");
			sb.append("-->cmd[shutdown]--shutdown this server\n");
			sb.append("-->cmd[restartRpcServer]--restart rpc server\n");
		} else {
			sb.append("sorry,err cmd!\n");
		}
		return sb.toString();
	}

	@Override
	protected void shutdownServerEvent() {
		try {
			rpcSrv.stop();
			if (AppProperties.getAsBoolean(
					"businessAppSrv_jobScheduler_initialized", false)) {
				JobScheduler.getInstance().stop();
			}
		} finally {
			String imp = AppProperties.get("businessAppSrv_eventImp", "");
			if (imp.trim().length() > 0) {
				BusinessAppEvent bae;
				try {
					bae = (BusinessAppEvent) Class.forName(imp).newInstance();
					bae.stopEvent();
				} catch (Exception e) {
					throw new AppRuntimeException(e);
				}
			}
		}
		logger.info("BusinessAppSrv shutdown");
	}

	@Override
	protected void starServerEvent() {
		DIContainer.getInstance().init();
		if (AppProperties.getAsBoolean("businessAppSrv_dbPool_initialized",
				true)) {
			ConnectionFactory.initializeAllDataSources();// 初始化数据源（连接池）
		}
		if (AppProperties.getAsBoolean("businessAppSrv_daoObject_initialized",
				true)) {
			DaoFactory.initialize();// 初始化dao对象
		}
		if (AppProperties.getAsBoolean(
				"businessAppSrv_jobScheduler_initialized", false)) {
			JobScheduler.getInstance().start();
		}
		rpcSrv.start();
		String imp = AppProperties.get("businessAppSrv_eventImp", "");
		if (imp.trim().length() > 0) {
			BusinessAppEvent bae;
			try {
				bae = (BusinessAppEvent) Class.forName(imp).newInstance();
				bae.starEvent();
			} catch (Exception e) {
				throw new AppRuntimeException(e);
			}
		}
	}

	public static void main(String[] args) {
		inicmd(args);
	}

	/**
	 * 启动服务器
	 */
	public static void start() {
		inicmd(null);
	}

	/**
	 * 停止服务器
	 */
	public static void stop() {
		inicmd(new String[] { "shutdown" });
	}

	private static void inicmd(String[] args) {
		int localCmdSrvPort = AppProperties.getAsInt(
				"businessAppSrv_localCmdServer_port", 22476);
		int rpcSrvPort = AppProperties.getAsInt(
				"businessAppSrv_rpcServer_port", 7602);
		BusinessAppSrv xm = new BusinessAppSrv(localCmdSrvPort, rpcSrvPort);
		if (args != null && args.length == 1) {
			xm.executeCmd(args[0]);
		} else {
			xm.startServer();// 启动服务器
			xm.startCmdService();// 启动外部命令接受服务
			xm.startMemoryWatcherService();// 启动内存监控服务
			xm.startThreadMonitorService();// 启动线程监控服务
		}
		logger.info("BusinessAppSrv started[port:" + rpcSrvPort + "] OK");
	}
}
