package eu.kaguya.youhelper.ui;

import java.net.URL;

import eu.kaguya.youhelper.youtubedl.YoutubeDL;

public interface VisualItemController {
	public boolean isThumbnailShowing();
	public void setThumbnailVisible(boolean visible);
	public void setThumbnailListener(ThumbnailListener listener);
	public ThumbnailListener getThumbnailListener();
	public void setCurrentID(String id);
	public void setCurrentFlagString(String flags);
	public void setCurrentProgress(boolean indeterminate, float progress, String size, String speed, String eta);
	public void setCurrentStatus(YoutubeDL.Status status);
	public void setCurrentService(URL serviceURL);
	public void setCurrentOrder(int order, int max);
}
