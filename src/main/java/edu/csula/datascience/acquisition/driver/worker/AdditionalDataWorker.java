package edu.csula.datascience.acquisition.driver.worker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import edu.csula.datascience.acquisition.driver.database.mongo.ext.AmazonDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.QuandlRevenueDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.QuandlStockDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.TweetDataCollector;
import edu.csula.datascience.acquisition.driver.network.api.search.SearchTwitterPopularityApiDriver;
import edu.csula.datascience.acquisition.model.database.AmazonModel;

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
		int pointer = 0;
		System.out.print("Grabbing supplemental data from ");
		String keyTerms = "\"facebook\"OR\"cumulus\"OR\"erickson\"OR\"aaon\"OR\"msci\"OR\"apple\"OR\"sierra\"OR\"cambium\"OR\"microsoft\"OR\"android\"OR\"twitter\"OR\"youtube\"OR\"google\"OR\"technology\"OR\"stock market\"";
		
		List<HashMap<String,String>> configs = new ArrayList<HashMap<String,String>>();
		
		HashMap<String,String> _temp1 = new HashMap<String,String>();
		_temp1.put("oAuth","https://api.twitter.com/oauth2/token");
		_temp1.put("service","https://api.twitter.com/1.1/search/tweets.");
		_temp1.put("type","json");
		_temp1.put("key","Vd08xrR9cN6axryGYuvHFYOYB");
		_temp1.put("secret","yzTFZgGUfEpgHPttiR9s5a8PPJxbri1OGORLtC4SrPb0metDMw");
		configs.add(_temp1);
		
		HashMap<String,String> _temp2 = new HashMap<String,String>();
		_temp2.put("oAuth","https://api.twitter.com/oauth2/token");
		_temp2.put("service","https://api.twitter.com/1.1/search/tweets.");
		_temp2.put("type","json");
		_temp2.put("key","taBjh406oVTKiarRl4j2wTMj8");
		_temp2.put("secret","R2CFVHG5ruU76thySrE7Sfe62mvlpxafGCD73Ae4lCHM83IELT");
		configs.add(_temp2);
		
		HashMap<String,String> _temp3 = new HashMap<String,String>();
		_temp3.put("oAuth","https://api.twitter.com/oauth2/token");
		_temp3.put("service","https://api.twitter.com/1.1/search/tweets.");
		_temp3.put("type","json");
		_temp3.put("key","43fIMEPWY7fEmwotdxz8FhOtf");
		_temp3.put("secret","x9IdqoV8N2KX4BGYIrRLXENvpyLyuC1f0OgnpdSLzDHinQBMWg");
		configs.add(_temp3);
		
		Calendar start = Calendar.getInstance();
		start.set(Calendar.YEAR, 2012);
		start.set(Calendar.MONTH, 00);
		start.set(Calendar.DAY_OF_MONTH, 01);
		start.set(Calendar.HOUR, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		
		Calendar end = Calendar.getInstance();
		end.set(Calendar.YEAR, 2016);
		end.set(Calendar.MONTH, 04);
		end.set(Calendar.DAY_OF_MONTH, 25);
		end.set(Calendar.HOUR, 0);
		end.set(Calendar.MINUTE, 0);
		end.set(Calendar.SECOND, 0);
		
		Calendar _end = Calendar.getInstance();
		_end.setTime(end.getTime());
		_end.add(Calendar.DAY_OF_MONTH, -7);
		
		TweetDataCollector tweetDriver = new TweetDataCollector(this.dbHost);
		ArrayList<String> nextResultSet = new ArrayList<String>();
//		AmazonDataCollector amazonDB = new AmazonDataCollector(this.dbHost);
//		List<AmazonModel> companies = amazonDB.findAll(new AmazonModel());
//		for(AmazonModel company : companies) {
//			//Twitter
//			SearchTwitterPopularityApiDriver apiDriver = new SearchTwitterPopularityApiDriver('"'+company.name+'"',_end.getTime(),end.getTime());
//			boolean foundSomething = false;
//			do {
//				foundSomething = false;
//				apiDriver.setConfigData(configs.get(pointer));
//				pointer = (pointer+1) %configs.size();
//				if(apiDriver.authenticate()) {
//					apiDriver.queryService(nextResultSet);
//					foundSomething = apiDriver.hasNext();
//					while(apiDriver.hasNext()) {
//						tweetDriver.save(apiDriver.next());
//					}
//					try {
//						Thread.sleep((2000 / configs.size()) + 1);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			} while(foundSomething);
//		}
		
//		System.out.println(start.toString() + " to " + end.toString());

		//Twitter
		SearchTwitterPopularityApiDriver apiDriver = new SearchTwitterPopularityApiDriver(keyTerms,start.getTime(),end.getTime());
		boolean hasResults = false;
		do {			
			apiDriver.setConfigData(configs.get(pointer));
			pointer = (pointer+1) %configs.size();
			apiDriver.queryService(nextResultSet);
			hasResults = apiDriver.hasNext();
			while(apiDriver.hasNext()) {
				tweetDriver.save(apiDriver.next());
			}
//			_end.add(Calendar.DAY_OF_MONTH, -7);
//			end.add(Calendar.DAY_OF_MONTH, -1);
//			nextResultSet.clear();
			
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while(hasResults);
	}
}
