package edu.csula.datascience.acquisition.driver.database;

import java.util.List;
import java.util.concurrent.Callable;

import edu.csula.datascience.acquisition.Collector;

public abstract class BaseDataCollector<T,A,L> implements Collector<T,A>{
	protected boolean isConnected;
	
	abstract public T find(L searchModel, T model);
	abstract public List<T> findAll(L searchModel, T model);
	abstract public void fetchAll(Callable<?> callback, T model);
}
