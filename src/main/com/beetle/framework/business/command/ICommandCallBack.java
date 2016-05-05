package com.beetle.framework.business.command;

/**
 * 异步回调处理结果接口
 * 
 */
public interface ICommandCallBack {
	/**
	 * 结果处理
	 * @param resultCmd--执行返回的Cmd
	 * @throws CommandException
	 */
	void handle(CommandImp resultCmd) throws CommandException;
}
