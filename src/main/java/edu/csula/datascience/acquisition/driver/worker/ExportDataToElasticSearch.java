package edu.csula.datascience.acquisition.driver.worker;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.node.Node;

import edu.csula.datascience.acquisition.driver.database.mongo.ext.QuandlRevenueDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.QuandlStockDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.TweetDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.YoutubeDataCollector;
import edu.csula.datascience.acquisition.driver.worker.helper.DataSaverThreadHelper;
import edu.csula.datascience.acquisition.model.database.QuandlRevenueModel;
import edu.csula.datascience.acquisition.model.database.QuandlStockModel;
import edu.csula.datascience.acquisition.model.database.TweetModel;
import edu.csula.datascience.acquisition.model.database.YoutubeModel;

public class ExportDataToElasticSearch extends Thread {
	protected String dbHost;
	protected String name;
	protected String address;
	protected Node esNode;
	
	public ExportDataToElasticSearch(String dbHost, String address, String name) {
		this.dbHost = dbHost;
		this.name = name;
		this.address = address;
	}
	
	public void run() {
		//Connect to database
		List<Thread> threads = new ArrayList<>();

        System.out.println("Exporting Quandl Revenue");
        QuandlRevenueDataCollector dbQuandlRevenue = new QuandlRevenueDataCollector(this.dbHost);
        DataSaverThreadHelper<QuandlRevenueModel,QuandlRevenueModel> thread0 = new DataSaverThreadHelper<>(dbQuandlRevenue,new QuandlRevenueModel(), this.address,this.name,"quandl-revenues");
        thread0.start();
        threads.add(thread0);
        
        System.out.println("Exporting Quandl Stock");
        QuandlStockDataCollector dbQuandlStock = new QuandlStockDataCollector(this.dbHost);
        DataSaverThreadHelper<QuandlStockModel,QuandlStockModel> thread1 = new DataSaverThreadHelper<>(dbQuandlStock,new QuandlStockModel(), this.address,this.name,"quandl-stocks");
        thread1.start();
        threads.add(thread1);
        
        System.out.println("Exporting Tweets");
        TweetDataCollector dbTweet = new TweetDataCollector(this.dbHost);
        DataSaverThreadHelper<TweetModel,TweetModel> thread2 = new DataSaverThreadHelper<>(dbTweet, new TweetModel(), this.address,this.name,"tweets");
        thread2.start();
        threads.add(thread2);
        
        System.out.println("Exporting Youtube");
        YoutubeDataCollector dbYoutube = new YoutubeDataCollector(this.dbHost);
        DataSaverThreadHelper<YoutubeModel,YoutubeModel> thread3 = new DataSaverThreadHelper<>(dbYoutube, new YoutubeModel(), this.address,this.name,"youtube");
        thread3.start();
        threads.add(thread3);
        
        //Keep parent alive until children are finished
  		for(int i = 0; i < threads.size(); i++) {
  			if(threads.get(i).isAlive()) {
  				try {
  					Thread.sleep(1000);
  				} catch (InterruptedException e) {
  					// TODO Auto-generated catch block
  					e.printStackTrace();
  				}
  				i--;
  				continue;
  			}
  		}
	}
}
