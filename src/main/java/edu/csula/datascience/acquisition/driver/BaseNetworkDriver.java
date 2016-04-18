package edu.csula.datascience.acquisition.driver;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

public abstract class BaseNetworkDriver extends BaseDriver {
	public static final String GET = "GET";
	public static final String POST = "POST";
	
	protected String method;
	protected HashMap<String,String> requestData;
	protected HashMap<String,String> requestHeaderData;
	protected HttpClient connection;
	protected HttpResponse response;
	
	abstract public void connect() throws IOException;
	
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
	
	public final void setMethodGet() {
		this.method = GET;
	}
	public final void setMethodPost() {
		this.method = POST;
	}
	
	public final void setHeader(String name, String value) {
		this.requestHeaderData.put(name, value);
	}
	
	public final void setRequestData(String name, String value) {
		this.requestData.put(name, value);
	}
}
