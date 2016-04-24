package edu.csula.datascience.acquisition.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.csula.datascience.acquisition.Source;

public abstract class BaseApiDriver<A> extends BaseDriver implements Source<A> {
	protected String apiServer;
	protected int batchSize = 1000;
	protected HashMap<String,String> config;
	protected List<HashMap<String,String>> data;
	
	protected BaseApiDriver() {
		this.data = new ArrayList<HashMap<String,String>>();
	}
	
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
	abstract public void queryService(BaseCallable callback);
	
	public final List<HashMap<String,String>> getRawData() {
		return this.data;
	}
	
	public final boolean hasNext() {
		return this.data.size() > 0;
	}
}