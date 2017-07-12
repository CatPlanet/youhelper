package eu.kaguya.youhelper.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import eu.kaguya.youhelper.ItemStatus;
import eu.kaguya.youhelper.core.JsonDump;
import eu.kaguya.youhelper.youtubedl.YoutubeDL.Status;

public class VisualItemStatus extends JPanel implements VisualItemController, ThumbnailListener {

	private static final long serialVersionUID = 379334889211992999L;

	private ThumbnailListener thumbnailListener;
	private Thumbnail thumbnail;
	private InfoPanel infoPanel;

	// constructor

	public VisualItemStatus(ItemStatus itemStatus) {
		configurePanel();
		makeUI(itemStatus);
	}


	// ui

	private void configurePanel() {
		setLayout(new GridBagLayout());
		setThumbnailListener(this);
		setFocusable(true);
		addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {}
			
			@Override
			public void focusGained(FocusEvent e) {
				if(thumbnail != null && isThumbnailShowing()){
					SwingUtilities.invokeLater(() -> {
						thumbnail.grabFocus();
						thumbnail.requestFocusInWindow();
					});
				}
			}
		});
	}

	private void makeUI(ItemStatus itemStatus) {
		this.thumbnail = makeThumbnail();
		this.infoPanel = makeBody(itemStatus);
	}

	private Thumbnail makeThumbnail() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0;
		c.weightx = 0;

		Thumbnail t = new Thumbnail();
		add(t,c);
		return t;
	}

	private InfoPanel makeBody(ItemStatus itemStatus) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = 0;
		c.weighty = 0;
		c.weightx = 1;

		InfoPanel i = new InfoPanel(itemStatus);
		add(i,c);
		return i;
	}

	// public API

	@Override
	public boolean isThumbnailShowing() {
		return this.thumbnail.isVisible();
	}

	@Override
	public void setThumbnailVisible(boolean visible) {
		this.thumbnail.setVisible(visible);
	}

	@Override
	public void setCurrentID(String id) {
		this.infoPanel.setCurrentID(id);
	}

	@Override
	public void setCurrentFlagString(String flags) {
		this.infoPanel.setCurrentFlagString(flags);
	}

	@Override
	public void setCurrentProgress(boolean indeterminate, float progress, String size, String speed, String eta) {
		this.infoPanel.setCurrentProgress(indeterminate, progress, size, speed, eta);
	}

	@Override
	public void setCurrentStatus(Status status) {
		this.infoPanel.setCurrentStatus(status);
	}

	@Override
	public void setCurrentService(URL serviceURL) {
		this.infoPanel.setCurrentService(serviceURL);
	}

	@Override
	public void setCurrentOrder(int order, int max) {
		this.infoPanel.setCurrentOrder(order, max);
	}

	// thumbnail operandi

	@Override
	public void setThumbnailListener(ThumbnailListener listener) {
		this.thumbnailListener = listener;
	}

	@Override
	public void fetchingComplete(BufferedImage thumbnailImage) {
		this.thumbnail.load(thumbnailImage);
		this.thumbnail.fetchingEnded();
	}

	@Override
	public void fetchingStarted() {
		this.thumbnail.fetchingStarted();
	}

	@Override
	public void fetchingError(Exception exception) {
		exception.printStackTrace();
		this.thumbnail.fetchingEnded();
	}

	@Override
	public ThumbnailListener getThumbnailListener() {
		return this.thumbnailListener;
	}


	public void populateWith(JsonDump dump, URL url) {
		this.thumbnail.fetchingStarted();
		this.thumbnail.setLikesDislikes(dump.getLike_count(), dump.getDislike_count());
		this.thumbnail.setDuration(dump.getDuration(), TimeUnit.SECONDS);
		this.infoPanel.setCurrentID(dump.getId());
		this.infoPanel.setCurrentService(url);
	}

}
