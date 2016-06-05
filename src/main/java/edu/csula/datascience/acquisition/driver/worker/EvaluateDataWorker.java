package edu.csula.datascience.acquisition.driver.worker;

import java.util.Calendar;
import java.util.Collection;

import com.mongodb.BasicDBObject;

import edu.csula.datascience.acquisition.driver.callable.CompareColumnsCallable;
import edu.csula.datascience.acquisition.driver.callable.QuandlFindDataCallable;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.AmazonDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.QuandlRevenueDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.QuandlStockDataCollector;
import edu.csula.datascience.acquisition.driver.network.api.AmazonFinanceApiDriver;
import edu.csula.datascience.acquisition.driver.network.api.QuandlRevenueApiDriver;
import edu.csula.datascience.acquisition.driver.network.api.QuandlStockApiDriver;
import edu.csula.datascience.acquisition.driver.worker.helper.EvaluateThreadHelper;
import edu.csula.datascience.acquisition.model.database.AmazonModel;
import edu.csula.datascience.acquisition.model.database.QuandlStockModel;
import edu.csula.datascience.acquisition.runner.DataCollectionRunner;

public class EvaluateDataWorker extends Thread {
	protected QuandlRevenueDataCollector dbRevDriver;
	protected QuandlStockDataCollector dbSckDriver;
	protected AmazonDataCollector dbAmazonDriver;
	protected String dbHost;
	protected static int limit = 10;
	
	public EvaluateDataWorker(String dbHost) {
		this.dbHost = dbHost;
		this.dbRevDriver = new QuandlRevenueDataCollector(this.dbHost);
		this.dbSckDriver = new QuandlStockDataCollector(this.dbHost);
		this.dbAmazonDriver = new AmazonDataCollector(this.dbHost);
	}
	
	protected void grabDataFromAmazonAndQuandl() {
		//String[] exchanges = {"nasdaq","nyse"};
		String[] exchanges = {"nasdaq"};
		EvaluateThreadHelper[] helpers = new EvaluateThreadHelper[limit];
		
		for(int i = 0; i < limit; i++) {
			helpers[i] = new EvaluateThreadHelper(this.dbRevDriver,this.dbSckDriver,this.dbAmazonDriver);
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
								helpers[i] = new EvaluateThreadHelper(this.dbRevDriver,this.dbSckDriver,this.dbAmazonDriver);
								helper = helpers[i];
								break;
							}							
						}
						
						if(helper == null) {
							try {
								Thread.sleep(1);
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
		CompareColumnsCallable<QuandlStockModel> callback = new CompareColumnsCallable<>(5,-1,_callback);
		QuandlStockModel model = new QuandlStockModel();
		BasicDBObject query = new BasicDBObject();
		Calendar start = Calendar.getInstance();
		start.set(Calendar.YEAR, 2012);
		start.set(Calendar.MONTH, 0);
		start.set(Calendar.DAY_OF_MONTH, 01);
		start.set(Calendar.HOUR, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		
		Calendar end = Calendar.getInstance();
		end.set(Calendar.YEAR, 2015);
		end.set(Calendar.MONTH, 9);
		end.set(Calendar.DAY_OF_MONTH, 7);
		end.set(Calendar.HOUR, 0);
		end.set(Calendar.MINUTE, 0);
		end.set(Calendar.SECOND, 0);
		
		query.put("date", new BasicDBObject("$gte",start.getTime()).append("$lte", end.getTime()));
		
		BasicDBObject sort = new BasicDBObject();
		sort.put("date", -1);
		dbSckDriver.findAll(query, sort, model, callback);
	}
	
	public void run() {
		System.out.println("Grabbing data from Amazon and Quandl");
		this.grabDataFromAmazonAndQuandl();
		
//		System.out.println("Process data from Quandl");		
//		this.processDataFromQuandl();
	}
}
