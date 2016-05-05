package com.beetle.framework.util.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

public class NoBlockConcurrentQueue implements IQueue {
	private ConcurrentLinkedQueue<Object> q;

	public NoBlockConcurrentQueue() {
		this.q = new ConcurrentLinkedQueue<Object>();
	}

	public void push(Object obj) {
		q.add(obj);
	}

	public Object pop() {
		return q.poll();
	}

	public boolean isEmpty() {
		return q.isEmpty();
	}

	public void clear() {
		q.clear();
	}

	public int size() {
		return q.size();
	}

}
