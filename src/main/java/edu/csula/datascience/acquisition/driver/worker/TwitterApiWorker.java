package edu.csula.datascience.acquisition.driver.worker;

import java.util.List;

import edu.csula.datascience.acquisition.driver.database.mongo.TweetDataCollector;
import edu.csula.datascience.acquisition.driver.network.api.TwitterApiDriver;
import edu.csula.datascience.acquisition.model.Company;
import edu.csula.datascience.acquisition.model.TweetModel;
import edu.csula.datascience.acquisition.runner.DataCollectionRunner;

public class TwitterApiWorker extends Thread {
	private String dbHost;
	public TwitterApiWorker(String dbHost) {
		this.dbHost = dbHost;
	}
	
	public void run() {
		TwitterApiDriver Instance = TwitterApiDriver.getInstance();
		TweetDataCollector<TweetModel,TweetModel> db = new TweetDataCollector<>(this.dbHost);
		db.setMinute(-1);
		//db.setSecond(-10);
		
		List<Company> companies = DataCollectionRunner.getCompanies();
		for(Company company : companies) {
			Instance.addCompany(company);
		}
		
		if(Instance.authenticate()) {
			while(true) {
				try {
					Instance.queryService();
					while(Instance.hasNext()) {
						db.save(db.mungee(Instance.next()));
					}
					Thread.sleep(60000); //60 second
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("Failed to authenticate Twitter API");
		}		
	}
}
