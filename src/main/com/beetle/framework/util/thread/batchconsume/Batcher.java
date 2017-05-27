package com.beetle.framework.util.thread.batchconsume;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.beetle.framework.util.thread.RunWrapper;

public class Batcher {
	private static class Dispatcher extends RunWrapper {
		private final int size;
		private final int interval;
		private final ConsumeWorker worker;
		private final ConcurrentLinkedDeque<Object> deque;
		private final List<Object> bufferList = new java.util.LinkedList<>();

		public Dispatcher(int size, int interval, ConsumeWorker worker, ConcurrentLinkedDeque<Object> deque) {
			super("Batcher[" + System.currentTimeMillis() + "]");
			this.size = size;
			this.interval = interval;
			this.worker = worker;
			this.deque = deque;
		}

		public void run() {
			while (true) {
				this.sleep(interval);
				fill();
				if (!bufferList.isEmpty()) {
					try {
						this.worker.handle(bufferList);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						bufferList.clear();
					}
				}
			}
		}

		private void fill() {
			for (int i = 0; i < size; i++) {
				Object o = deque.pollLast();
				if (o != null) {
					bufferList.add(o);
				}
			}
		}
	}

	private final ConcurrentLinkedDeque<Object> deque;

	private int size;
	private int interval;
	private Dispatcher dp;

	public Batcher(ConsumeWorker worker) {
		super();
		this.deque = new ConcurrentLinkedDeque<>();
		this.size = 500;
		this.interval = 1000 * 5;
		dp = new Dispatcher(size, interval, worker, deque);
		dp.start();
	}

	public Batcher(ConsumeWorker worker, int size, int interval) {
		this.deque = new ConcurrentLinkedDeque<>();
		this.size = size;
		this.interval = interval;
		dp = new Dispatcher(size, interval, worker, deque);
		dp.start();
	}

	public void put(Object data) {
		this.deque.add(data);
	}

	public void stop() {
		dp.stopBrutally();
	}

}
