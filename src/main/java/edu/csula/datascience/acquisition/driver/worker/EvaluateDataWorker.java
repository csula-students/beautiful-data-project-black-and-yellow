package edu.csula.datascience.acquisition.driver.worker;

import java.util.Collection;

import edu.csula.datascience.acquisition.driver.callable.CompareRowsCallable;
import edu.csula.datascience.acquisition.driver.callable.QuandlFindDataCallable;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.QuandlRevenueDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.QuandlStockDataCollector;
import edu.csula.datascience.acquisition.driver.network.api.AmazonFinanceApiDriver;
import edu.csula.datascience.acquisition.driver.network.api.QuandlRevenueApiDriver;
import edu.csula.datascience.acquisition.driver.network.api.QuandlStockApiDriver;
import edu.csula.datascience.acquisition.driver.worker.helper.EvaluateThreadHelper;
import edu.csula.datascience.acquisition.model.AmazonModel;
import edu.csula.datascience.acquisition.model.database.QuandlStockModel;
import edu.csula.datascience.acquisition.runner.DataCollectionRunner;

public class EvaluateDataWorker extends Thread {
	protected QuandlRevenueDataCollector dbRevDriver;
	protected QuandlStockDataCollector dbSckDriver;
	protected String dbHost;
	protected static int limit = 20;
	
	public EvaluateDataWorker(String dbHost) {
		this.dbHost = dbHost;
		this.dbRevDriver = new QuandlRevenueDataCollector(this.dbHost);
		this.dbSckDriver = new QuandlStockDataCollector(this.dbHost);
	}
	
	protected void grabDataFromAmazonAndQuandl() {
		String[] exchanges = {"nasdaq","nyse"};
		EvaluateThreadHelper[] helpers = new EvaluateThreadHelper[limit];
		
		for(int i = 0; i < limit; i++) {
			helpers[i] = new EvaluateThreadHelper(this.dbRevDriver,this.dbSckDriver);
		}
		
		for(String exchange : exchanges) {
			AmazonFinanceApiDriver amazonInstance = new AmazonFinanceApiDriver(exchange);
			amazonInstance.setConfigData(DataCollectionRunner.getConfig("amazon"));
			amazonInstance.queryService();
			while(amazonInstance.hasNext()) {
				Collection<AmazonModel> list = amazonInstance.next();
				for(AmazonModel model : list) {
					EvaluateThreadHelper helper = null;
					while(true) {
						for(int i = 0; i < limit; i++) {
							if(!helpers[i].isAlive()) {
								helpers[i] = new EvaluateThreadHelper(this.dbRevDriver,this.dbSckDriver);
								helper = helpers[i];
								break;
							}							
						}
						
						if(helper == null) {
							try {
								Thread.sleep(10000);
							} catch (InterruptedException e) {
								//Do nothing
							}
							continue;
						}
						break;
					}
					
					QuandlStockApiDriver stockInstance = new QuandlStockApiDriver();
					QuandlRevenueApiDriver revInstance = new QuandlRevenueApiDriver();
					
					stockInstance.setConfigData(DataCollectionRunner.getConfig("quandl"));
					revInstance.setConfigData(DataCollectionRunner.getConfig("quandl"));
					
					stockInstance.addCompanyStock(model.ticker);
					revInstance.addCompanyStock(model.ticker);
					
					helper.setStockInstance(stockInstance);
					helper.setRevInstance(revInstance);
					helper.setModel(model);
					helper.start();
				}
			}
		}
	}
	
	public void processDataFromQuandl() {
		QuandlFindDataCallable _callback = new QuandlFindDataCallable(this.dbHost);
		CompareRowsCallable<QuandlStockModel> callback = new CompareRowsCallable<>(5,0,_callback);
		QuandlStockModel model = new QuandlStockModel();
		dbSckDriver.fetchAll(callback, model);
	}
	
	public void run() {
		System.out.println("Grabbing data from Amazon and Quandl");
		this.grabDataFromAmazonAndQuandl();
		
		System.out.println("Process data from Quandl");		
		this.processDataFromQuandl();
	}
}
