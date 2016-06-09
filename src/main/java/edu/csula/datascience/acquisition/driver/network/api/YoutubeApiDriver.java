package edu.csula.datascience.acquisition.driver.network.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.util.DateTime;
import com.google.api.services.samples.youtube.cmdline.Auth;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import edu.csula.datascience.acquisition.driver.BaseApiDriver;
import edu.csula.datascience.acquisition.driver.BaseCallable;
import edu.csula.datascience.acquisition.model.Company;
import edu.csula.datascience.acquisition.model.database.YoutubeModel;

public class YoutubeApiDriver extends BaseApiDriver<YoutubeModel> {
	private static YoutubeApiDriver Instance = null;
	protected String apiServiceUrl;
	protected List<Company> companies;
	
	public static YoutubeApiDriver getInstance() {
		if(Instance == null) {
			Instance = new YoutubeApiDriver();
		}
		
		return Instance;
	}
	
	protected YoutubeApiDriver() {
		super();
		this.companies = new ArrayList<>();
	}
	
	public void addCompanyStock(Company company) {
		this.companies.add(company);
	}
	
	protected void helperQueryService(String name,YouTube youtube, DateTime dateTime) throws GoogleJsonResponseException, IOException {
		// Define the API request for retrieving search results.
        YouTube.Search.List search = youtube.search().list("id,snippet");

        // Set your developer key from the Google Developers Console for
        String apiKey = this.config.get("key");
        search.setKey(apiKey);
        search.setQ(name);
        search.setFields("items(id/channelId,id/videoId,snippet/title,snippet/description,snippet/publishedAt)");
        search.setType("video");
        search.setMaxResults(50L);
        search.setPublishedAfter(dateTime);
        SearchListResponse searchResponse = search.execute();
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
        	}
        }
	}

	@Override
	public void queryService() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		DateTime dateTime = new DateTime(calendar.getTime());
		YouTube youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("cs454-homework2").build();

		for(Company company : this.companies) {
			try {
				if(company.alias.size() > 0) {
					for(String name : company.alias) {
						this.helperQueryService(name, youtube, dateTime);
					}
				}
				this.helperQueryService(company.name, youtube, dateTime);
			} catch (GoogleJsonResponseException e) {
	            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
	                    + e.getDetails().getMessage());
	        } catch (IOException e) {
	            System.err.println("There was an I/O error: " + e.getCause() + " : " + e.getMessage());
	        }	
		}
	}
	
	@Override
	public void queryService(BaseCallable callable) {
		this.queryService();
		
		Iterator<HashMap<String,String>> itr = this.data.iterator();
		while(itr.hasNext()) {
			HashMap<String,String> row = itr.next();
			JSONObject json = new JSONObject();
			
			Iterator<String> keyItr = row.keySet().iterator();
			while(keyItr.hasNext()) {
				String key = keyItr.next();
				json.put(key, row.get(key));
			}
			
			callable.call(json);
		}
	}

	@Override
	public Collection<YoutubeModel> next() {
		List<YoutubeModel> ret = new ArrayList<YoutubeModel>();
		while(this.data.size() > 0 && ret.size() < this.batchSize) {
			HashMap<String,String> row = this.data.remove(0);
			
			YoutubeModel model = new YoutubeModel();
			model.channel_id = row.get("channel_id");
			model.id = row.get("video_id");
			model.description = row.get("description");
			model.title = row.get("title");
			model.published = new Date(DateTime.parseRfc3339(row.get("published_at")).getValue());
			ret.add(model);
		}
		return ret;
	}

}
