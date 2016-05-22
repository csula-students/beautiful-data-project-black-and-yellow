package edu.csula.datascience.acquisition.model.database;

import java.util.Date;

import org.json.JSONObject;
import com.google.api.services.youtube.model.SearchResult;

import edu.csula.datascience.acquisition.driver.database.BaseDatabaseModel;

public class YoutubeModel extends BaseDatabaseModel<YoutubeModel> {
	public String id;
	public String channel_id;
	public String title;
	public String description;
	public Date published;
	
	public YoutubeModel() {
		id = "";
		channel_id = "";
		title = "";
		description = "";
		published = new Date();
	}
	
	public YoutubeModel(SearchResult singleVideo) {
		this.id = singleVideo.getId().getVideoId();
		this.title = singleVideo.getSnippet().getTitle();
		this.description = singleVideo.getSnippet().getDescription();
		this.published = new Date(singleVideo.getSnippet().getPublishedAt().getValue());
		this.channel_id = singleVideo.getId().getChannelId();
	}
	
	public void parseJSONObject(JSONObject model) {
		this.id = model.getString("id");
		this.title = model.getString("title");
		this.description = model.getString("description");
		this.published = (Date)model.get("published");
	}
	
	public YoutubeModel clone() {
		YoutubeModel object = new YoutubeModel();
		object.id = this.id.toString();
		object.channel_id = this.channel_id.toString();
		object.title = this.title.toString();
		object.description = this.description.toString();
		object.published = (Date)this.published.clone();
		return object;
	}
}
