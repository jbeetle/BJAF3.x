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
import com.beetle.framework.business.command.CommandHelper;
import com.beetle.framework.business.command.CommandImp;
import com.beetle.framework.business.command.ICommandTarget;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.resource.jta.ITransaction;
import com.beetle.framework.resource.jta.JTAException;
import com.beetle.framework.resource.jta.JTAFactory;

public class PojoCommandTarget implements ICommandTarget {
	private static ICommandTarget instance = new PojoCommandTarget();
	private static AppLogger logger = AppLogger
			.getInstance(PojoCommandTarget.class);

	private PojoCommandTarget() {
		// WatchHelper.startWatch();
	}

	public static ICommandTarget getInstance() {
		return instance;
	}

	public CommandImp executeCommandWithTransation(CommandImp command)
			throws CommandExecuteException {
		ITransaction trans = null;
		try {
			trans = JTAFactory.getTransactionFromMock();
			CommandHelper.bind(trans);
			// trans = JTAFactory.getTransactionFromFramework();
			trans.begin();
			command.process();
			if (CommandHelper.isNeedToRollback()) {
				trans.rollback();
				logger.info("rollback by hand!!!");
			} else {
				trans.commit();
			}
			return command;
		} catch (CommandException e) {
			trans.rollback();
			String m = e.getMessage();
			if (m != null
					&& m.equals(String.valueOf(CommandImp.FATAL_ERR_FLAG))) {
				logger.info("rollback by hand!");
			} else {
				command.setReturnFlag(CommandImp.FATAL_ERR_FLAG);
				command.setReturnMsg(logger.getStackTraceInfo(e));
			}
			command.setPlus(e.getPlus());
			return command;
		} catch (JTAException jte) {
			throw new CommandExecuteException(jte);
		} catch (Exception e) {
			if (trans != null)
				trans.rollback();
			command.setReturnFlag(CommandImp.FATAL_ERR_FLAG);
			command.setReturnMsg(logger.getStackTraceInfo(e));
			command.setPlus(e);
			return command;
		} finally {
			// trans = null;
			CommandHelper.unbind();
		}
	}

	public CommandImp executeCommand(CommandImp command)
			throws CommandExecuteException {
		try {
			CommandHelper.bind();
			command.process();
		} catch (Exception ce) {
			command.setReturnFlag(CommandImp.FATAL_ERR_FLAG);
			command.setReturnMsg(logger.getStackTraceInfo(ce));
		} finally {
			CommandHelper.unbind();
		}
		return command;

	}
}
