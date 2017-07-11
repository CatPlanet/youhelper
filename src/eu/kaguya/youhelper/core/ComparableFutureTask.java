package eu.kaguya.youhelper.core;

import java.util.concurrent.FutureTask;

public class ComparableFutureTask<V extends Comparable<V>> extends FutureTask<V> implements Comparable<ComparableFutureTask<V>>{

	private Runnable runnable;
	private V result;
	
	public ComparableFutureTask(Runnable runnable, V result) {
		super(runnable, result);
		this.result = result;
		this.runnable = runnable;
	}
	
	public <T extends CallableOrderedEarlyStatus<V>> ComparableFutureTask(T callable){
		super(callable);
		this.result = callable.getStatus();
	}
	
	private V getResult(){
		return result;
	}

	@Override
	public int compareTo(ComparableFutureTask<V> o) {
		return getResult().compareTo(o.getResult());
	}
	
	@SuppressWarnings("rawtypes")
	public boolean cancel(boolean mayInterruptIfRunning){
		if(runnable != null && runnable instanceof CallableOrderedEarlyStatus){
			CallableOrderedEarlyStatus c = (CallableOrderedEarlyStatus) runnable;
			c.cancel();
		}
		if(result != null && result instanceof CallableOrderedEarlyStatus){
			CallableOrderedEarlyStatus c = (CallableOrderedEarlyStatus) result;
			c.cancel();
		}
		return super.cancel(mayInterruptIfRunning);
	}
}
