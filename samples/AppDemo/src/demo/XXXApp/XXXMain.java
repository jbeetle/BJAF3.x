package demo.XXXApp;

import com.beetle.framework.AppProperties;
import com.beetle.framework.appsrv.AppMainImp;
import com.beetle.framework.appsrv.plugin.PluginManager;
import com.beetle.framework.business.service.server.ServiceServer;
import com.beetle.framework.persistence.access.ConnectionFactory;
import com.beetle.framework.persistence.dao.DaoFactory;

import demo.XXXApp.plugin.IXXXPlugin;

public class XXXMain extends AppMainImp {
	private final ServiceServer rpcSrv;// 支持RPC对外服务暴露的Server

	public XXXMain(int cmdSrvPort) {
		super(cmdSrvPort);
		int rpcSrvPort = AppProperties.getAsInt("app_rpcSrvPort", 9090);
		rpcSrv = new ServiceServer(rpcSrvPort);
	}

	@Override
	protected String dealCmd(String cmd) {
		// 接受外部命令，并处理命令（外部命令一般是指shell脚本发出的）
		// 例如：
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
		} else if (cmd.equalsIgnoreCase("gc")) {
			System.gc();
		} else {
			sb.append("sorry,err cmd!\n");
		}
		return sb.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int localCmdSrvPort = AppProperties.getAsInt("app_localCmdSrvPort",
				22476);
		XXXMain xm = new XXXMain(localCmdSrvPort);
		if (args != null && args.length == 1) {
			xm.executeCmd(args[0]);
		} else {
			xm.startServer();// 启动服务器
			xm.startCmdService();// 启动外部命令接受服务
			xm.startMemoryWatcherService();// 启动内存监控服务
			xm.startThreadMonitorService();// 启动线程监控服务
		}
	}

	@Override
	protected void shutdownServerEvent() {
		// 关闭服务器时，触发的动作，在此事件中释放整个应用资源，并处理应用状态数据的持久化保护工作
		rpcSrv.stop();// 关闭rpc服务
		PluginManager.getInstance().clear();// 清空插件容器资源
		// ...
	}

	@Override
	protected void starServerEvent() {
		// 启动服务时出发的事件，在此事件中初始化整个应用的相关资源
		ConnectionFactory.initializeAllDataSources();// 初始化数据源（连接池）
		DaoFactory.initialize();// 初始化dao对象
		PluginManager.getInstance().initialize(IXXXPlugin.class);// 初始化插件容器
		rpcSrv.start();// 启动rpc服务
		// ...
	}
}
