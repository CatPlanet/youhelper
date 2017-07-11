package eu.kaguya.youhelper.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class DaemonThreadFactory implements ThreadFactory {

	private final ThreadFactory threadFactory;
	
	public DaemonThreadFactory() {
		threadFactory = Executors.defaultThreadFactory();
	}
	
	@Override
	public Thread newThread(Runnable r) {
		Thread thread = threadFactory.newThread(r);
		thread.setDaemon(true);
		thread.setUncaughtExceptionHandler(new ResizeExceptionHandler(thread.getUncaughtExceptionHandler()));
		return thread;
	}

}
