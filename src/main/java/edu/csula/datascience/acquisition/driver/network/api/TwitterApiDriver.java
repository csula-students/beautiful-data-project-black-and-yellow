package edu.csula.datascience.acquisition.driver.network.api;

import edu.csula.datascience.acquisition.driver.BaseApiDriver;

public class TwitterApiDriver extends BaseApiDriver {
	private static TwitterApiDriver Instance = null;
	protected String consumerKey;
	protected String consumerSecret;
	protected String encodedConsumerKey;
	protected String encodedConsumerSecret;
	
	public static TwitterApiDriver getInstance() {
		if(Instance == null) {
			Instance = new TwitterApiDriver();
		}
		
		return Instance;
	}
	
	protected TwitterApiDriver() {
		//TODO: Obtain oAuth Token
		
		//TODO: Connect to Stream
	}

	@Override
	public void queryService() {
		// TODO Auto-generated method stub
		
	}

}
