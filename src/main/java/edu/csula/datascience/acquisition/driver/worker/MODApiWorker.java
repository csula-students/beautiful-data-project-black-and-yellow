package edu.csula.datascience.acquisition.driver.worker;

import edu.csula.datascience.acquisition.driver.network.api.MarkitOnDemandApiDriver;

public class MODApiWorker extends Thread {
	public void run() {
		MarkitOnDemandApiDriver Instance = MarkitOnDemandApiDriver.getInstance();
		try {
			Thread.sleep(10000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
