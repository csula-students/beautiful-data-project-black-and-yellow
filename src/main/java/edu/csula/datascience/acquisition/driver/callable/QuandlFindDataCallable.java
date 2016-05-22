package edu.csula.datascience.acquisition.driver.callable;

import java.util.Calendar;
import java.util.Date;

import edu.csula.datascience.acquisition.driver.database.mongo.ext.TweetDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.YoutubeDataCollector;
import edu.csula.datascience.acquisition.driver.network.api.search.SearchTwitterApiDriver;
import edu.csula.datascience.acquisition.driver.network.api.search.SearchYoutubeApiDriver;
import edu.csula.datascience.acquisition.model.database.QuandlStockModel;
import edu.csula.datascience.acquisition.runner.DataCollectionRunner;

public class QuandlFindDataCallable extends FindDataCallable<QuandlStockModel> {
	protected String dbHost;
	public QuandlFindDataCallable(String dbHost) {
		super();
		this.dbHost = dbHost;
	}
	
	public void call(QuandlStockModel row) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(row.date);
		
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Date startDate = calendar.getTime();
		
		calendar.add(Calendar.DAY_OF_MONTH, 2);
		Date endDate = calendar.getTime();
		
		System.out.println("Checking twitter for data");
		SearchTwitterApiDriver twitterApiDriver = new SearchTwitterApiDriver(row.stock,startDate,endDate);
		twitterApiDriver.setConfigData(DataCollectionRunner.getConfig(DataCollectionRunner.TWITTER));
		if(twitterApiDriver.authenticate()) {
			TweetDataCollector tweetDriver = new TweetDataCollector(this.dbHost);
			twitterApiDriver.queryService();
			while(twitterApiDriver.hasNext()) {
				tweetDriver.save(twitterApiDriver.next());
			}
		} else {
			System.out.println("Failed to authenticate");
		}
		
		System.out.println("Checking youtube for data");
		SearchYoutubeApiDriver youtubeApiDriver = new SearchYoutubeApiDriver(row.stock,startDate,endDate);
		youtubeApiDriver.setConfigData(DataCollectionRunner.getConfig(DataCollectionRunner.GOOGLE));
		YoutubeDataCollector youtubeDriver = new YoutubeDataCollector(this.dbHost);
		youtubeApiDriver.queryService();
		while(youtubeApiDriver.hasNext()) {
			youtubeDriver.save(youtubeApiDriver.next());
		}
		
		Thread.sleep(15000);
	}
}
