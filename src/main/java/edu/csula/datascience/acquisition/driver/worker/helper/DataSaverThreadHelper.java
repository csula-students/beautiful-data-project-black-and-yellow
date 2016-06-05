package edu.csula.datascience.acquisition.driver.worker.helper;

import edu.csula.datascience.acquisition.driver.callable.SaveToElasticSearchCallable;
import edu.csula.datascience.acquisition.driver.database.BaseDatabaseModel;
import edu.csula.datascience.acquisition.driver.database.mongo.BaseMongoDbDataCollector;

public class DataSaverThreadHelper<T extends BaseDatabaseModel<T> ,A extends T> extends Thread {
	BaseMongoDbDataCollector<T,A> databasedriver;
	A model;
	String address;
	String name;
	String type;
	int limit;
	int offset;
	
	public DataSaverThreadHelper(BaseMongoDbDataCollector<T,A> driver, A model, String address, String name, String type) {
		this.databasedriver = driver;
		this.address = address;
		this.name = name;
		this.type = type;
		this.model = model;
		this.limit = this.offset = 0;
	}
	
	public DataSaverThreadHelper(BaseMongoDbDataCollector<T,A> driver, A model, String address, String name, String type,int offset, int limit) {
		this.databasedriver = driver;
		this.address = address;
		this.name = name;
		this.type = type;
		this.model = model;
		this.limit = limit;
		this.offset = offset;
	}
	
	public void run() {
		SaveToElasticSearchCallable callable = new SaveToElasticSearchCallable(this.address,this.name,this.type,this.offset);
		if(this.limit == 0 && this.offset ==0) {
			this.databasedriver.findAllBaseCallable(callable,this.model);
		} else {
			this.databasedriver.findAllBaseCallable(callable,this.model,this.offset,this.limit);
		}
        
	}

}
