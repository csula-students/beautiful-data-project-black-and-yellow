package edu.csula.datascience.acquisition.driver;

import java.util.HashMap;

public abstract class BaseApiDriver extends BaseDriver {
	protected String apiServer;
	protected HashMap<String,String> config;
	
	public final void setConfigData(HashMap<String,String> data) {
		this.config = data;
	}
	
	public final HashMap<String,String> getConfigData() {
		return this.config;
	}
	
	abstract public void queryService();
}
