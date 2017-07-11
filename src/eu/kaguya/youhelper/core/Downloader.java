package eu.kaguya.youhelper.core;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import eu.kaguya.youhelper.ItemStatus;
import eu.kaguya.youhelper.config.YouHelperConfiguration;

public class Downloader<T extends Runnable & Comparable<T>> {
	private PriorityExecutor service;
	private PriorityBlockingQueue<T> queue;
	private YouHelperConfiguration config;
	
	@SuppressWarnings("unchecked")
	public Downloader(int corePoolSize, YouHelperConfiguration config){
		queue = new PriorityBlockingQueue<>(11);
		this.config = config;
		service = new PriorityExecutor(corePoolSize, corePoolSize, 3L, TimeUnit.MINUTES, (BlockingQueue<Runnable>) queue, new DaemonThreadFactory());
		service.allowCoreThreadTimeOut(true);
		service.setKeepAliveTime(10, TimeUnit.SECONDS);
	}
	
	public Future<DownloaderTask> submit(ItemStatus item){
		DownloaderTask t = new DownloaderTask(item, config);
		Future<DownloaderTask> future = service.submit(t, t);
		return future;
	}
	
	public boolean cancel(Future<ItemStatus> item){
		return item.cancel(true);
	}
	
	public List<Runnable> shutdown(){
		queue.clear();
		return service.shutdownNow();
	}
}
