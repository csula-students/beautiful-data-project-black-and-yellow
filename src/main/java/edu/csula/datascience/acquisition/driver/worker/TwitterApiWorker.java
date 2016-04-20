package edu.csula.datascience.acquisition.driver.worker;

import edu.csula.datascience.acquisition.driver.network.api.TwitterApiDriver;

public class TwitterApiWorker extends Thread {
	public void run() {
		TwitterApiDriver Instance = TwitterApiDriver.getInstance();
		if(Instance.authenticate()) {
			while(true) {
				try {
					Instance.queryService();
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
