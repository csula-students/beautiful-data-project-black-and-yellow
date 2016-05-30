package edu.csula.datascience.acquisition.driver.callable;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;

import com.mongodb.BasicDBObject;

import edu.csula.datascience.acquisition.driver.database.mongo.ext.AmazonDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.TweetDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.YoutubeDataCollector;
import edu.csula.datascience.acquisition.driver.network.api.search.SearchTwitterApiDriver;
import edu.csula.datascience.acquisition.driver.network.api.search.SearchYoutubeApiDriver;
import edu.csula.datascience.acquisition.model.database.AmazonModel;
import edu.csula.datascience.acquisition.model.database.QuandlStockModel;
import edu.csula.datascience.acquisition.runner.DataCollectionRunner;

public class QuandlFindDataCallable extends FindDataCallable<QuandlStockModel> {
	protected String dbHost;
	protected Date startDate;
	protected Date endDate;
	protected AmazonDataCollector dbAmazonDriver;
	protected TweetDataCollector tweetDriver;
	protected YoutubeDataCollector youtubeDriver;
	
	public QuandlFindDataCallable(String dbHost) {
		super();
		this.dbHost = dbHost;
		this.startDate = null;
		this.endDate = null;
		this.dbAmazonDriver = new AmazonDataCollector(this.dbHost);
		this.tweetDriver = new TweetDataCollector(this.dbHost);
		this.youtubeDriver = new YoutubeDataCollector(this.dbHost);
	}
	
	public void call(QuandlStockModel row) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(row.date);
		
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		startDate = calendar.getTime();
		
		calendar.add(Calendar.DAY_OF_MONTH, 2);
		endDate = calendar.getTime();
		
		System.out.println("Getting Company name for " + row.stock);
		BasicDBObject query = new BasicDBObject();
		query.put("ticker", row.stock);
		AmazonModel model = this.dbAmazonDriver.find(query, new AmazonModel());		
		
		System.out.println("Checking twitter data for " +model.name);
		SearchTwitterApiDriver twitterApiDriver = new SearchTwitterApiDriver("'"+this.filterName(model.name)+"'",this.startDate,this.endDate);
		twitterApiDriver.setConfigData(DataCollectionRunner.getConfig(DataCollectionRunner.TWITTER));
		do {
			try {
				if(twitterApiDriver.authenticate()) {
					twitterApiDriver.queryService();
					while(twitterApiDriver.hasNext()) {
						tweetDriver.save(twitterApiDriver.next());
					}
				} else {
					System.out.println("Failed to authenticate");
				}
			} catch(JSONException e) {
				e.printStackTrace();
				Thread.sleep(10000);
				continue;
			}
		} while(1 == 2);
		
		
		System.out.println("Checking youtube data for " + model.name);
		SearchYoutubeApiDriver youtubeApiDriver = new SearchYoutubeApiDriver(this.filterName(model.name),this.startDate,this.endDate);
		youtubeApiDriver.setConfigData(DataCollectionRunner.getConfig(DataCollectionRunner.GOOGLE));		
		youtubeApiDriver.queryService();
		while(youtubeApiDriver.hasNext()) {
			youtubeDriver.save(youtubeApiDriver.next());
		}
		
		Thread.sleep(2000);
	}
	
	private String filterName(String name) {
		return name.replaceAll("\\s(Inc\\.?|Corp\\.?|Company|Ltd\\.?|Co\\.?|Corporation|\\-\\sClass|[A-Z\\-\\_])\\s*$","").replaceAll("\\s*The\\s*", "");
	}
}
