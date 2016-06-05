package edu.csula.datascience.acquisition.driver.network.api.search;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.util.DateTime;
import com.google.api.services.samples.youtube.cmdline.Auth;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import edu.csula.datascience.acquisition.driver.network.api.YoutubeApiDriver;

public class SearchYoutubeApiDriver extends YoutubeApiDriver {
	protected long limit = 50;
	protected long count = limit;
	protected String pageToken = null;
	protected String dbHost;
	protected Date startTime;
	protected Date endTime;
	protected String query;
	protected boolean failSafe = false;

	public SearchYoutubeApiDriver(String name, Date startTime, Date endTime) {
		super();
		this.query = name;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	@Override
	public void queryService() {
		DateTime startDateTime = new DateTime(this.startTime);
		DateTime endDateTime = new DateTime(this.endTime);
		YouTube youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
			public void initialize(HttpRequest request) throws IOException {
			}
		}).setApplicationName("cs454-homework2").build();

		try {
			// Define the API request for retrieving search results.
			YouTube.Search.List search = youtube.search().list("id,snippet");
			
			do {
				this.count = 0;
				
				search.setKey(this.config.get("key"));
				if(this.pageToken != null) {
					search.setPageToken(this.pageToken);
					this.failSafe = true;
				} else {
					search.setQ(this.query);
					//search.setFields("items(id/videoId,snippet/title,snippet/description,snippet/publishedAt)");
					search.setType("video");
					search.setMaxResults(this.limit);
					search.setPublishedAfter(startDateTime);
					search.setPublishedBefore(endDateTime);
					search.setRelevanceLanguage("en");
					search.setOrder("rating");
				}
				
				
				SearchListResponse searchResponse = search.execute();
				this.pageToken = searchResponse.getNextPageToken();
				List<SearchResult> searchResultList = searchResponse.getItems();
	
				if (searchResultList != null) {
					Iterator<SearchResult> iter = searchResultList.iterator();
					while(iter.hasNext()) {
						SearchResult result = iter.next();
						HashMap<String,String> data = new HashMap<>();
	
						data.put("channel_id", result.getId().getChannelId());
						data.put("video_id", result.getId().getVideoId());
						data.put("title", result.getSnippet().getTitle());
						data.put("description", result.getSnippet().getDescription());
						data.put("published_at", result.getSnippet().getPublishedAt().toStringRfc3339());
						this.data.add(data);
						this.count++;
					}
				}
				if(searchResponse.getPageInfo() != null) {
					System.out.println("Retrieved " + count + " videos from youtube out of " + searchResponse.getPageInfo().getTotalResults() );
				} else {
					System.out.println("Retrieved " + count + " videos from youtube");
				}
			} while(this.pageToken != null && this.data.size() < 50000);
		} catch (GoogleJsonResponseException e) {
			System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
					+ e.getDetails().getMessage());
		} catch (IOException e) {
			System.err.println("There was an I/O error: " + e.getCause() + " : " + e.getMessage());
		}
	}
}
