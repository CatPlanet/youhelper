package eu.kaguya.youhelper.core;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import eu.kaguya.youhelper.ItemStatus;
import eu.kaguya.youhelper.config.YouHelperConfiguration;

public class InfoGrabber {
	
	// runnable task for scheduled json info grab
	// structure to kill for
	
	class InfoGrabberTask implements Runnable {
		@Override
		public void run() {
			System.out.println("Grabbing info");
			List<ItemStatus> list = null;
			synchronized (InfoGrabber.this.pendingList) {
				list = InfoGrabber.this.pendingList.stream().limit(10).collect(Collectors.toList()); //TODO config
				InfoGrabber.this.pendingList.removeAll(list);
				System.out.println("Grabbed "+list.size()+" items");
			}
			if(!list.isEmpty()){
				DownloadProcessor processor = new DownloadProcessor();
				final List<ItemStatus> finalList = list; //thanks, java
				processor.setDumpListener((d) -> {
					List<ItemStatus> statuses = finalList.stream().filter(i -> i.getRequestedURL().toString().equals(d.getWebpage_url())).collect(Collectors.toList());
					if(statuses.isEmpty()) System.err.println(""+d.getWebpage_url()+" --> ?"); //TODO uh, it can happen? never too sure
					else { 
						statuses.stream().forEach((s) -> {
							s.populateWith(d);
						});
						
						ThumbnailDownloaderTask tdt = new ThumbnailDownloaderTask(statuses, d);
						InfoGrabber.this.thumbnailGrabberExecutor.execute(tdt);
					}
				});
				
				File dir = new File(InfoGrabber.this.config.directory());
				File executable = new File(dir, InfoGrabber.this.config.executable());

				try {
					//TODO god left me unfinished
					String[] urls = list.stream().map(i -> i.getRequestedURL().toString()).distinct().toArray(String[]::new);
					String[] params = Stream.concat(Arrays.stream(new String[]{executable.toString(), "--dump-json"}), Arrays.stream(urls)).toArray(String[]::new);
					Process p = new ProcessBuilder(params)
							.directory(dir)
							.redirectErrorStream(true)
							.start();
					try(Scanner s = new Scanner(p.getInputStream())){
						while(s.hasNextLine()) processor.consume(s.nextLine());
					}
				} catch (Exception exception) {
					//TODO
					exception.printStackTrace();
				}
				// finished
				pokeScheduler();
			}
			// else - if there is nothing to grab just end, poke if you want to schedule something later
		}
	}
	
	// runnable task for thumbnail download
	
	class ThumbnailDownloaderTask implements Runnable {
		private List<ItemStatus> statuses;
		private JsonDump dump;

		public ThumbnailDownloaderTask(List<ItemStatus> statuses, JsonDump dump) {
			this.statuses = statuses;
			this.dump = dump;
		}
		
		@Override
		public void run() {
			System.out.println("Downloading thumbnail");
			if(dump.getThumbnail() == null || !dump.getThumbnail().isEmpty()){
				try {
					BufferedImage img = ImageIO.read(new URL(dump.getThumbnail()));
					BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
					Graphics2D g = newImage.createGraphics();
					g.drawImage(img, 0, 0, null);
					g.dispose();

					this.statuses.stream().forEach((s) -> s.getView().fetchingComplete(newImage));
				} catch (Exception e) {
					this.statuses.stream().forEach((s) -> s.getView().fetchingError(e));
				}
				System.out.println("Thumbnail downloaded for "+statuses.size()+" status(es)");
			}
		}
		
	}

	// fields
	
	private ExecutorService thumbnailGrabberExecutor;
	private List<ItemStatus> pendingList;
	private ScheduledThreadPoolExecutor infoGrabberExecutor;
	private Runnable runnableTask;
	private ScheduledFuture<?> future;
	private YouHelperConfiguration config;

	// const
	
	public InfoGrabber(YouHelperConfiguration config){
		this.thumbnailGrabberExecutor = Executors.newCachedThreadPool(new DaemonThreadFactory()); //TODO: config
		this.pendingList = Collections.synchronizedList(new ArrayList<ItemStatus>());
		this.infoGrabberExecutor = new ScheduledThreadPoolExecutor(1, new DaemonThreadFactory()); //TODO: config
		this.infoGrabberExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
		this.infoGrabberExecutor.setRemoveOnCancelPolicy(true);
		this.infoGrabberExecutor.prestartAllCoreThreads();
		this.runnableTask = new InfoGrabberTask();
		this.config = config;
	}
	
	// public API
	
	public void requestInfoGrab(ItemStatus itemStatus){
		synchronized(this.pendingList){
			this.pendingList.add(itemStatus);
		}
		pokeScheduler();
	}

	// helper methods
	
	//TODO: probably unsafe af, worth checking
	private void pokeScheduler() {
		System.out.println("Adding runnable task...");
		if(this.future != null) this.future.cancel(false);
		addTask();
	}

	private void addTask() {
		synchronized(this.pendingList){
			if(this.pendingList.isEmpty()) return;
		}
		this.future = this.infoGrabberExecutor.schedule(this.runnableTask, 100, TimeUnit.MILLISECONDS); //TODO config
	}
}
