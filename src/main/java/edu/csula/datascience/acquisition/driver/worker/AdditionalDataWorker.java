package edu.csula.datascience.acquisition.driver.worker;

import java.util.Calendar;

import edu.csula.datascience.acquisition.driver.database.mongo.ext.AmazonDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.QuandlRevenueDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.QuandlStockDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.TweetDataCollector;
import edu.csula.datascience.acquisition.driver.network.api.search.SearchTwitterPopularityApiDriver;
import edu.csula.datascience.acquisition.runner.DataCollectionRunner;

public class AdditionalDataWorker extends Thread {
	protected QuandlRevenueDataCollector dbRevDriver;
	protected QuandlStockDataCollector dbSckDriver;
	protected AmazonDataCollector dbAmazonDriver;
	protected String dbHost;
	protected static int limit = 10;
	
	public AdditionalDataWorker(String dbHost) {
		this.dbHost = dbHost;
		this.dbRevDriver = new QuandlRevenueDataCollector(this.dbHost);
		this.dbSckDriver = new QuandlStockDataCollector(this.dbHost);
		this.dbAmazonDriver = new AmazonDataCollector(this.dbHost);
	}
	
	public void run() {
		System.out.print("Grabbing supplemental data from ");
		String keyTerms = "stock technology OR wage OR apple OR ipad OR iphone OR microsoft OR android OR twitter OR youtube OR google OR sales OR market OR price OR oil OR military OR job OR food OR fashion";
		
		Calendar start = Calendar.getInstance();
		start.set(Calendar.YEAR, 2012);
		start.set(Calendar.MONTH, 00);
		start.set(Calendar.DAY_OF_MONTH, 01);
		start.set(Calendar.HOUR, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		
		Calendar end = Calendar.getInstance();
//		end.set(Calendar.YEAR, 2015);
//		end.set(Calendar.MONTH, 11);
//		end.set(Calendar.DAY_OF_MONTH, 27);
//		end.set(Calendar.HOUR, 0);
//		end.set(Calendar.MINUTE, 0);
//		end.set(Calendar.SECOND, 0);
		
		Calendar _end = Calendar.getInstance();
		_end.setTime(end.getTime());
		_end.add(Calendar.DAY_OF_MONTH, -1);
		System.out.println(start.toString() + " to " + end.toString());
		
		TweetDataCollector tweetDriver = new TweetDataCollector(this.dbHost);
		
		while(start.getTimeInMillis() < end.getTimeInMillis()) {
			//Twitter
			SearchTwitterPopularityApiDriver apiDriver = new SearchTwitterPopularityApiDriver(keyTerms,_end.getTime(),end.getTime());
			apiDriver.setConfigData(DataCollectionRunner.getConfig(DataCollectionRunner.TWITTER));
			if(apiDriver.authenticate()) {
				apiDriver.queryService();
				while(apiDriver.hasNext()) {
					tweetDriver.save(apiDriver.next());
				}
				
			}
			end.add(Calendar.DAY_OF_MONTH, -1);
			_end.add(Calendar.DAY_OF_MONTH, -1);
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
