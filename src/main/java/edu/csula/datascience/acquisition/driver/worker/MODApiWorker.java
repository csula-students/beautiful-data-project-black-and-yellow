package edu.csula.datascience.acquisition.driver.worker;

import java.util.List;

import edu.csula.datascience.acquisition.driver.database.mongo.MODDataCollector;
import edu.csula.datascience.acquisition.driver.network.api.MarkitOnDemandApiDriver;
import edu.csula.datascience.acquisition.model.Company;
import edu.csula.datascience.acquisition.model.MarkitOnDemandModel;
import edu.csula.datascience.acquisition.runner.DataCollectionRunner;

public class MODApiWorker extends Thread {
	protected String dbHost;
	
	public MODApiWorker(String dbHost) {
		this.dbHost = dbHost;
	}

	public void run() {
		MarkitOnDemandApiDriver Instance = MarkitOnDemandApiDriver.getInstance();
		MODDataCollector<MarkitOnDemandModel,MarkitOnDemandModel> db = new MODDataCollector<>(this.dbHost);
		List<Company> companies = DataCollectionRunner.getCompanies();
		for(Company company : companies) {
			for(String stock : company.stock) {
				Instance.addCompanyStock(stock);
			}
		}
		while(true) {
			try {
				Instance.queryService();
				while(Instance.hasNext()) {
					db.save(db.mungee(Instance.next()));
				}
				
				//TODO: Do Something with data			
				Thread.sleep(3600000); //Once an hour
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
