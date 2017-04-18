package edu.csula.datascience.acquisition.model;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.SearchResult;

public class YoutubeModel {
	public String id;
	public String channel_id;
	public String title;
	public String description;
	public DateTime published;
	
	public YoutubeModel() {
		
	}
	
	public YoutubeModel(SearchResult singleVideo) {
		this.id = singleVideo.getId().getVideoId();
		this.title = singleVideo.getSnippet().getTitle();
		this.description = singleVideo.getSnippet().getDescription();
		this.published = singleVideo.getSnippet().getPublishedAt();
		this.channel_id = singleVideo.getId().getChannelId();
	}
}
