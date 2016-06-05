package edu.csula.datascience.acquisition.driver.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.elasticsearch.node.Node;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;

import edu.csula.datascience.acquisition.driver.database.mongo.ext.QuandlRevenueDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.QuandlStockDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.TweetDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.YoutubeDataCollector;
import edu.csula.datascience.acquisition.driver.network.HTTPServiceDriver;
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
	
	@SuppressWarnings("unused")
	private void _run1() {
		//Connect to database
		List<Thread> threads = new ArrayList<>();
		
		int count = 10789;
		int numOfThreads = 10;
		int limit = count / numOfThreads;
		for(int i = 0; i < numOfThreads; i++) {
			System.out.println("Exporting Youtube Data");
	        QuandlStockDataCollector dbYoutubeDbDriver = new QuandlStockDataCollector(this.dbHost);
	        DataSaverThreadHelper<QuandlStockModel,QuandlStockModel> thread2 = new DataSaverThreadHelper<>(dbYoutubeDbDriver,new QuandlStockModel(), this.address,this.name,"tweets",limit *i,limit);
	        thread2.start();
	        threads.add(thread2);
		}
        
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
	
	private void _run2() {
		QuandlStockDataCollector dbQuandlDriver = new QuandlStockDataCollector(this.dbHost);
		BasicDBObject query = new BasicDBObject();
		Calendar start = Calendar.getInstance();
		start.set(Calendar.YEAR, 2016);
		start.set(Calendar.MONTH, 4);
		start.set(Calendar.DAY_OF_MONTH, 01);
		start.set(Calendar.HOUR, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		query.put("date", new BasicDBObject("$gte",start.getTime()));
		
		List<QuandlStockModel> rows = dbQuandlDriver.findAll(query, new QuandlStockModel());
		int count = 5715714;
		for(QuandlStockModel model : rows) {
			HTTPServiceDriver apiCaller = new HTTPServiceDriver("http://superlunchvote.com:9200/datascience/stocks/"+count);
			apiCaller.setMethodPut();
			JSONObject json = model.toJSONObject();
			json.put("date", json.getLong("date") / 1000);
			try {
				apiCaller.connect(json.toString());
				System.out.println(apiCaller.getContent());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			count++;
		}
	}
	
	public void run() {
		this._run2();
	}
}
