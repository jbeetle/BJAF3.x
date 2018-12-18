package com.beetle.framework.business.common.tst.aop;

import java.lang.reflect.Method;

import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.business.command.CommandException;
import com.beetle.framework.business.command.CommandExecutor;
import com.beetle.framework.business.command.CommandImp;
import com.beetle.framework.resource.watch.WatchHelper;
import com.beetle.framework.resource.watch.WatchInfo;
import com.beetle.framework.util.cache.ConcurrentCache;
import com.beetle.framework.util.cache.ICache;
import com.beetle.framework.util.thread.task.TaskExecutor;
import com.beetle.framework.util.thread.task.TaskImp;

public class ServiceTransactionInterceptor {
	private final static ICache KFC = new ConcurrentCache(1334);
	private final static String TMP_KEY = "ysc@20090521";

	private static Object dealWithTransaction(Method method, Object[] args, Object imp) throws Throwable {
		Cmd cmd = new Cmd();
		cmd.setArgs(args);
		cmd.setMethod(method);
		cmd.setImpObj(imp);
		cmd = (Cmd) CommandExecutor.executeWithTransaction(cmd, CommandExecutor.COMMON_EXECUTE);
		if (cmd.getReturnFlag() < 0) {
			if (cmd.getPlus() != null)
				throw (Throwable) cmd.getPlus();
			throw new AppRuntimeException(cmd.getReturnFlag(), cmd.getReturnMsg());
		}
		return cmd.getResult();
	}

	public static Object invokeRequired(Object targetImp, Method method, Object[] args) throws Throwable {
		String kfc = (String) KFC.get(Thread.currentThread());// 为了支持事务嵌套，其实事务嵌套没毛用，反而把系统用复杂了，以后考虑去掉，20181218review
		if (kfc == null) {
			try {
				KFC.put(Thread.currentThread(), TMP_KEY);
				return dealWithTransaction(method, args, targetImp);
			} finally {
				KFC.remove(Thread.currentThread());
			}
		} else {
			return method.invoke(targetImp, args);
		}
	}

	public static Object invokeRequiresNew(Object targetImp, Method method, Object[] args) throws Throwable {
		TaskExecutor te = new TaskExecutor();
		te.addSubRoutine(new NewTransExeTask(targetImp, method, args));
		te.runRoutineEarly();
		Object x = te.getResult();
		if (x instanceof Throwable) {
			throw (Throwable) x;
		}
		// return te.getResult();
		return x;
	}

	private static class NewTransExeTask extends TaskImp {
		private final Object targetImp;
		private final Method method;
		private final Object[] args;

		public NewTransExeTask(Object targetImp, Method method, Object[] args) {
			super();
			this.targetImp = targetImp;
			this.method = method;
			this.args = args;
		}

		@Override
		protected void routine() throws InterruptedException {
			try {
				Object o = invokeRequired(targetImp, method, args);
				this.setResult(o);
			} catch (Throwable e) {
				// throw new InterruptedException(
				// AppLogger.getErrStackTraceInfo(e));
				// 不能吃掉异常，否则调用方无法获取业务异常
				this.setResult(e);
			}
		}

	}

	private static class Cmd extends CommandImp {
		private static final long serialVersionUID = 1L;
		private transient Method method;
		private Object[] args;
		private Object impObj;
		private Object result;

		public Cmd() {
			super();
		}

		public Object getResult() {
			return result;
		}

		public void setImpObj(Object impObj) {
			this.impObj = impObj;
		}

		public void setMethod(Method method) {
			this.method = method;
		}

		public void setArgs(Object[] args) {
			this.args = args;
		}

		@Override
		public void process() throws CommandException {
			try {
				WatchInfo wi = WatchHelper.currentWatch();
				if (wi != null) {
					wi.setId(impObj.getClass().getName() + "-" + method.getName());
				}
				this.result = method.invoke(impObj, args);
			} catch (Exception e) {
				CommandException ce = new CommandException(e);
				if (e instanceof java.lang.reflect.InvocationTargetException) {
					ce.setPlus(((java.lang.reflect.InvocationTargetException) e).getTargetException());
				}
				throw ce;
			}
		}

	}

}
