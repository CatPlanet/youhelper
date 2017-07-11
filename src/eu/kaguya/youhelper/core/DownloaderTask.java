package eu.kaguya.youhelper.core;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.kaguya.youhelper.App;
import eu.kaguya.youhelper.ItemStatus;

public class DownloaderTask implements Runnable, CallableOrderedEarlyStatus<DownloaderTask> {

	//TODO
	private static final Pattern progressPattern = Pattern.compile("\\[download\\]\\s+(.*?)%\\s+of\\s+(.*?)\\s+at\\s+(.*?)\\s+ETA\\s+(.*?)");
	private final ItemStatus status;
	private Process p;

	public DownloaderTask(ItemStatus status) {
		this.status = status;
		if(status == null) throw new NullPointerException("ItemStatus cannot be null");
	}

	public int order(){
		return status.order();
	}

	private boolean isCanceled(){
		boolean c = status.isCanceled() || Thread.currentThread().isInterrupted();
		//		if(c) System.out.println("TASK CANCELED");
		return c;
	}

	private void disrupt(){
		if(p != null){
			synchronized (this) {
				p.destroyForcibly();
				try {
					p.waitFor();
					//				System.out.println("DISRUPTED");
				} catch (InterruptedException e) {
					//				System.out.println("TRIED DISRUPTING");
				}
			}
		}
	}

	@Override
	public DownloaderTask call() throws Exception {
		if(status.isDone()) return this;
		if(isCanceled()) disrupt();

		System.out.println("Current thread: "+Thread.currentThread().getName());

		try {
			DownloadProcessor processor = status.getProcessor();
			String dir = App.DIR; //TODO

			//TODO only if no prefetching was made
			synchronized (this) {
				p = new ProcessBuilder(dir + "youtube-dl.exe","--dump-json", status.getRequestedURL().toExternalForm())
						.directory(new File(dir))
						.redirectErrorStream(true)
						.start();
			}
			try(Scanner s = new Scanner(p.getInputStream())){
				while(s.hasNextLine() && !isCanceled()){
					String m = s.nextLine();
					processor.consume(m);
				}
			}

			synchronized (this) {
				p = new ProcessBuilder(dir + "youtube-dl.exe","--restrict-filenames", status.getRequestedURL().toExternalForm())
						.directory(new File(dir))
						.redirectErrorStream(true)
						.start();
			}
			try(Scanner s = new Scanner(p.getInputStream())){
				while(s.hasNextLine() && !isCanceled()){
					String m = s.nextLine();
					processor.consume(m);

					System.out.println(">"+m);
					Matcher matcher = progressPattern.matcher(m);
					if(isCanceled()) disrupt();

					if(matcher.matches()){
						status.update(Float.parseFloat(matcher.group(1)), matcher.group(2), matcher.group(3), matcher.group(4));
					}
					if(m.startsWith("ERROR")){
						status.error();
					}
				}
			}

			processor.endConsuming();
			if(isCanceled()) disrupt();
			status.validateCompletion();
		} catch (IOException e) {
			status.error();
			throw e;
		}
		return this;
	}

	@Override
	public void run() {
		try {
			call();
		} catch (Exception e) {
			status.error();
			e.printStackTrace();
		}
	}

	@Override
	public int compareTo(DownloaderTask o) {
		return this.status.compareTo(o.status);
	}

	@Override
	public DownloaderTask getStatus() {
		return this;
	}

	@Override
	public void cancel() {
		//		System.out.println("CANCEL ACTIVATED, DISRUPTING...");
		disrupt();
	}

}
