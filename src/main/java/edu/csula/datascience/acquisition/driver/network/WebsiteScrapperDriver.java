package edu.csula.datascience.acquisition.driver.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import edu.csula.datascience.acquisition.driver.BaseNetworkDriver;

public class WebsiteScrapperDriver extends BaseNetworkDriver {	
	public WebsiteScrapperDriver(String protocol, String host, int port, String path) {
		this.protocol = protocol;
		this.host = host;
		this.port = port;
		this.path = path;
		
		this.requestData = new HashMap<>();
		this.requestHeaderData = new HashMap<>();
	}
	
	public WebsiteScrapperDriver(String host, String path) {
		new WebsiteScrapperDriver("http",host,80,path);
	}
	
	public void connect() throws IOException {
		if(this.method.compareTo(GET) == 0) {
			String query = "";
			for(String key : this.requestData.keySet()) {
				query += "&" +key + "=" + URLEncoder.encode(this.requestData.get(key),"UTF-8");
			}
			query = query.substring(1);
			
			HttpGet connection = new HttpGet(this.protocol+"://"+this.host+":"+this.port+this.path+"?"+query);
			for(String key : this.requestData.keySet()) {
				connection.addHeader(key, this.requestHeaderData.get(key));
			}
			
			this.response = this.connection.execute(connection);
		} else {
			HttpPost connection = new HttpPost(this.protocol+"://"+this.host+":"+this.port+this.path);
			ArrayList<NameValuePair> parameters = new ArrayList<>();
			for (String key : this.requestData.keySet()) {
				parameters.add(new BasicNameValuePair(key,this.requestData.get(key)));
			}
			
			for(String key : this.requestData.keySet()) {
				connection.addHeader(key, this.requestHeaderData.get(key));
			}
			
			connection.setEntity(new UrlEncodedFormEntity(parameters,"UTF-8"));
			
			this.response = this.connection.execute(connection);
		}
	}
	
	public String getContent() {
		String response = null;
		
		HttpEntity entity = this.response.getEntity();
		if(entity != null) {
			InputStream iStream = null;
			try {
				iStream = entity.getContent();
				Scanner reader = new Scanner(iStream);
				while(reader.hasNextLine()) {
					response += reader.nextLine();
				}
				reader.close();
				iStream.close();
			} catch (IOException e) {
				//Don't Care
			}
		}
		
		return response;
	}
	
}