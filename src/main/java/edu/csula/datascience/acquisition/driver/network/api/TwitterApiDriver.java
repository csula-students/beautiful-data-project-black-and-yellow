package edu.csula.datascience.acquisition.driver.network.api;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import edu.csula.datascience.acquisition.driver.BaseApiDriver;
import edu.csula.datascience.acquisition.driver.network.ApiServiceCallDriver;
import edu.csula.datascience.acquisition.model.Company;
//import utility.vendors.douglascrockford.json.JSONObject;
import edu.csula.datascience.acquisition.runner.DataCollectionRunner;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import com.mongodb.util.JSON;

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
	
	public boolean authenticate() {
		ApiServiceCallDriver apiCaller = new ApiServiceCallDriver(this.config.get("oAuth"));
		apiCaller.setMethodPost();
		try {
			String key = URLEncoder.encode(this.config.get("key"),"UTF-8");
			String secret = URLEncoder.encode(this.config.get("secret"),"UTF-8");
			String encodedKey = new String(Base64.encodeBase64((key+":"+secret).getBytes()));
			
			apiCaller.setHeader("Authorization", "Basic " + encodedKey);
			apiCaller.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			
			apiCaller.setRequestData("grant_type", "client_credentials");
			apiCaller.connect();
			
			String response = apiCaller.getContent();
			System.out.println("Twitter Authenication Response: " + response);
			JSONObject JSON = new JSONObject(response);
			if(JSON.getString("token_type") != null && JSON.getString("token_type").compareToIgnoreCase("bearer") == 0) {
				this.config.put("access_token", JSON.getString("access_token"));
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void queryService() {
		if(!this.config.containsKey("access_token")) {
			//TODO: toss error
			return;
		}
		
		ApiServiceCallDriver apiCaller = new ApiServiceCallDriver(this.config.get("service")+this.config.get("type"));
		apiCaller.setMethodGet();
		apiCaller.setHeader("Authorization", "Bearer " + this.config.get("access_token"));
		apiCaller.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		
		List<Company> companies = DataCollectionRunner.getCompanies();
		String q = "";
		for(Company company : companies) {
			q += " OR " + company.name;
		}
		q = q.substring(4);
		apiCaller.setRequestData("q", q);
		try {
			apiCaller.connect();
			System.out.println(apiCaller.getContent());
			//Object data = (Object)JSON.parse(apiCaller.getContent());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

}
