package eu.kaguya.youhelper.core;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.kaguya.youhelper.ItemStatus;

@SuppressWarnings("unused")
public class DownloadProcessor {
	public enum DownloadMode {
		STANDARD, INFO, END
	}

	private ItemStatus status;
	DownloadMode mode = DownloadMode.STANDARD;
	
	private static final Pattern downloadProgressPattern = Pattern.compile("\\[download\\]\\s+(.*?)\\s+of\\s+(.*?)\\s+at\\s+(.*?)\\s+ETA\\s+(.*?)");
	private static final Pattern downloadFinishedPattern = Pattern.compile("\\[download\\]\\s+(.*?)\\s+of\\s+(.*?)\\s+in\\s+(.*?)");
	private static final Pattern downloadFinishedWithoutProgressBarPattern = Pattern.compile("\\[download\\] Download completed");
	private static final Pattern downloadDestinationPattern = Pattern.compile("\\[download\\]\\s+Destination: (.*?)");
	private static final Pattern standardOperationPattern = Pattern.compile("\\[(.*?)\\]\\s+(.+): (.*?)");
	private static final Pattern warningPattern = Pattern.compile("WARNING: (.*?)");
	private static final Pattern errorPattern = Pattern.compile("ERROR: (.*?)");
	private static final Pattern mergingPattern = Pattern.compile("\\[ffmpeg\\] Merging formats into \"(.*?)\"");
	private static final Pattern alreadyMergedPattern = Pattern.compile("\\[download\\] (.*?) has already been downloaded and merged");
	private static final Pattern deletingPattern = Pattern.compile("Deleting original file (.+?) (pass -k to keep)");
	private static final Pattern infoFormatsPattern = Pattern.compile("\\[info\\] Available formats for (.+?):");
	private static final Pattern infoFormatsHeaderPattern = Pattern.compile("format\\s+code\\s+extension\\s+resolution\\s+note");
	private static final Pattern infoThumbnailsPattern = Pattern.compile("\\[info\\] Thumbnails for (.+?):");
	private static final Pattern infoThumbnailsHeaderPattern = Pattern.compile("ID\\s+width\\s+height\\s+URL");
	private static final Pattern json = Pattern.compile("\\{\".*?\\}");

	public DownloadProcessor(ItemStatus status){
		this.status = status;
	}
	
	public void consume(String message){
		Matcher m = infoFormatsPattern.matcher(message);
		if(m.matches()){
			mode = DownloadMode.INFO;
		}
		if(mode == DownloadMode.INFO){
			if(message.trim().isEmpty()) {
				mode = DownloadMode.STANDARD;
				return;
			}
			m = infoFormatsHeaderPattern.matcher(message);
			if(m.matches()) return;
		}else{
			m = json.matcher(message);
			if(m.matches()) {
				json(message);
			}
		}
	}

	private void json(String message) {
		ObjectMapper m = new ObjectMapper();
		JsonDump dump;
		try {
			dump = m.readValue(message, JsonDump.class);
			this.status.populateWith(dump);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void endConsuming() {
		mode = DownloadMode.END;
	}
}
