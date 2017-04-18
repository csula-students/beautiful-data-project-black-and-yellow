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
	protected HttpClient connection = null;
	protected HttpResponse response = null;
	
	abstract public void connect() throws IOException;
	
	public String getContent() throws IOException{
		if(this.response == null) {
			throw new IOException("You need to establish a connection first");
		}
		String response = "";
		
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
	
	public InputStream getInputStream() throws IOException {
		if(this.response == null) {
			throw new IOException("You need to establish a connection first");
		}
		
		HttpEntity entity = this.response.getEntity();
		if(entity != null) {
			InputStream iStream = null;
			try {
				iStream = entity.getContent();
				return iStream;
			} catch (IOException e) {
				//Don't Care
			}
		}
		
		return null;
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
