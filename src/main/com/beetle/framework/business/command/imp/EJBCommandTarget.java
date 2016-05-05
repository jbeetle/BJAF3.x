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
package com.beetle.framework.business.command.imp;

import com.beetle.framework.business.command.CommandException;
import com.beetle.framework.business.command.CommandExecuteException;
import com.beetle.framework.business.command.CommandImp;
import com.beetle.framework.business.command.ICommandTarget;
import com.beetle.framework.business.command.imp.ejb.CommandServerFactory;

public class EJBCommandTarget implements ICommandTarget {
	private static ICommandTarget instance = new EJBCommandTarget();

	private EJBCommandTarget() {
	}

	public static ICommandTarget getInstance() {
		return instance;
	}

	/**
	 * executeCommand
	 * 
	 * @param command
	 *            CommandImp
	 * @throws CommandException
	 * @return CommandImp
	 * @todo Implement this com.beetle.framework.pattern.cmd.ICommandTarget
	 *       method
	 */
	public CommandImp executeCommand(CommandImp command)
			throws CommandExecuteException {
		try {
			command = CommandServerFactory.getCommandServer().executeCommand(
					command);
		} catch (Exception e) {
			throw new CommandExecuteException(e);
		}
		return command;
	}

	public CommandImp executeCommandWithTransation(CommandImp command)
			throws CommandExecuteException {
		try {
			command = CommandServerFactory.getCommandServer()
					.executeCommandWithTransaction(command);
		} catch (Exception e) {
			throw new CommandExecuteException(e);
		}
		return command;

	}
}
