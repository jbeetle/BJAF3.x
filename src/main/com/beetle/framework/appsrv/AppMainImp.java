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
package com.beetle.framework.appsrv;

import com.beetle.framework.AppProperties;
import com.beetle.framework.appsrv.monitor.JVMWatcher;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.util.thread.Locker;
import com.beetle.framework.util.thread.RunWrapper;
import com.beetle.framework.util.thread.TMonitor;
import com.beetle.framework.util.thread.ThreadImp;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * <p>
 * Title: BeetleSoft Framework
 * </p>
 * 
 * 
 * <p>
 * Description: 服务器主程序抽象类 <br>
 * \u2022启动命令参数监听服务startCmdService()，在主程序中只有启动此服务，应用服务器才能具备参数的处理功能。
 * 
 * 如果一个应用服务器不需求命令参数，则可以不调用startCmdService()方法。 <br>
 * \u2022定义dealInputParameterCmd(cmd:String)抽象方法。所有参数命令的响应处理，都在这个方法里面实现。
 * 
 * 如果你的应用具备了参数命令管理功能，则你需要实现这个方法。 <br>
 * \u2022参数命令发送，sendParameterCmd(cmd:String)方法。你需要向服务器发送命令指令，调用此方法来实现。 <br>
 * \u2022启动后台线程监控服务startThreadMonitor()。只有启动了此服务，应用服务器才能实时监控各个功能子模块的运行情况。
 * 
 * 如果无需监控服务，则不必启动。 <br>
 * \u2022指定监控线程，monitorOneThread(threadImp:AppThreadImp)方法。利用这个方法，
 * 可以指定那个功能子模块需要被监控。 <br>
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: 甲壳虫软件
 * 
 * </p>
 * 
 * @author 余浩东
 * 
 * @version 1.0
 */
public abstract class AppMainImp {
	private static AppLogger logger = AppLogger.getInstance(AppMainImp.class);

	private CmdListener cmdService;
	private MainCoreService mcs;
	private JVMWatcher amw;
	private boolean monitorFlag = false;
	private boolean watcherFlag = false;
	private boolean cmdServiceFlag = false;

	private int cmdSerivicePort = 22476;

	final public void startMemoryWatcherService() {
		if (!watcherFlag) {
			amw = new JVMWatcher(3000);
			amw.start();
			logger.info("MemoryWatcherService started!");
			watcherFlag = true;
		}
	}

	public AppMainImp() {
		this.mcs = new MainCoreService();
	}

	/**
	 * MainAppImp
	 * 
	 * @param cmdSerivicePort
	 *            后台命令服务监控端口，默认为22476
	 */
	public AppMainImp(int cmdSrvPort) {
		this();
		cmdSerivicePort = cmdSrvPort;
	}

	/**
	 * 启动命令服务
	 */
	final public void startCmdService() {
		if (!cmdServiceFlag) {
			cmdService = new CmdListener(cmdSerivicePort, this);
			cmdService.start();
			logger.info("CmdService started!");
			cmdServiceFlag = true;
		}
	}

	/**
	 * 发送命令参数 (一般开启一个新的进程去完成这个工作)
	 * 
	 * @param cmd
	 *            命令字符串
	 */
	final public void executeCmd(String cmd) {
		final Locker locker = new Locker();
		CmdVisitor cmdVistitor = new CmdVisitor(cmdSerivicePort, cmd, locker);
		cmdVistitor.start();
		try {
			locker.lockForTime(AppProperties.getAsInt("appsrv_cmd_waitForTime",
					1000 * 10));
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		} finally {
			cmdVistitor.stop();
			// System.exit(0);
			Runtime.getRuntime().exit(0);
		}
	}

	/**
	 * 监控后台应用线程
	 * 
	 * @param threadImp
	 *            应用线程实现对象
	 */
	final public void monitoThread(ThreadImp threadImp) {
		TMonitor.putIntoCache(threadImp);
		if (logger.isDebugEnabled()) {
			logger.debug("monitor:" + threadImp.getName());
		}
	}

	final public void unmonitorThread(ThreadImp threadImp) {
		TMonitor.removeFromCache(threadImp);
		if (logger.isDebugEnabled()) {
			logger.debug("unmonitor:" + threadImp.getName());
		}
	}

	/**
	 * 启动后台线程监控服务
	 */
	final public void startThreadMonitorService() {
		if (!this.monitorFlag) {
			TMonitor.startMonitor();
			logger.info("ThreadMonitorService started!");
			this.monitorFlag = true;
		}
	}

	/**
	 * 处理输入命令的抽象方法
	 * 
	 * 
	 * @param cmd
	 *            命令字符串
	 * @return 返回给客户端(shell)的信息
	 */
	protected abstract String dealCmd(String cmd);

	/**
	 * 关闭服务器时候触发的事件
	 */
	protected abstract void shutdownServerEvent();

	/**
	 * 启动服务器时触发的事件
	 */
	protected abstract void starServerEvent();

	/**
	 * 启动此应用服务器 (CmdService\MemoryWatcherService\ThreadMonitorService不会启动)
	 */
	final public void startServer() {
		if (this.mcs == null) {
			this.mcs = new MainCoreService();
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				if (logger.isDebugEnabled()) {
					logger.debug("jvm hook work...");
				}
				shutdownBeforeDo();
				if (logger.isDebugEnabled()) {
					logger.debug("jvm hook done.");
				}
			}
		});
		this.starServerEvent();
		this.mcs.start();
		logger.debug("AppMainImp Server started!");
	}

	/**
	 * 关闭此应用服务器
	 */
	final public void shutDownServer() {
		try {
			shutdownBeforeDo();
		} finally {
			// System.exit(0);
			Runtime.getRuntime().exit(0);
		}
	}

	private static boolean shutdownBeforeDoFlag = false;

	private void shutdownBeforeDo() {
		if (!shutdownBeforeDoFlag) {
			logger.info("Application Server shutdown..");
			try {
				Thread.sleep(2048);
				this.mcs.stop();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			} finally {
				this.shutdownServerEvent();
				shutdownBeforeDoFlag = true;
			}
		}
	}

	private class MainCoreService extends RunWrapper {

		public MainCoreService() {
			super("MainCoreService");
		}

		protected void stopEvent() {
			if (cmdServiceFlag) {
				cmdService.stop();
			}
			if (monitorFlag) {
				TMonitor.stopMonitor();
			}
			if (watcherFlag) {
				amw.stop();
			}
		}

		public void run() {
			while (!this.getStopFlag()) {
				// ...预留，以便以后做自检
				sleep(1000 * 30);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("application server stopped!");
			}
			this.stop();
		}
	}

	/**
	 * 命令监听器
	 * 
	 * 
	 */
	private static class CmdListener extends RunWrapper {
		/**
		 * 
		 */
		private ServerSocket theServerSocket;
		private int port;
		private final AppMainImp amp;

		public CmdListener(int port, final AppMainImp amp) {
			super();
			this.port = port;
			this.amp = amp;
			init();
		}

		private void init() {
			try {
				theServerSocket = new ServerSocket();
				theServerSocket.bind(new InetSocketAddress("127.0.0.1", port));
				theServerSocket.setReuseAddress(true);
				logger.info("CmdListener started and the port:" + port);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		protected void stopEvent() {
			try {
				this.theServerSocket.close(); // 关闭ServerSocket
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			logger.info("stop the CmdListener");
		}

		final public void run() {

			while (!this.getStopFlag()) {
				Socket theClientSocket = null;
				BufferedReader reader = null;
				PrintWriter out = null;
				try {
					theClientSocket = this.theServerSocket.accept();
					reader = new BufferedReader(new InputStreamReader(
							theClientSocket.getInputStream()));
					while (true) {
						String aStr = reader.readLine();
						if (aStr != null) {
							logger.info("receive shell cmd:{" + aStr + "}");
							String rstr = amp.dealCmd(aStr);
							if (rstr != null && rstr.trim().length() > 0) {
								out = new PrintWriter(
										theClientSocket.getOutputStream());
								out.println(rstr);
								out.flush();
							}
						} else {
							break;
						}
					}
				} catch (java.net.SocketException se) {
				} catch (Throwable e) {
					logger.error(e);
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (out != null) {
						out.close();
					}
					if (theClientSocket != null) {
						try {
							theClientSocket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				// sleep(1000);
			}
		}

	}

	/**
	 * 命令发送器
	 * 
	 */
	private static class CmdVisitor extends RunWrapper {
		/**
		 * 
		 */
		private int port;
		private Socket theSocket;
		private String cmdStr;
		private final Locker lock;

		public CmdVisitor(int port, String cmdStr, Locker lock) {
			this.port = port;
			this.cmdStr = cmdStr;
			this.lock = lock;
			init();
		}

		private void init() {
			try {
				theSocket = new Socket("127.0.0.1", this.port);
			} catch (Exception e) {
				logger.error(e);
			}
		}

		protected void stopEvent() {
			try {
				if (theSocket != null && !theSocket.isClosed())
					theSocket.close();
			} catch (Exception e) {
				logger.error(e);
				// 若run正常结果，则已经关闭过
			}
		}

		public void run() {
			OutputStream os = null;
			InputStream is = null;
			try {
				os = theSocket.getOutputStream();
				is = theSocket.getInputStream();
				PrintWriter out = new PrintWriter(os);
				out.println(cmdStr);
				out.flush();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				while (true) {
					String rstr = reader.readLine();
					if (rstr == null || rstr.trim().length() == 0) {
						break;
					} else {
						// logger.info("server say:{}", rstr);
						System.out.println(rstr);// 输出到控制台，以便管道捕捉
					}
				}
			} catch (Exception e) {
				// logger.error("Cmd Serivice not start,please check it!", e);
			} finally {
				try {
					if (os != null) {
						os.close();
					}
					if (is != null) {
						is.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				lock.unlock();
			}
		}
	}
}
