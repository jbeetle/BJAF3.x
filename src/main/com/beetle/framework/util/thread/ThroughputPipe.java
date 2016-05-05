package com.beetle.framework.util.thread;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

/**
 * 管道流量控制<br>
 * 建立一个流量管道，控制此管道的消费速度
 * 
 * @param <T>--通过管道的对象
 */
public class ThroughputPipe<T> {
	private BlockingQueue<T> queue;
	private final IConsumeHandler<T> handler;
	private ThreadPoolExecutor tpe;
	private int livetime = 60 * 1000 * 5;
	private final long stepOfDispatcher;

	private static class Cmd<T> implements Runnable {
		private T t;
		private IConsumeHandler<T> handler;

		public Cmd(T t, final IConsumeHandler<T> handler) {
			super();
			this.t = t;
			this.handler = handler;
		}

		@Override
		public void run() {
			try {
				handler.handle(t);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	private volatile boolean f = false;

	/**
	 * 启动管道流量控制，而且只能启动一次。
	 */
	public synchronized void start() {
		if (!f) {
			f = true;
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						try {
							if (!f) {
								break;
							}
							T t = queue.take();
							if (tpe != null) {
								tpe.execute(new Cmd<T>(t, handler));
							} else {
								new Cmd<T>(t, handler).run();
							}
							if (stepOfDispatcher > 0) {
								Thread.sleep(stepOfDispatcher);
							}
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}
			}, "ThroughputPipe-Dispatcher");
			t.setDaemon(true);
			t.start();
		}
	}

	/**
	 * 停止控制器并回收资源。停止后，此实例不再可用。<br>
	 * 注意：如果想重新进行容量控制功能，必须重新建立一个新的实例。
	 */
	public void stop() {
		f = false;
		if (tpe != null) {
			tpe.shutdown();
		}
		queue.clear();
	}

	/**
	 * 流量管道（流量控制器）<br>
	 * 使用此构造函数，为单线程消息消费，只适合消费顺序有严格要求的场景<br>
	 * 
	 * @param pipeSize
	 *            --管道大小
	 * @param handler
	 *            --消费接口实现
	 * @param stepOfDispatcher
	 *            --流量消费指派速度参数，单位ms毫秒。例如，每秒消费1个请求，则值为1000ms<br>
	 *            特别地，值为0时，指派速度没有限制，管道流量消费速度只依赖于线程池的大小及handler计算速度。
	 */
	public ThroughputPipe(int pipeSize, IConsumeHandler<T> handler,
			long stepOfDispatcher) {
		if (null == handler) {
			throw new RuntimeException("handle can't be null!");
		}
		this.handler = handler;
		this.queue = new LinkedBlockingQueue<T>(pipeSize);
		this.stepOfDispatcher = stepOfDispatcher;
		this.tpe = null;
	}

	/**
	 * 流量管道（流量控制器）
	 * 
	 * @param pipeSize
	 *            --管道大小
	 * @param poolSize
	 *            --流量消费线程池大小
	 * @param handler
	 *            --消费接口实现
	 * @param stepOfDispatcher
	 *            --流量消费指派速度参数，单位ms毫秒。例如，每秒消费1个请求，则值为1000ms<br>
	 *            特别地，值为0时，指派速度没有限制，管道流量消费速度只依赖于线程池的大小及handler计算速度。
	 */
	public ThroughputPipe(int pipeSize, int poolSize,
			IConsumeHandler<T> handler, long stepOfDispatcher) {
		if (pipeSize <= 0 || poolSize <= 0) {
			throw new RuntimeException("size's value must >0!");
		}
		if (null == handler) {
			throw new RuntimeException("handle can't be null!");
		}
		this.queue = new LinkedBlockingQueue<T>(pipeSize);
		this.handler = handler;
		tpe = new ThreadPoolExecutor(poolSize, poolSize, livetime,
				TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(),
				new CallerRunsPolicy());
		this.stepOfDispatcher = stepOfDispatcher;
	}

	/**
	 * 管道消费处理接口
	 * 
	 * @param <T>
	 */
	public static interface IConsumeHandler<T> {
		void handle(T value);
	}

	public static class PipeFullException extends Exception {

		private static final long serialVersionUID = 3819998391639391211L;

		public PipeFullException(String message) {
			super(message);
		}

	}

	/**
	 * @param values
	 * @throws PipeFullException
	 */
	public void put(List<T> values) throws PipeFullException {
		try {
			queue.addAll(values);
		} catch (java.lang.IllegalStateException e) {
			throw new PipeFullException("Pipe full!");
		}
	}

	/**
	 * 获取当前管道流量的大小
	 * 
	 * @return
	 */
	public int getThroughputPipeSize() {
		return queue.size();
	}

	/**
	 * 往管道加入数据，<br>
	 * 如果管道容量已经满了，会抛出 PipeFullException异常
	 * 
	 * @param values
	 * @throws PipeFullException
	 */
	public void put(T... values) throws PipeFullException {
		for (T v : values) {
			try {
				queue.add(v);
			} catch (java.lang.IllegalStateException e) {
				throw new PipeFullException("Pipe full!");
			}
		}
	}

}
