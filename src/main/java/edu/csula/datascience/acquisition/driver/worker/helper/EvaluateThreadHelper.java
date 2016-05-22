package edu.csula.datascience.acquisition.driver.worker.helper;

import edu.csula.datascience.acquisition.driver.database.mongo.ext.QuandlRevenueDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.QuandlStockDataCollector;
import edu.csula.datascience.acquisition.driver.network.api.QuandlRevenueApiDriver;
import edu.csula.datascience.acquisition.driver.network.api.QuandlStockApiDriver;
import edu.csula.datascience.acquisition.model.AmazonModel;

public class EvaluateThreadHelper extends Thread {
	protected QuandlRevenueDataCollector dbRevDriver;
	protected QuandlStockDataCollector dbSckDriver;
	protected QuandlStockApiDriver stockInstance;
	protected QuandlRevenueApiDriver revInstance;
	protected AmazonModel model;
	
	public EvaluateThreadHelper(QuandlRevenueDataCollector dbRevDriver, QuandlStockDataCollector dbSckDriver) {
		this(dbRevDriver, dbSckDriver, null,null,null);
	}
	
	public EvaluateThreadHelper(QuandlRevenueDataCollector dbRevDriver, QuandlStockDataCollector dbSckDriver, QuandlStockApiDriver stockInstance, QuandlRevenueApiDriver revInstance, AmazonModel model) {
		this.dbRevDriver = dbRevDriver;
		this.dbSckDriver = dbSckDriver;
		this.stockInstance = stockInstance;
		this.revInstance = revInstance;
		this.model = model;
	}	
	
	public QuandlStockApiDriver getStockInstance() {
		return stockInstance;
	}

	public void setStockInstance(QuandlStockApiDriver stockInstance) {
		this.stockInstance = stockInstance;
	}

	public QuandlRevenueApiDriver getRevInstance() {
		return revInstance;
	}

	public void setRevInstance(QuandlRevenueApiDriver revInstance) {
		this.revInstance = revInstance;
	}

	public AmazonModel getModel() {
		return model;
	}

	public void setModel(AmazonModel model) {
		this.model = model;
	}

	public void run() {		
		revInstance.queryService();
		while(revInstance.hasNext()) {
			dbRevDriver.save(dbRevDriver.mungee(revInstance.next()));
		}
		
		stockInstance.queryService();
		while(stockInstance.hasNext()) {
			dbSckDriver.save(dbSckDriver.mungee(stockInstance.next()));
		}
	}

}
