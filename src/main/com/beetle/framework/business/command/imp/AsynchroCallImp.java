package com.beetle.framework.business.command.imp;

import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

import com.beetle.framework.AppProperties;
import com.beetle.framework.business.command.CommandExecutor;
import com.beetle.framework.business.command.CommandImp;
import com.beetle.framework.business.command.ICommandCallBack;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.util.queue.BlockQueue;
import com.beetle.framework.util.queue.IQueue;
import com.beetle.framework.util.thread.ThreadImp;
import com.beetle.framework.util.thread.task.TaskExecutor;
import com.beetle.framework.util.thread.task.TaskImp;
import com.beetle.framework.util.thread.task.TaskThreadPool;

public class AsynchroCallImp {
	private static AsynchroCallImp instance = new AsynchroCallImp();
	private TaskThreadPool wtp;
	private IQueue cmdQueue;
	private Consumer consumer;

	public static AsynchroCallImp getInstance() {
		return instance;
	}

	private class Consumer extends ThreadImp {

		public Consumer(String threadName) {
			super(threadName);
		}

		protected void routine() {
			AsynTask task = (AsynTask) cmdQueue.pop();
			execute(task);
		}

	}

	public static class AsynTask {
		public int getCommandExecuteFlag() {
			return commandExecuteFlag;
		}

		public void setCommandExecuteFlag(int commandExecuteFlag) {
			this.commandExecuteFlag = commandExecuteFlag;
		}

		private CommandImp cmd;
		private ICommandCallBack back;
		private int dealFlag;
		private int commandExecuteFlag;

		public int getDealFlag() {
			return dealFlag;
		}

		public void setDealFlag(int dealFlag) {
			this.dealFlag = dealFlag;
		}

		public CommandImp getCmd() {
			return cmd;
		}

		public void setCmd(CommandImp cmd) {
			this.cmd = cmd;
		}

		public ICommandCallBack getBack() {
			return back;
		}

		public void setBack(ICommandCallBack back) {
			this.back = back;
		}
	}

	private static class Worker extends TaskImp {
		private AsynTask task;

		public Worker(AsynTask task) {
			super();
			this.task = task;
		}

		protected void routine() throws InterruptedException {
			try {
				if (task.getDealFlag() == 10) {
					task.getBack().handle(
							CommandExecutor.execute(task.getCmd()));
				} else if (task.getDealFlag() == 11) {
					task.getBack().handle(
							CommandExecutor.execute(task.getCmd(),
									task.getCommandExecuteFlag()));
				} else if (task.getDealFlag() == 20) {
					task.getBack().handle(
							CommandExecutor.executeWithTransaction(task
									.getCmd()));
				} else if (task.getDealFlag() == 21) {
					task.getBack()
							.handle(CommandExecutor.executeWithTransaction(
									task.getCmd(), task.getCommandExecuteFlag()));
				} else {
					throw new InterruptedException(
							"can't foun this case,please check command!");
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new InterruptedException(
						AppLogger.getErrStackTraceInfo(e));
			}
		}
	}

	private AsynchroCallImp() {
		String size = AppProperties.get("command_asynchro_call_queue_size");
		if (size == null || size.length() == 0) {
			size = "10";
		}
		int size2 = Integer.parseInt(size.trim());
		wtp = new TaskThreadPool(size2, size2, 5 * 1000 * 60, true,
				new CallerRunsPolicy());
		cmdQueue = new BlockQueue();
		consumer = new Consumer("AsynchroCallConsumer");
		consumer.start();
	}

	public void clear() {
		if (wtp != null) {
			wtp.shutdown();
			wtp = null;
		}
		if (consumer != null) {
			consumer.stop();
		}
	}

	public void addTask(AsynTask task) {
		cmdQueue.push(task);
	}

	private void execute(AsynTask task) {
		try {
			TaskExecutor re = new TaskExecutor(this.wtp);
			re.addSubRoutine(new Worker(task));
			re.runRoutine();
			re = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
