package edu.csula.datascience.acquisition.driver;

import java.util.HashMap;

import edu.csula.datascience.acquisition.Source;

public abstract class BaseApiDriver<A> extends BaseDriver implements Source<A> {
	protected String apiServer;
	protected int batchSize;
	protected HashMap<String,String> config;
	
	public void setBatchSize(int size) {
		this.batchSize = size;
	}
	
	public final void setConfigData(HashMap<String,String> data) {
		this.config = data;
	}
	
	public final HashMap<String,String> getConfigData() {
		return this.config;
	}
	
	abstract public void queryService();
}
