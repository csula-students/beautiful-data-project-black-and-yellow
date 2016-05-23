package edu.csula.datascience.acquisition.driver.worker.helper;

import java.util.ArrayList;
import java.util.List;

import edu.csula.datascience.acquisition.driver.database.mongo.ext.AmazonDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.QuandlRevenueDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.QuandlStockDataCollector;
import edu.csula.datascience.acquisition.driver.network.api.QuandlRevenueApiDriver;
import edu.csula.datascience.acquisition.driver.network.api.QuandlStockApiDriver;
import edu.csula.datascience.acquisition.model.database.AmazonModel;

public class EvaluateThreadHelper extends Thread {
	protected QuandlRevenueDataCollector dbRevDriver;
	protected QuandlStockDataCollector dbSckDriver;
	protected QuandlStockApiDriver stockInstance;
	protected QuandlRevenueApiDriver revInstance;
	protected AmazonDataCollector dbAmazonDriver;
	protected AmazonModel model;
	
	public EvaluateThreadHelper(QuandlRevenueDataCollector dbRevDriver, QuandlStockDataCollector dbSckDriver, AmazonDataCollector dbAmazonDriver) {
		this(dbRevDriver, dbSckDriver, dbAmazonDriver,null,null,null);
	}
	
	public EvaluateThreadHelper(QuandlRevenueDataCollector dbRevDriver, QuandlStockDataCollector dbSckDriver, AmazonDataCollector dbAmazonDriver, QuandlStockApiDriver stockInstance, QuandlRevenueApiDriver revInstance, AmazonModel model) {
		this.dbRevDriver = dbRevDriver;
		this.dbSckDriver = dbSckDriver;
		this.stockInstance = stockInstance;
		this.revInstance = revInstance;
		this.dbAmazonDriver = dbAmazonDriver;
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
		List<AmazonModel> list = new ArrayList<>();
		model.name = model.name.trim();
		list.add(model);
		this.dbAmazonDriver.save(list);
		
		revInstance.queryService();
		revInstance.setBatchSize(2000);
		while(revInstance.hasNext()) {
			dbRevDriver.save(dbRevDriver.mungee(revInstance.next()));
		}
		
		stockInstance.queryService();
		stockInstance.setBatchSize(2000);
		while(stockInstance.hasNext()) {
			dbSckDriver.save(dbSckDriver.mungee(stockInstance.next()));
		}
		
		try {
			this.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
