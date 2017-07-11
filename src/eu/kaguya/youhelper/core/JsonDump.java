package eu.kaguya.youhelper.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonDump {
	String extractor_key;
	Integer duration;
	Integer like_count;
	Integer dislike_count;
	String thumbnail;
	String id;
	String webpage_url;
	String title;
}
