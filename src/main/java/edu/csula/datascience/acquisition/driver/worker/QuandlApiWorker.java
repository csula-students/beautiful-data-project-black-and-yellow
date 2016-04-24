package edu.csula.datascience.acquisition.driver.worker;

import java.util.List;

import edu.csula.datascience.acquisition.driver.database.mongo.QuandlRevenueDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.QuandlStockDataCollector;
import edu.csula.datascience.acquisition.driver.network.api.QuandlRevenueApiDriver;
import edu.csula.datascience.acquisition.driver.network.api.QuandlStockApiDriver;
import edu.csula.datascience.acquisition.model.Company;
import edu.csula.datascience.acquisition.model.QuandlRevenueModel;
import edu.csula.datascience.acquisition.model.QuandlStockModel;
import edu.csula.datascience.acquisition.runner.DataCollectionRunner;

public class QuandlApiWorker extends Thread {
	protected String dbHost;
	
	public QuandlApiWorker(String dbHost) {
		this.dbHost = dbHost;
	}
	
	public void run() {
		QuandlStockApiDriver stockInstance = QuandlStockApiDriver.getInstance();
		QuandlRevenueApiDriver revInstance = QuandlRevenueApiDriver.getInstance();
		QuandlStockDataCollector<QuandlStockModel,QuandlStockModel> dbStockDriver = new QuandlStockDataCollector<>(dbHost);
		QuandlRevenueDataCollector<QuandlRevenueModel,QuandlRevenueModel> dbRevDriver = new QuandlRevenueDataCollector<>(dbHost);
		
		List<Company> companies = DataCollectionRunner.getCompanies();
		for(Company company : companies) {
			for(String stock : company.stock) {
				stockInstance.addCompanyStock(stock);
				revInstance.addCompanyStock(stock);
			}
		}
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			//Do nothing
		}
		
		stockInstance.queryService();
		while(stockInstance.hasNext()) {
			dbStockDriver.save(dbStockDriver.mungee(stockInstance.next()));
		}
		
		revInstance.queryService();
		while(revInstance.hasNext()) {
			dbRevDriver.save(dbRevDriver.mungee(revInstance.next()));
		}
	}
}
