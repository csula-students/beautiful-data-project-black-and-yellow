package edu.csula.datascience.acquisition.driver.worker;

import edu.csula.datascience.acquisition.driver.network.api.YoutubeApiDriver;

public class YoutubeApiWorker extends Thread {
	public void run() {
		YoutubeApiDriver Instance = YoutubeApiDriver.getInstance();
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
