package edu.csula.datascience.acquisition.driver;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

public abstract class BaseNetworkDriver extends BaseDriver {
	public static final String GET = "GET";
	public static final String POST = "POST";
	
	protected String protocol;
	protected String host;
	protected int port;
	protected String path;
	protected String method;
	protected HashMap<String,String> requestData;
	protected HashMap<String,String> requestHeaderData;
	protected HttpClient connection;
	protected HttpResponse response;
	
	abstract public void connect() throws IOException;
	abstract public String getContent();
	
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
