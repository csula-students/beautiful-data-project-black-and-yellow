package edu.csula.datascience.acquisition.driver.worker;

import edu.csula.datascience.acquisition.driver.database.mongo.TweetDataCollector;
import edu.csula.datascience.acquisition.driver.network.api.TwitterApiDriver;
import edu.csula.datascience.acquisition.model.TweetModel;

public class TwitterApiWorker extends Thread {
	public void run() {
		TwitterApiDriver Instance = TwitterApiDriver.getInstance();
		//TweetDataCollector<TweetModel,TweetModel> db = new TweetDataCollector<>();
		if(Instance.authenticate()) {
			while(true) {
				try {
					Instance.queryService();
//					while(Instance.hasNext()) {
//						db.save(db.mungee(Instance.next()));
//					}
					Thread.sleep(10000);
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
