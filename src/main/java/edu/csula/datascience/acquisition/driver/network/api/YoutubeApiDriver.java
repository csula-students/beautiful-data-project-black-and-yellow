package edu.csula.datascience.acquisition.driver.network.api;

import edu.csula.datascience.acquisition.driver.BaseApiDriver;

public class YoutubeApiDriver extends BaseApiDriver {
	private static YoutubeApiDriver Instance = null;
	protected String apiServiceUrl;
	
	public static YoutubeApiDriver getInstance() {
		if(Instance == null) {
			Instance = new YoutubeApiDriver();
		}
		
		return Instance;
	}
	
	protected YoutubeApiDriver() {
		
	}

	@Override
	public void queryService() {
		// TODO Auto-generated method stub
		
	}

}
