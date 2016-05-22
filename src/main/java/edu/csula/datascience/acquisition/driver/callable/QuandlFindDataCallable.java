package edu.csula.datascience.acquisition.driver.callable;

import java.util.Calendar;
import java.util.Date;

import edu.csula.datascience.acquisition.driver.database.mongo.ext.TweetDataCollector;
import edu.csula.datascience.acquisition.driver.network.api.search.SearchTwitterApiDriver;
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
		
		SearchTwitterApiDriver apiDriver = new SearchTwitterApiDriver(row.name,startDate,endDate);
		if(apiDriver.authenticate()) {
			TweetDataCollector tweetDriver = new TweetDataCollector(this.dbHost);
			apiDriver.queryService();
			while(apiDriver.hasNext()) {
				tweetDriver.save(tweetDriver.mungee(apiDriver.next()));
			}
		}
	}
}
