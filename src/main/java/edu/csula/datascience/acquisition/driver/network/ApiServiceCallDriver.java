package edu.csula.datascience.acquisition.driver.network;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import edu.csula.datascience.acquisition.driver.BaseNetworkDriver;

public class ApiServiceCallDriver extends BaseNetworkDriver{
	protected String url;
	
	public ApiServiceCallDriver(String url) {
		this.url = url;
		this.requestHeaderData = new HashMap<>();
		this.requestData = new HashMap<>();
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public void connect() throws IOException {
		this.connection = HttpClients.createDefault();
		if(this.method.compareTo(GET) == 0) {
			System.out.println("URL - GET: " + this.url);
			String query = "";
			for(String key : this.requestData.keySet()) {
				query += "&" +key + "=" + URLEncoder.encode(this.requestData.get(key),"UTF-8");
			}
			query = query.substring(1);
			
			HttpGet connection = new HttpGet(this.url+"?"+query);
			for(String key : this.requestHeaderData.keySet()) {
				connection.addHeader(key, this.requestHeaderData.get(key));
			}
			
			this.response = this.connection.execute(connection);
		} else {
			System.out.println("URL - POST: " + this.url);
			HttpPost connection = new HttpPost(this.url);
			ArrayList<NameValuePair> parameters = new ArrayList<>();
			for (String key : this.requestData.keySet()) {
				parameters.add(new BasicNameValuePair(key,this.requestData.get(key)));
			}
			
			for(String key : this.requestHeaderData.keySet()) {
				connection.addHeader(key, this.requestHeaderData.get(key));
			}
			
			connection.setEntity(new UrlEncodedFormEntity(parameters,"UTF-8"));
			
			this.response = this.connection.execute(connection);
		}
		
	}
}
