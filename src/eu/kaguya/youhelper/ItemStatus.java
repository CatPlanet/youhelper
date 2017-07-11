package eu.kaguya.youhelper;

import java.awt.Component;
import java.net.URL;
import java.util.UUID;

import javax.swing.SwingUtilities;

import eu.kaguya.youhelper.core.DownloadProcessor;
import eu.kaguya.youhelper.core.JsonDump;
import eu.kaguya.youhelper.ui.VisualItemStatus;

public class ItemStatus implements Comparable<ItemStatus> {

	private URL requestedURL;
	private final UUID uuid;
	
	private ItemStatusList itemStatusList;
	
	private float progress = -1;
	private boolean error = false;
	private boolean done = false;
	private volatile boolean canceled = false;
	
	private int order;
	
	private VisualItemStatus visualComponent;
	private DownloadProcessor processor;

	public ItemStatus(ItemStatusList itemStatusList, URL url, int order) {
		this.uuid = UUID.randomUUID();
		this.itemStatusList = itemStatusList;
		this.order = order;
		this.requestedURL = url;
		this.visualComponent = new VisualItemStatus(this);
		this.processor = new DownloadProcessor(this);
	}
	
	@Override
	public int compareTo(ItemStatus o) {
		return Integer.compare(order(), o.order());
	}
	
	@Override
	public int hashCode(){
		return 42 + super.hashCode() + requestedURL.hashCode();
	}

	public void update(float progress, String size, String speed, String eta) {
		this.progress = progress;
		SwingUtilities.invokeLater(() -> {
			this.visualComponent.setCurrentProgress(false, progress, size, speed, eta);
		});
	}

	public void validateCompletion() {
		if(this.progress == 100) {
			SwingUtilities.invokeLater(() -> {
				this.visualComponent.setCurrentProgress(false, progress, null, null, null);
			});
		}
	}

	public void error() {
		// this.progress = -1;
		this.canceled = false;
		this.done = false;
		this.error = true;
	}

	public void start() {
		this.progress = -1;
		this.canceled = false;
		this.done = false;
		this.error = false;
		this.itemStatusList.runTask(this);
	}
	
	public void cancel() {
		// this.progress = -1;
		this.canceled = true;
		this.done = false;
		this.error = false;
		this.itemStatusList.cancelTask(this);
	}
	
	public void remove() {
		this.progress = -1;
		this.canceled = true;
		this.done = false;
		this.error = false;
		this.itemStatusList.removeTask(this);
	}
	
	public void settings() {
		// TODO Auto-generated method stub
		
	}
	
/////////////// setters getters

	public int order(){
		return order;
	}
	
	public boolean hasError(){
		return this.error;
	}

	public boolean isCanceled(){
		return canceled;
	}
	
	public Component getView(){
		return this.visualComponent;
	}

	public boolean isDone() {
		return done;
	}

	public String uuid(){
		return this.uuid.toString();
	}

	public DownloadProcessor getProcessor() {
		return this.processor;
	}
	
	public URL getRequestedURL(){
		return this.requestedURL;
	}

	public void populateWith(JsonDump dump) {
		this.visualComponent.populateWith(dump, this.requestedURL);
	}

}
