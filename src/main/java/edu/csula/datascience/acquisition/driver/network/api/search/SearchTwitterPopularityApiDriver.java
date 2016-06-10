package edu.csula.datascience.acquisition.driver.network.api.search;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.csula.datascience.acquisition.driver.network.HTTPServiceDriver;
import edu.csula.datascience.acquisition.driver.network.api.TwitterApiDriver;

public class SearchTwitterPopularityApiDriver extends TwitterApiDriver {
	protected int limit = 100;
	protected long _counter = 0;
	protected long _limit = 10000;
	protected int count = limit;
	protected Date startTime;
	protected Date endTime;
	protected String query;
	protected String lastId = null;
	
	public SearchTwitterPopularityApiDriver(String query, Date startTime, Date endTime) {
		super();
		
		this.query = query;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public void queryService(List<String> nextResultSet) {
//		if(!this.config.containsKey("access_token")) {
//			return;
//		}
		int pointer = 0;
		
		List<HashMap<String,String>> configs = new ArrayList<HashMap<String,String>>();
		
		HashMap<String,String> _temp1 = new HashMap<String,String>();
		_temp1.put("oAuth","https://api.twitter.com/oauth2/token");
		_temp1.put("service","https://api.twitter.com/1.1/search/tweets.");
		_temp1.put("type","json");
		_temp1.put("key","Vd08xrR9cN6axryGYuvHFYOYB");
		_temp1.put("secret","yzTFZgGUfEpgHPttiR9s5a8PPJxbri1OGORLtC4SrPb0metDMw");
		configs.add(_temp1);
		
		HashMap<String,String> _temp2 = new HashMap<String,String>();
		_temp2.put("oAuth","https://api.twitter.com/oauth2/token");
		_temp2.put("service","https://api.twitter.com/1.1/search/tweets.");
		_temp2.put("type","json");
		_temp2.put("key","taBjh406oVTKiarRl4j2wTMj8");
		_temp2.put("secret","R2CFVHG5ruU76thySrE7Sfe62mvlpxafGCD73Ae4lCHM83IELT");
		configs.add(_temp2);
		
		HashMap<String,String> _temp3 = new HashMap<String,String>();
		_temp3.put("oAuth","https://api.twitter.com/oauth2/token");
		_temp3.put("service","https://api.twitter.com/1.1/search/tweets.");
		_temp3.put("type","json");
		_temp3.put("key","43fIMEPWY7fEmwotdxz8FhOtf");
		_temp3.put("secret","x9IdqoV8N2KX4BGYIrRLXENvpyLyuC1f0OgnpdSLzDHinQBMWg");
		configs.add(_temp3);
		count = limit;
		
		while((count > 0 ) && this.data.size() < _limit) {
			this.setConfigData(configs.get(pointer));
			pointer = (pointer+1) %configs.size();
			count = 0;
			if(!this.authenticate()) {
				nextResultSet.clear();
				break;
			}
			
			System.out.println("Data Size: " + this.data.size());
			SimpleDateFormat date = new SimpleDateFormat("YYYY-MM-dd");
			HTTPServiceDriver apiCaller = null;
			if(nextResultSet.size() == 0) {
				apiCaller = new HTTPServiceDriver(this.config.get("service")+this.config.get("type"));
				apiCaller.setMethodGet();
				apiCaller.setHeader("Authorization", "Bearer " + this.config.get("access_token"));
				apiCaller.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

				apiCaller.setRequestData("q", this.query);
				
				//apiCaller.setRequestData("since",date.format(this.startTime));
				//apiCaller.setRequestData("until",date.format(this.endTime));
				if(this.lastId != null) {
					apiCaller.setRequestData("max_id",this.lastId);
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				apiCaller.setRequestData("lang", "en");
				apiCaller.setRequestData("count", limit+"");
				//apiCaller.setRequestData("result_type", "mixed");
			} else {
				apiCaller = new HTTPServiceDriver(this.config.get("service")+this.config.get("type")+nextResultSet.remove(0));
				apiCaller.setMethodGet();
				apiCaller.setHeader("Authorization", "Bearer " + this.config.get("access_token"));
				apiCaller.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			}
			
			try {
				apiCaller.connect();
				String response = apiCaller.getContent();
				JSONObject json = new JSONObject(response);
				if(json.has("search_metadata")) {
					JSONObject metadata = json.getJSONObject("search_metadata");
					if(this.lastId == null) {
						if(metadata.has("refresh_url")) {
							nextResultSet.add(metadata.getString("refresh_url"));
							System.out.println(metadata.getString("refresh_url"));
							this.lastId = "1";
							count = limit;
							
							continue;
						}
					} 

					if(metadata.has("next_results")) {
						nextResultSet.add(metadata.getString("next_results"));
						System.out.println(metadata.getString("next_results"));
					}
					
					if(json.has("statuses")) {
						JSONArray statuses = json.getJSONArray("statuses");
						System.out.println("Retrieved " + statuses.length() + " statuses");
						count = statuses.length();
						
						for(int i = 0; i < statuses.length(); i++) {
							JSONObject _json = statuses.getJSONObject(i);
							HashMap<String,String> data = new HashMap<>();
							Iterator<String> keyItr = _json.keySet().iterator();
							while(keyItr.hasNext()) {
								String key = keyItr.next();
								data.put(key, _json.get(key).toString());
							}							
							
							this.data.add(data);
							if(this.lastId == null || Long.valueOf(this.lastId) > Long.valueOf(data.get("id"))) {
								this.lastId = data.get("id");
							}
							
						}
						//_counter = (_counter + 1) % _limit;
					} else {
						count = limit;
						System.out.println("Did not retrieved any statuses: " + response);
					}
				}				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoClassDefFoundError e) {
				e.printStackTrace();
			}
		}		
	}
}
