package edu.csula.datascience.acquisition.driver.network.api;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.csula.datascience.acquisition.driver.BaseApiDriver;
import edu.csula.datascience.acquisition.driver.BaseCallable;
import edu.csula.datascience.acquisition.driver.network.HTTPServiceDriver;
import edu.csula.datascience.acquisition.model.Company;
import edu.csula.datascience.acquisition.model.TweetModel;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

public class TwitterApiDriver extends BaseApiDriver<TweetModel> {
	private static TwitterApiDriver Instance = null;
	protected List<Company> companies;
	
	public static TwitterApiDriver getInstance() {
		if(Instance == null) {
			Instance = new TwitterApiDriver();
		}
		
		return Instance;
	}
	
	protected TwitterApiDriver() { 
		super();
		companies = new ArrayList<>();
	}
	
	public void addCompany(Company company) {
		this.companies.add(company);
	}
	
	public boolean authenticate() {
		HTTPServiceDriver apiCaller = new HTTPServiceDriver(this.config.get("oAuth"));
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
		if(!this.config.containsKey("access_token") || companies.size() == 0) {
			return;
		}
		
		SimpleDateFormat date = new SimpleDateFormat("YYYY-MM-dd");
		HTTPServiceDriver apiCaller = new HTTPServiceDriver(this.config.get("service")+this.config.get("type"));
		apiCaller.setMethodGet();
		apiCaller.setHeader("Authorization", "Bearer " + this.config.get("access_token"));
		apiCaller.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		
		String q = "";
		for(Company company : companies) {
			q += " OR " + company.name.trim().replaceAll(" ", " OR ");
			if(company.alias.size() > 0) {
				for(String alias : company.alias) {
					q += " OR " + alias.trim().replaceAll(" ", " OR ");
				}
			}
		}
		
		q = q.substring(4);
		apiCaller.setRequestData("q", q+" since:"+date.format(new Date()));
		apiCaller.setRequestData("lang", "en");
		try {
			apiCaller.connect();
			String response = apiCaller.getContent();
			JSONObject JSON = new JSONObject(response);
			if(JSON.has("statuses")) {
				JSONArray statuses = JSON.getJSONArray("statuses");
				System.out.println("Retrieved " + statuses.length() + " statuses");
				
				for(int i = 0; i < statuses.length(); i++) {
					JSONObject json = statuses.getJSONObject(i);
					HashMap<String,String> data = new HashMap<>();
					Iterator<String> keyItr = json.keySet().iterator();
					while(keyItr.hasNext()) {
						String key = keyItr.next();
						data.put(key, json.get(key).toString());
					}
					this.data.add(data);
				}
			} else {
				System.out.println("Did not retrieved any statuses");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	@Override
	public void queryService(BaseCallable callable) {
		if(!this.config.containsKey("access_token") || companies.size() == 0) {
			return;
		}
		
		SimpleDateFormat date = new SimpleDateFormat("YYYY-MM-dd");
		HTTPServiceDriver apiCaller = new HTTPServiceDriver(this.config.get("service")+this.config.get("type"));
		apiCaller.setMethodGet();
		apiCaller.setHeader("Authorization", "Bearer " + this.config.get("access_token"));
		apiCaller.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		
		String q = "";
		for(Company company : companies) {
			q += " OR " + company.name.trim().replaceAll(" ", " OR ");
			if(company.alias.size() > 0) {
				for(String alias : company.alias) {
					q += " OR " + alias.trim().replaceAll(" ", " OR ");
				}
			}
		}
		
		q = q.substring(4);
		apiCaller.setRequestData("q", q+" since:"+date.format(new Date()));
		apiCaller.setRequestData("lang", "en");
		try {
			apiCaller.connect();
			String response = apiCaller.getContent();
			JSONObject JSON = new JSONObject(response);
			if(JSON.has("statuses")) {
				JSONArray statuses = JSON.getJSONArray("statuses");
				System.out.println("Retrieved " + statuses.length() + " statuses");
				callable.call(statuses);				
			} else {
				System.out.println("Did not retrieved any statuses");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getLocalizedMessage());
			e.printStackTrace();
		}	
	}

	@Override
	public Collection<TweetModel> next() {
		List<TweetModel> ret = new ArrayList<TweetModel>();
		while(this.data.size() > 0 && ret.size() < this.batchSize) {
			HashMap<String,String> row = this.data.remove(0);
			
			try {
				TweetModel model = new TweetModel();
				
				model.created_at = row.get("created_at");
				model.favorite_count = Integer.valueOf(row.get("favorite_count"));
				model.id = Long.valueOf(row.get("id"));
				model.retweet_count = Integer.valueOf(row.get("retweet_count"));
				model.text = row.get("text");
				
				ret.add(model);
			} catch(NullPointerException e) {
				//Dirty Data
			}
		}
		return ret;
	}
}
