package edu.csula.datascience.acquisition.driver;

public abstract class BaseApiDriver extends BaseDriver {
	protected String apiServer;
	
	abstract public void queryService();
}
