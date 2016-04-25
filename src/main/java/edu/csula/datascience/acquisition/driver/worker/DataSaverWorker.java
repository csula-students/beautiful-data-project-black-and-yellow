package edu.csula.datascience.acquisition.driver.worker;

import java.io.IOException;
import java.util.Collection;

import edu.csula.datascience.acquisition.driver.callable.SaveToFileCallable;
import edu.csula.datascience.acquisition.driver.network.api.AmazonFinanceApiDriver;
import edu.csula.datascience.acquisition.driver.network.api.QuandlRevenueApiDriver;
import edu.csula.datascience.acquisition.driver.network.api.QuandlStockApiDriver;
import edu.csula.datascience.acquisition.model.AmazonModel;
import edu.csula.datascience.acquisition.runner.DataCollectionRunner;

public class DataSaverWorker extends Thread {
	protected String dbHost;
	
	public DataSaverWorker(String dbHost) {
		this.dbHost = dbHost;
	}
	
	public void run() {
		String[] exchanges = {"nasdaq","nyse"};
		
		for(String exchange : exchanges) {
			AmazonFinanceApiDriver amazonInstance = new AmazonFinanceApiDriver(exchange);
			amazonInstance.setConfigData(DataCollectionRunner.getConfig("amazon"));
			amazonInstance.queryService();
			while(amazonInstance.hasNext()) {
				Collection<AmazonModel> list = amazonInstance.next();
				for(AmazonModel model : list) {
					QuandlStockApiDriver stockInstance = new QuandlStockApiDriver();
					QuandlRevenueApiDriver revInstance = new QuandlRevenueApiDriver();
					
					stockInstance.setConfigData(DataCollectionRunner.getConfig("quandl"));
					revInstance.setConfigData(DataCollectionRunner.getConfig("quandl"));
					
					stockInstance.addCompanyStock(model.ticker);
					revInstance.addCompanyStock(model.ticker);
					
					SaveToFileCallable revCallable = new SaveToFileCallable("./quandl/"+model.name.toLowerCase().trim().replaceAll("[^A-Za-z0-9]+", "_")+"_revenue.csv",",");
					SaveToFileCallable stockCallable = new SaveToFileCallable("./quandl/"+model.name.toLowerCase().trim().replaceAll("[^A-Za-z0-9]+", "_")+"_stock.csv",",");
					
					try {
						revCallable.open();
						revInstance.queryService(revCallable);
						revCallable.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						stockCallable.open();
						stockInstance.queryService(stockCallable);
						stockCallable.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
