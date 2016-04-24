package edu.csula.datascience.acquisition.runner;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.csula.datascience.acquisition.driver.callable.SaveToFileCallable;
import edu.csula.datascience.acquisition.driver.network.api.MarkitOnDemandApiDriver;
import edu.csula.datascience.acquisition.driver.network.api.QuandlRevenueApiDriver;
import edu.csula.datascience.acquisition.driver.network.api.QuandlStockApiDriver;
import edu.csula.datascience.acquisition.driver.network.api.TwitterApiDriver;
import edu.csula.datascience.acquisition.driver.network.api.YoutubeApiDriver;
import edu.csula.datascience.acquisition.driver.worker.MODApiWorker;
import edu.csula.datascience.acquisition.driver.worker.QuandlApiWorker;
import edu.csula.datascience.acquisition.driver.worker.TwitterApiWorker;
import edu.csula.datascience.acquisition.driver.worker.YoutubeApiWorker;
import edu.csula.datascience.acquisition.model.Company;

public class DataCollectionRunner {
	protected static DataCollectionRunner Instance = null;
	public static final String MOD = "markitOnDemand";
	public static final String YOUTUBE = "youtube";
	public static final String TWITTER = "twitter";
	public static final String GOOGLE = "google";
	public static final String QUANDL = "quandl";
	
	protected String dbHost;
	protected List<Company> companies;
	protected HashMap<String,HashMap<String,String>> apiConfigs;
	protected List<Thread> threads;
	public DataCollectionRunner() {
		companies = new ArrayList<Company>();
		apiConfigs = new HashMap<>();
		threads = new ArrayList<>();
	}
	
	protected void runDataWorkers() {
		//Load & Start Workers
		MODApiWorker worker1 = new MODApiWorker(this.dbHost);
		worker1.start();
		threads.add(worker1);
		
		TwitterApiWorker worker2 = new TwitterApiWorker(this.dbHost);
		worker2.start();
		threads.add(worker2);
		
		YoutubeApiWorker worker3 = new YoutubeApiWorker(this.dbHost);
		worker3.start();
		threads.add(worker3);
		
		QuandlApiWorker worker4 = new QuandlApiWorker(this.dbHost);
		worker4.start();
		threads.add(worker4);
		
		//Keep parent alive until children are finished
		for(Thread thread : threads) {
			if(thread.isAlive()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
			}
		}
	}
	
	public void runDataSaver() {
		for(Company company : companies) {
			for(String stock : company.stock) {
				QuandlRevenueApiDriver.getInstance().addCompanyStock(stock);
				QuandlStockApiDriver.getInstance().addCompanyStock(stock);
			}
		}
		
		SaveToFileCallable revCallable = new SaveToFileCallable("./quandle_revenue.csv",",");
		SaveToFileCallable stockCallable = new SaveToFileCallable("./quandle_stock.csv",",");
		
		try {
			revCallable.open();
			QuandlRevenueApiDriver.getInstance().queryService(revCallable);
			revCallable.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			stockCallable.open();
			QuandlStockApiDriver.getInstance().queryService(stockCallable);
			stockCallable.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<Company> getCompanies() {
		return Instance.companies;
	}
	
	public static HashMap<String,String> getConfig(String key) {
		return Instance.apiConfigs.get(key);
	}
	
	public static void main(String[] args) {
		Instance = new DataCollectionRunner();
		
		//Read Config File
		try {
			//Load Config file into string
			Scanner scan = new Scanner(new FileReader("./config.json"));
			String jsonStr = "";
			while(scan.hasNext()) {
				jsonStr += scan.nextLine();
			}
			scan.close();
			
			// Parse the JSON string to a JSONObject
			JSONObject json = new JSONObject(jsonStr);
			JSONArray rows = null;
			JSONObject item = null;
			
			rows = json.getJSONArray("companies");
			if(rows != null) {
				for(int i = 0 ; i < rows.length(); i++) {
					item = rows.getJSONObject(i);
					JSONArray _rows = null;
					
					List<String> alias = new ArrayList<>();
					List<String> stock = new ArrayList<>();
					
					_rows = item.getJSONArray("alias");
					for(int j = 0; j < _rows.length(); j++) {
						alias.add(_rows.getString(j));
					}
					
					_rows = item.getJSONArray("stock");
					for(int j = 0; j < _rows.length(); j++) {
						stock.add(_rows.getString(j));
					}
					
					Instance.companies.add(new Company(item.getString("name"),alias,stock));
				}
			}
			
			item = json.getJSONObject("api");
			if(item != null) {
				String[] names = {GOOGLE,TWITTER,MOD,QUANDL};
				for(String name : names) {
					JSONObject _item = item.getJSONObject(name);
					HashMap<String,String> data = new HashMap<>();
					
					for(String prop : JSONObject.getNames(_item)) {
						data.put(prop,_item.getString(prop));
					}
					
					Instance.apiConfigs.put(name,data);
				}
			}
			
			//Load Configs
			MarkitOnDemandApiDriver.getInstance().setConfigData(Instance.apiConfigs.get(MOD));
			YoutubeApiDriver.getInstance().setConfigData(Instance.apiConfigs.get(GOOGLE));
			TwitterApiDriver.getInstance().setConfigData(Instance.apiConfigs.get(TWITTER));
			QuandlStockApiDriver.getInstance().setConfigData(Instance.apiConfigs.get(QUANDL));
			QuandlRevenueApiDriver.getInstance().setConfigData(Instance.apiConfigs.get(QUANDL));
			
			Instance.dbHost = json.getString("dbHost");
			if(args.length > 0 && args[0].equalsIgnoreCase("--save-data")) {
				Instance.runDataSaver();
			} else {
				Instance.runDataWorkers();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
