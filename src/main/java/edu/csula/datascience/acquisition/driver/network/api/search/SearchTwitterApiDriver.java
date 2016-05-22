package edu.csula.datascience.acquisition.driver.network.api.search;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.csula.datascience.acquisition.driver.network.HTTPServiceDriver;
import edu.csula.datascience.acquisition.driver.network.api.TwitterApiDriver;

public class SearchTwitterApiDriver extends TwitterApiDriver {
	protected int limit = 100;
	protected int count = limit;
	protected Date startTime;
	protected Date endTime;
	protected String query;
	protected String lastId = null;
	
	public SearchTwitterApiDriver(String query, Date startTime, Date endTime) {
		super();
		
		this.query = query;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	@Override
	public void queryService() {
		if(!this.config.containsKey("access_token")) {
			return;
		}
		
		while(count == limit) {
			SimpleDateFormat date = new SimpleDateFormat("YYYY-MM-dd");
			HTTPServiceDriver apiCaller = new HTTPServiceDriver(this.config.get("service")+this.config.get("type"));
			apiCaller.setMethodGet();
			apiCaller.setHeader("Authorization", "Bearer " + this.config.get("access_token"));
			apiCaller.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

			if(this.lastId == null) {
				apiCaller.setRequestData("q", this.query+" since:"+date.format(this.startTime) + " until:"+date.format(this.endTime));
			} else {
				apiCaller.setRequestData("q", this.query+" since_id:"+this.lastId + " until:"+date.format(this.endTime));
			}
			
			apiCaller.setRequestData("lang", "en");
			apiCaller.setRequestData("count", limit+"");
			apiCaller.setRequestData("result_type", "recent");
			try {
				apiCaller.connect();
				String response = apiCaller.getContent();
				JSONObject json = new JSONObject(response);
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
						this.lastId = data.get("id");
					}
				} else {
					this.count = 0;
					System.out.println("Did not retrieved any statuses");
				}
			} catch (IOException e) {
				this.count = 0;
				e.printStackTrace();
			}
		}		
	}
}
