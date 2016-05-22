package edu.csula.datascience.acquisition.driver.callable;

import java.util.Calendar;
import java.util.Date;

import edu.csula.datascience.acquisition.driver.database.mongo.ext.TweetDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.YoutubeDataCollector;
import edu.csula.datascience.acquisition.driver.network.api.search.SearchTwitterApiDriver;
import edu.csula.datascience.acquisition.driver.network.api.search.SearchYoutubeApiDriver;
import edu.csula.datascience.acquisition.model.database.QuandlStockModel;

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
		
		SearchTwitterApiDriver twitterApiDriver = new SearchTwitterApiDriver(row.name,startDate,endDate);
		if(twitterApiDriver.authenticate()) {
			TweetDataCollector tweetDriver = new TweetDataCollector(this.dbHost);
			twitterApiDriver.queryService();
			while(twitterApiDriver.hasNext()) {
				tweetDriver.save(tweetDriver.mungee(twitterApiDriver.next()));
			}
		}
		
		SearchYoutubeApiDriver youtubeApiDriver = new SearchYoutubeApiDriver(row.name,startDate,endDate);
		YoutubeDataCollector youtubeDriver = new YoutubeDataCollector(this.dbHost);
		youtubeApiDriver.queryService();
		while(youtubeApiDriver.hasNext()) {
			youtubeDriver.save(youtubeDriver.mungee(youtubeApiDriver.next()));
		}
	}
}
