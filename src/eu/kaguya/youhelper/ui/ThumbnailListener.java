package eu.kaguya.youhelper.ui;

import java.awt.image.BufferedImage;

public interface ThumbnailListener {
	public void fetchingComplete(BufferedImage thumbnailImage);
	public void fetchingStarted();
	public void fetchingError(Exception exception);
}
