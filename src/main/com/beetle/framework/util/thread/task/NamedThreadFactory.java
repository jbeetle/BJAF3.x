package com.beetle.framework.util.thread.task;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

	private final AtomicInteger tconter = new AtomicInteger(0);
	final String namePrefix;
	final boolean isDaemon;

	public NamedThreadFactory(String preffix, boolean daemon) {
		this.namePrefix = preffix;
		isDaemon = daemon;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r, namePrefix + "[" + tconter.getAndIncrement()
				+ "]");
		t.setDaemon(isDaemon);
		if (t.getPriority() != Thread.NORM_PRIORITY) {
			t.setPriority(Thread.NORM_PRIORITY);
		}
		return t;
	}

}