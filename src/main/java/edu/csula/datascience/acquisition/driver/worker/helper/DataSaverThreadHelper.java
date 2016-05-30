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
	
	public DataSaverThreadHelper(BaseMongoDbDataCollector<T,A> driver, A model, String address, String name, String type) {
		this.databasedriver = driver;
		this.address = address;
		this.name = name;
		this.type = type;
		this.model = model;
	}
	
	public void run() {
		SaveToElasticSearchCallable callable = new SaveToElasticSearchCallable(this.address,this.name,this.type);
        this.databasedriver.findAllBaseCallable(callable,this.model);
	}

}
