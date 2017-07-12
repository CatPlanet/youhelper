package eu.kaguya.youhelper.core;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.kaguya.youhelper.ItemStatus;
import eu.kaguya.youhelper.config.YouHelperConfiguration;

public class DownloaderTask implements Runnable, CallableOrderedEarlyStatus<DownloaderTask> {

	//TODO
	private static final Pattern progressPattern = Pattern.compile("\\[download\\]\\s+(.*?)%\\s+of\\s+(.*?)\\s+at\\s+(.*?)\\s+ETA\\s+(.*?)");
	private final ItemStatus status;
	private Process p;
	private YouHelperConfiguration config;

	public DownloaderTask(ItemStatus status, YouHelperConfiguration config) {
		this.status = status;
		this.config = config;
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
		if(this.status.isDone()) return this;
		if(isCanceled()) disrupt();

		System.out.println("Current thread: "+Thread.currentThread().getName());

		try {
			DownloadProcessor processor = this.status.getProcessor();
			File dir = new File(this.config.directory());
			File executable = new File(dir, this.config.executable());

			synchronized (this) {
				p = new ProcessBuilder(executable.toString(), "--restrict-filenames", status.getRequestedURL().toExternalForm())
						.directory(dir)
						.redirectErrorStream(true)
						.start();
			}
			try(Scanner s = new Scanner(p.getInputStream())){
				while(s.hasNextLine() && !isCanceled()){
					String m = s.nextLine();
					processor.consume(m);

					Matcher matcher = progressPattern.matcher(m);
					if(isCanceled()) disrupt();

					if(matcher.matches()){
						status.update(Float.parseFloat(matcher.group(1)), matcher.group(2), matcher.group(3), matcher.group(4));
					} else {
						System.out.println(">"+m);
					}
					if(m.contains("has already been downloaded")){ //TODO quick hack until download processor fix
						status.update(100, null, null, null);
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
