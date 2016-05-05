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
package com.beetle.framework.business.command;

import com.beetle.framework.AppProperties;
import com.beetle.framework.business.command.imp.*;
import com.beetle.framework.business.command.imp.AsynchroCallImp.AsynTask;

public final class CommandExecutor {
	/**
	 * EJB本地接口方法执行
	 */
	public final static int EJB_LOCAL_EXECUTE = 1;
	/**
	 * EJB远程接口方式执行
	 */
	public final static int EJB_REMOTEL_EXECUTE = 2;
	/**
	 * 普通的本地jvm方式执行
	 */
	public final static int COMMON_EXECUTE = 3;
	/**
	 * 框架实现http远程方式执行
	 */
	public final static int RPC_EXECUTE = 4;
	private static int exe_flag = -1;
	static {
		// exe_flag = Integer.parseInt(BusinessConfigReader
		// .getCommandLogicValue("COMMAND_EXECUTE_FLAG"));
	}

	/**
	 * 以事务方式执行command业务对象
	 * 
	 * @param command
	 *            CommandImp
	 * @return 返回执行后command对象（执行后command为一个纯数据对象，只作为结果传送，是dto）
	 * 
	 * @throws CommandExecuteException
	 */

	public static CommandImp executeWithTransaction(CommandImp command)
			throws CommandExecuteException {
		if (exe_flag == -1) {
			exe_flag = AppProperties.getAsInt("command_execute_flag");
		}
		return executeWithTransaction(command, exe_flag);
	}

	/**
	 * 以非事务方式执行command业务对象
	 * 
	 * @param command
	 *            CommandImp
	 * @return 返回执行后command对象（执行后command为一个纯数据对象，只作为结果传送，是dto）
	 * 
	 * @throws CommandExecuteException
	 */
	public static CommandImp execute(CommandImp command)
			throws CommandExecuteException {
		if (exe_flag == -1) {
			exe_flag = AppProperties.getAsInt("command_execute_flag");
		}
		return execute(command, exe_flag);
	}

	/**
	 * 异步执行，非阻塞<br>
	 * 提交命令，后台执行，并通过回调来处理命令执行的结果
	 * 
	 * @param command
	 *            --输入命令
	 * @param callBack
	 *            --结果处理回调
	 * @throws CommandExecuteException
	 */
	public static void asynchroExecute(CommandImp command,
			ICommandCallBack callBack) throws CommandExecuteException {
		try {
			AsynTask at = new AsynTask();
			at.setBack(callBack);
			at.setCmd(command);
			at.setDealFlag(10);
			AsynchroCallImp.getInstance().addTask(at);
		} catch (Exception e) {
			throw new CommandExecuteException(e);
		}
	}

	public static void asynchroExecute(CommandImp command,
			int commandExecuteFlag, ICommandCallBack callBack)
			throws CommandExecuteException {
		try {
			AsynTask at = new AsynTask();
			at.setBack(callBack);
			at.setCmd(command);
			at.setDealFlag(11);
			at.setCommandExecuteFlag(commandExecuteFlag);
			AsynchroCallImp.getInstance().addTask(at);
		} catch (Exception e) {
			throw new CommandExecuteException(e);
		}
	}

	public static void asynchroExecuteWithTransaction(CommandImp command,
			int commandExecuteFlag, ICommandCallBack callBack)
			throws CommandExecuteException {
		try {
			AsynTask at = new AsynTask();
			at.setBack(callBack);
			at.setCmd(command);
			at.setDealFlag(21);
			at.setCommandExecuteFlag(commandExecuteFlag);
			AsynchroCallImp.getInstance().addTask(at);
		} catch (Exception e) {
			throw new CommandExecuteException(e);
		}
	}

	public static void asynchroExecuteWithTransaction(CommandImp command,
			ICommandCallBack callBack) throws CommandExecuteException {
		try {
			AsynTask at = new AsynTask();
			at.setBack(callBack);
			at.setCmd(command);
			at.setDealFlag(20);
			AsynchroCallImp.getInstance().addTask(at);
		} catch (Exception e) {
			throw new CommandExecuteException(e);
		}
	}

	/**
	 * 以非事务方式执行command业务对象
	 * 
	 * @param command
	 *            CommandImp
	 * @param commandExecuteFlag
	 *            --执行方式标记
	 * @return 返回执行后command对象（执行后command为一个纯数据对象，只作为结果传送，是dto）
	 * 
	 * @throws CommandExecuteException
	 */
	public static CommandImp execute(CommandImp command, int commandExecuteFlag)
			throws CommandExecuteException {
		ICommandTarget target = getTarget(commandExecuteFlag);
		return target.executeCommand(command);
	}

	/**
	 * 以事务方式执行command业务对象
	 * 
	 * @param command
	 *            CommandImp
	 * @param commandExecuteFlag
	 *            --执行方式标记
	 * @return 返回执行后command对象（执行后command为一个纯数据对象，只作为结果传送，是dto）
	 * 
	 * @throws CommandExecuteException
	 */

	public static CommandImp executeWithTransaction(CommandImp command,
			int commandExecuteFlag) throws CommandExecuteException {
		ICommandTarget target = getTarget(commandExecuteFlag);
		return target.executeCommandWithTransation(command);
	}

	private static ICommandTarget getTarget(int commandExecuteFlag) {
		ICommandTarget target;
		switch (commandExecuteFlag) {
		case EJB_LOCAL_EXECUTE:
			target = EJBCommandLocalTarget.getInstance();
			break;
		case EJB_REMOTEL_EXECUTE:
			target = EJBCommandTarget.getInstance();
			break;
		case COMMON_EXECUTE:
			target = PojoCommandTarget.getInstance();
			break;
		case RPC_EXECUTE:
			target = RPCCommandTarget.getInstance();
			break;
		default:
			target = EJBCommandLocalTarget.getInstance();
		}
		return target;
	}
}
