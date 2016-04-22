package edu.csula.datascience.acquisition.driver.network.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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
import edu.csula.datascience.acquisition.driver.BaseApiDriver;
import edu.csula.datascience.acquisition.model.Company;
import edu.csula.datascience.acquisition.model.YoutubeModel;

public class YoutubeApiDriver extends BaseApiDriver<YoutubeModel> {
	private static YoutubeApiDriver Instance = null;
	protected String apiServiceUrl;
	protected List<Company> companies;
	protected List<YoutubeModel> videos;
	
	public static YoutubeApiDriver getInstance() {
		if(Instance == null) {
			Instance = new YoutubeApiDriver();
		}
		
		return Instance;
	}
	
	protected YoutubeApiDriver() {
		this.videos = new ArrayList<>();
		this.companies = new ArrayList<>();
	}
	
	public void addCompanyStock(Company company) {
		this.companies.add(company);
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

		this.companies.forEach((company) -> {
			try {
				// Define the API request for retrieving search results.
		        YouTube.Search.List search = youtube.search().list("id,snippet");

		        // Set your developer key from the Google Developers Console for
		        String apiKey = this.config.get("key");
		        search.setKey(apiKey);
		        search.setQ(company.name);
		        search.setFields("items(id/channelId,id/videoId,snippet/title,snippet/description,snippet/publishedAt)");
		        search.setType("video");
		        search.setMaxResults(50L);
		        search.setPublishedAfter(dateTime);
		        SearchListResponse searchResponse = search.execute();
		        List<SearchResult> searchResultList = searchResponse.getItems();
		        if (searchResultList != null) {
		        	Iterator<SearchResult> iter = searchResultList.iterator();
		        	while(iter.hasNext()) {
		        		YoutubeModel model = new YoutubeModel(iter.next());
		        		videos.add(model);
		        	}
		        }
			} catch (GoogleJsonResponseException e) {
	            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
	                    + e.getDetails().getMessage());
	        } catch (IOException e) {
	            System.err.println("There was an I/O error: " + e.getCause() + " : " + e.getMessage());
	        }			
		});		
	}

	@Override
	public boolean hasNext() {
		return this.videos.size() > 0;
	}

	@Override
	public Collection<YoutubeModel> next() {
		List<YoutubeModel> ret = new ArrayList<YoutubeModel>();
		for(int i = 0 ; i < batchSize && i < this.videos.size(); i++) {
			ret.add(this.videos.remove(0));
		}
		return ret;
	}

}
