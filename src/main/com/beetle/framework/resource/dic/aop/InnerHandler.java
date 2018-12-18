package com.beetle.framework.resource.dic.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.beetle.framework.AppContext;
import com.beetle.framework.resource.dic.DIContainer;
import com.beetle.framework.resource.dic.ReleBinder;
import com.beetle.framework.resource.dic.ReleBinder.BeanVO;
import com.beetle.framework.resource.dic.def.AsyncMethodCallback;
import com.beetle.framework.resource.dic.def.ServiceTransaction;
import com.beetle.framework.resource.watch.WatchHelper;
import com.beetle.framework.resource.watch.WatchInfo;
import com.beetle.framework.util.thread.task.TaskExecutor;
import com.beetle.framework.util.thread.task.TaskImp;

public class InnerHandler implements InvocationHandler {
	private final Class<?> targetImpFace;
	private static final Map<Method, AopInterceptor> CACHE = new ConcurrentHashMap<Method, AopInterceptor>();

	private static class ExeTask extends TaskImp {
		private final Object targetImp;
		private final Method method;
		private final Object[] args;
		private final int pos;

		public ExeTask(Object targetImp, Method method, Object[] args, int pos) {
			super();
			this.targetImp = targetImp;
			this.method = method;
			this.args = args;
			this.pos = pos;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void routine() throws InterruptedException {
			try {
				method.invoke(targetImp, args);
			} catch (Exception e) {
				AsyncMethodCallback<?> cb = (AsyncMethodCallback<?>) args[pos];
				if (cb != null) {
					cb.onError(-10000, e.getMessage(), e);
				}
			} finally {
				@SuppressWarnings("rawtypes")
				AsyncMethodCallback cb = (AsyncMethodCallback) args[pos];
				if (cb != null) {
					cb.onComplete(cb.getResult());
				}
			}
		}
	}

	private static class ExeTransTask extends TaskImp {
		private final Object targetImp;
		private final Method method;
		private final Object[] args;
		private final int pos;

		public ExeTransTask(Object targetImp, Method method, Object[] args, int pos) {
			super();
			this.targetImp = targetImp;
			this.method = method;
			this.args = args;
			this.pos = pos;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void routine() throws InterruptedException {
			try {
				ServiceTransaction.Manner manner = BeanVO.getFromTrans(method);
				if (manner.equals(ServiceTransaction.Manner.REQUIRED)) {
					com.beetle.framework.business.common.tst.aop.ServiceTransactionInterceptor.invokeRequired(targetImp,
							method, args);
				} else if (manner.equals(ServiceTransaction.Manner.REQUIRES_NEW)) {
					com.beetle.framework.business.common.tst.aop.ServiceTransactionInterceptor
							.invokeRequiresNew(targetImp, method, args);
				}
			} catch (Throwable e) {
				AsyncMethodCallback<?> cb = (AsyncMethodCallback<?>) args[pos];
				if (cb != null) {
					cb.onError(-10000, e.getMessage(), e);
				}
			} finally {
				@SuppressWarnings("rawtypes")
				AsyncMethodCallback cb = (AsyncMethodCallback) args[pos];
				if (cb != null) {
					cb.onComplete(cb.getResult());
				}
			}
		}
	}

	public InnerHandler(Class<?> targetImpFace) {
		super();
		this.targetImpFace = targetImpFace;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		AopInterceptor interceptor = getInterceptor(method);
		if (interceptor != null) {
			if (interceptor.interrupt()) {
				return interceptor.interruptResult(proxy, method, args);
			}
			interceptor.before(method, args);
		}
		//
		Object targetImp = DIContainer.Inner.getBeanFromDIBeanCache(targetImpFace.getName());
		Object rs = null;
		if (BeanVO.existInTrans(method)) {
			rs = dealWithTrans(method, args, targetImp, rs);
		} else {
			try {
				rs = dealWithoutTrans(method, args, targetImp, rs);
			} catch (Exception e) {
				if (e instanceof java.lang.reflect.InvocationTargetException) {
					throw ((java.lang.reflect.InvocationTargetException) e).getTargetException();
				}
				throw e;
			}
		}
		//
		if (interceptor != null) {
			interceptor.after(rs, method, args);
		}
		return rs;
	}

	private static Object dealWithTrans(Method method, Object[] args, Object targetImp, Object rs) throws Throwable {
		if (BeanVO.existInAsync(method) && !AppContext.getInstance().exist(Thread.currentThread().getId())) {
			TaskExecutor te = new TaskExecutor();
			te.addSubRoutine(new ExeTransTask(targetImp, method, args, BeanVO.getFromAsync(method)));
			te.runRoutine();
		} else {
			ServiceTransaction.Manner manner = BeanVO.getFromTrans(method);
			if (manner.equals(ServiceTransaction.Manner.REQUIRED)) {
				rs = com.beetle.framework.business.common.tst.aop.ServiceTransactionInterceptor
						.invokeRequired(targetImp, method, args);
			} else if (manner.equals(ServiceTransaction.Manner.REQUIRES_NEW)) {
				rs = com.beetle.framework.business.common.tst.aop.ServiceTransactionInterceptor
						.invokeRequiresNew(targetImp, method, args);
			}
		}
		return rs;
	}

	private static Object dealWithoutTrans(Method method, Object[] args, Object targetImp, Object rs) throws Throwable {
		if (BeanVO.existInAsync(method) && !AppContext.getInstance().exist(Thread.currentThread().getId())) {
			TaskExecutor te = new TaskExecutor();
			te.addSubRoutine(new ExeTask(targetImp, method, args, BeanVO.getFromAsync(method)));
			te.runRoutine();
		} else {
			// 20181218加上监控控制低级程序员的无脑设计
			try {
				if (WatchHelper.isNeedWatch()) {
					WatchHelper.bind(new WatchInfo().setId(targetImp.getClass().getName() + "-" + method.getName()));
				}
				rs = method.invoke(targetImp, args);
			} finally {
				if (WatchHelper.isNeedWatch()) {
					WatchHelper.unbind();
				}
			}
		}
		return rs;
	}

	private final static Object lockObj = new Object();

	private static AopInterceptor getInterceptor(Method method) {
		AopInterceptor interceptor = CACHE.get(method);
		if (interceptor == null) {
			synchronized (lockObj) {
				if (interceptor == null) {
					ReleBinder binder = DIContainer.Inner.getReleBinder();
					List<BeanVO> tmpList = binder.getBeanVoList();
					for (BeanVO bvo : tmpList) {
						Method m = bvo.getAopMethod();
						if (m != null && m.equals(method)) {
							interceptor = bvo.getInterceptor();
							CACHE.put(method, interceptor);
							break;
						}
					}
				}
			}
		}
		return interceptor;
	}

}
