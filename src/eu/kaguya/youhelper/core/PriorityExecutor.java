package eu.kaguya.youhelper.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@link ThreadPoolExecutor} for {@link CallableOrderedEarlyStatus} tasks <b>only</b>. Natural and easy priorities bought by {@link Comparable} interface.
 */
public class PriorityExecutor extends ThreadPoolExecutor {

	// default constructors
	public PriorityExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
			ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
	}

	public PriorityExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
			ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
	}

	public PriorityExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
			RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
	}

	public PriorityExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}
	
	// making better future, one at the time

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable){
		if(callable instanceof CallableOrderedEarlyStatus){
			CallableOrderedEarlyStatus ces = (CallableOrderedEarlyStatus) callable;
			return new ComparableFutureTask(ces);
		}
		throw new IllegalArgumentException("Callable not implementing CallableEarlyStatus.class");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value){
		if(value instanceof CallableOrderedEarlyStatus){
			CallableOrderedEarlyStatus ces = (CallableOrderedEarlyStatus) value;
			ComparableFutureTask future = new ComparableFutureTask(runnable, ces);
			return future;
		}
		if(value == null && runnable instanceof CallableOrderedEarlyStatus){
			CallableOrderedEarlyStatus ces = (CallableOrderedEarlyStatus) runnable;
			ComparableFutureTask future = new ComparableFutureTask(runnable, ces);
			return future;
		}
		throw new IllegalArgumentException("Runnable or/and value not implementing CallableEarlyStatus.class");
	}
}
