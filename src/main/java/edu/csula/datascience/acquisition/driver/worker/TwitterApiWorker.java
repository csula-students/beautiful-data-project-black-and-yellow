package edu.csula.datascience.acquisition.driver.worker;

import edu.csula.datascience.acquisition.driver.network.api.TwitterApiDriver;

public class TwitterApiWorker extends Thread {
	public void run() {
		TwitterApiDriver Instance = TwitterApiDriver.getInstance();
		while(true) {
			try {
				Thread.sleep(10000000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
