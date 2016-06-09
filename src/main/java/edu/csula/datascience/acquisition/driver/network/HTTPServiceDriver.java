package edu.csula.datascience.acquisition.driver.network;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import edu.csula.datascience.acquisition.driver.BaseNetworkDriver;

public class HTTPServiceDriver extends BaseNetworkDriver{
	public static final String PUT = "PUT";
	protected String url;
	
	public HTTPServiceDriver(String url) {
		this.url = url;
		this.requestHeaderData = new HashMap<>();
		this.requestData = new HashMap<>();
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setMethodPut() {
		this.method = PUT;
	}

	@Override
	public void connect() throws IOException {
		this.connection = HttpClients.createDefault();
		if(this.method.compareTo(GET) == 0) {
			String query = "";
			for(String key : this.requestData.keySet()) {
				query += "&" +key + "=" + URLEncoder.encode(this.requestData.get(key),"UTF-8");
			}
			
			if(query.length() > 0) {
				query = query.substring(1);
			}
			
			if(!this.url.contains("?")) {
				this.url+="?";
			}
			
			System.out.println("URL - "+this.method+": " + this.url+query);
			HttpGet connection = new HttpGet(this.url+query);
			for(String key : this.requestHeaderData.keySet()) {
				connection.addHeader(key, this.requestHeaderData.get(key));
			}
			
			this.response = this.connection.execute(connection);
		} else if(this.method.compareTo(POST) == 0) {
			System.out.println("URL - "+this.method+": " + this.url);
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
		} else if(this.method.compareTo(PUT) == 0) {
			System.out.println("URL - "+this.method+": " + this.url);
			HttpPut connection = new HttpPut(this.url);
			ArrayList<NameValuePair> parameters = new ArrayList<>();
			for (String key : this.requestData.keySet()) {
				parameters.add(new BasicNameValuePair(key,this.requestData.get(key)));
			}
			
			for(String key : this.requestHeaderData.keySet()) {
				connection.addHeader(key, this.requestHeaderData.get(key));
			}
			
			connection.setEntity(new UrlEncodedFormEntity(parameters,"UTF-8"));
			
			this.response = this.connection.execute(connection);
		} else {
			throw new IOException("This driver does not handle the request " + this.method);
		}		
	}
	
	public void connect(String requestData) throws IOException {
		this.connection = HttpClients.createDefault();
		if(this.method.compareTo(GET) == 0) {
			String query = requestData;			
			System.out.println("URL - "+this.method+": " + this.url+"?"+query);
			HttpGet connection = new HttpGet(this.url+"?"+query);
			for(String key : this.requestHeaderData.keySet()) {
				connection.addHeader(key, this.requestHeaderData.get(key));
			}
			
			this.response = this.connection.execute(connection);
		} else if(this.method.compareTo(POST) == 0) {
			System.out.println("URL - "+this.method+": " + this.url);
			HttpPost connection = new HttpPost(this.url);			
			for(String key : this.requestHeaderData.keySet()) {
				connection.addHeader(key, this.requestHeaderData.get(key));
			}
			
			connection.setEntity(new StringEntity(requestData,"UTF-8"));
			
			this.response = this.connection.execute(connection);
		} else if(this.method.compareTo(PUT) == 0) {
			System.out.println("URL - "+this.method+": " + this.url);
			HttpPut connection = new HttpPut(this.url);
			for(String key : this.requestHeaderData.keySet()) {
				connection.addHeader(key, this.requestHeaderData.get(key));
			}
			
			connection.setEntity(new StringEntity(requestData,"UTF-8"));
			
			this.response = this.connection.execute(connection);
		} else {
			throw new IOException("This driver does not handle the request " + this.method);
		}
		
	}
}
