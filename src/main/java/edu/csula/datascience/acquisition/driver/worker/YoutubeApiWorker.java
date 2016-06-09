package edu.csula.datascience.acquisition.driver.worker;

import edu.csula.datascience.acquisition.driver.database.mongo.ext.YoutubeDataCollector;
import edu.csula.datascience.acquisition.driver.network.api.YoutubeApiDriver;
import edu.csula.datascience.acquisition.runner.DataCollectionRunner;

public class YoutubeApiWorker extends Thread {
	protected String dbHost;
	
	public YoutubeApiWorker(String dbHost) {
		this.dbHost = dbHost;
	}
	
	public void run() {
		YoutubeApiDriver Instance = YoutubeApiDriver.getInstance();
		YoutubeDataCollector db = new YoutubeDataCollector(this.dbHost);
		DataCollectionRunner.getCompanies().forEach((company) -> {
			Instance.addCompanyStock(company);
		});
		while(true) {
			try {
				Instance.queryService();
				while(Instance.hasNext()) {
					db.save(db.mungee(Instance.next()));
				}
				Thread.sleep(86400000); //once a day
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
