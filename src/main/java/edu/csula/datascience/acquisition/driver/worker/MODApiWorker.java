package edu.csula.datascience.acquisition.driver.worker;

import java.util.List;
import edu.csula.datascience.acquisition.driver.network.api.MarkitOnDemandApiDriver;
import edu.csula.datascience.acquisition.model.Company;
import edu.csula.datascience.acquisition.model.MarkitOnDemandModel;
import edu.csula.datascience.acquisition.runner.DataCollectionRunner;

public class MODApiWorker extends Thread {
	public void run() {
		MarkitOnDemandApiDriver Instance = MarkitOnDemandApiDriver.getInstance();
		List<Company> companies = DataCollectionRunner.getCompanies();
		for(Company company : companies) {
			for(String stock : company.stock) {
				Instance.addCompanyStock(stock);
			}
		}
		try {
			Instance.queryService();
			List<MarkitOnDemandModel> data = Instance.getData();
			Instance.emptyData();
			
			//TODO: Do Something with data			
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
