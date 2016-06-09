package edu.csula.datascience.acquisition.driver.callable;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.csula.datascience.acquisition.driver.BaseCallable;
import edu.csula.datascience.acquisition.driver.network.HTTPServiceDriver;

public class SaveToElasticSearchCallable extends BaseCallable {	
	protected String address;
	protected String indexName;
	protected String typeName;
	protected int startCounter;
	protected HashMap<String,Integer> counter;
	
	public SaveToElasticSearchCallable(String address, String indexName, String typeName) {
		this.address = address;
		this.indexName = indexName;
		this.typeName = typeName;
		this.startCounter = 0;
		counter = new HashMap<>();
	}
	
	public SaveToElasticSearchCallable(String address, String indexName, String typeName, int startCounter) {
		this.address = address;
		this.indexName = indexName;
		this.typeName = typeName;
		this.startCounter = startCounter;
		counter = new HashMap<>();
	}

	@Override
	public Boolean call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean call(JSONObject data) {
		// TODO Auto-generated method stub
		if(!counter.containsKey(this.indexName+"/"+this.typeName)) {
			counter.put(this.indexName+"/"+this.typeName, this.startCounter);
		}
		
		if(data.has("_id")) {
			data.remove("_id");
		}
		if(data.has("id")) {
			data.remove("id");
		}
		HTTPServiceDriver networkDriver = new HTTPServiceDriver(this.address+"/"+this.indexName+"/"+this.typeName+"/"+counter.get(this.indexName+"/"+this.typeName));
		networkDriver.setMethodPut();
		try {
			networkDriver.connect(data.toString());
			System.out.println(networkDriver.getContent());
			counter.put(this.indexName+"/"+this.typeName, counter.get(this.indexName+"/"+this.typeName) + 1);
		} catch (org.apache.http.conn.ConnectTimeoutException e) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return call(data);
		} catch (java.net.SocketException e) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return call(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	@Override
	public Boolean call(JSONArray list) {
		for(int i = 0 ;i < list.length(); i++) {
			this.call(list.getJSONObject(i));
		}
		return true;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
}
