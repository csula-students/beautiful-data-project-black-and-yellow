package edu.csula.datascience.acquisition.driver.database;

import edu.csula.datascience.acquisition.Collector;

public abstract class BaseDataCollector<T,A> implements Collector<T,A>{
	protected boolean isConnected;
}
