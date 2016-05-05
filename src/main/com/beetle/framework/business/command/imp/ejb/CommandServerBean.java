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
package com.beetle.framework.business.command.imp.ejb;

import com.beetle.framework.business.command.CommandException;
import com.beetle.framework.business.command.CommandHelper;
import com.beetle.framework.business.command.CommandImp;
import com.beetle.framework.business.common.ejb.SessionEJBImp;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.resource.jta.ITransaction;
import com.beetle.framework.resource.jta.JTAFactory;

public class CommandServerBean extends SessionEJBImp {
	private static final long serialVersionUID = -19760224l;
	private static AppLogger logger = AppLogger
			.getInstance(CommandServerBean.class);

	public CommandImp executeCommandWithTransaction(CommandImp command) {
		CommandHelper.bind(); // 锟斤拷
		ITransaction tran = JTAFactory.getTransactionFromContainer();
		try {
			tran.begin();
			command.process();
			if (CommandHelper.isNeedToRollback()) {
				tran.rollback();
				logger.info("rollback by hand!!!");
			} else {
				tran.commit();
			}
		} catch (CommandException e) {
			// this.setRollbackOnly();
			tran.rollback();
			String m = e.getMessage();
			if (m != null
					&& m.equals(String.valueOf(CommandImp.FATAL_ERR_FLAG))) {
				logger.info("rollback by hand!");
			} else {
				logger.error(e);
				command.setReturnFlag(CommandImp.FATAL_ERR_FLAG);
				command.setReturnMsg(e.getMessage());
			}
		} catch (Exception e2) {
			tran.rollback();
			logger.error(e2);
			command.setReturnFlag(CommandImp.FATAL_ERR_FLAG);
			command.setReturnMsg(e2.getMessage());
		} finally {
			tran = null;
			CommandHelper.unbind();
		}
		return command;
	}

	public CommandImp executeCommand(CommandImp command) {
		CommandHelper.bind();
		try {
			command.process();
		} catch (CommandException e) {
			logger.error(e);
			command.setReturnFlag(CommandImp.FATAL_ERR_FLAG);
			command.setReturnMsg(e.getMessage());
		} finally {
			CommandHelper.unbind();
		}
		return command;
	}
}
