package edu.csula.datascience.acquisition.driver.network.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.util.JSON;

import edu.csula.datascience.acquisition.driver.BaseApiDriver;
import edu.csula.datascience.acquisition.driver.network.ApiServiceCallDriver;
import edu.csula.datascience.acquisition.model.MarkitOnDemandModel;

/**
 * @see http://dev.markitondemand.com/MODApis/
 */
public class MarkitOnDemandApiDriver extends BaseApiDriver {
	private static MarkitOnDemandApiDriver Instance = null;
	protected String responseFormat;
	
	protected List<String> companies;
	protected List<MarkitOnDemandModel> companyStockValues;
	
	public static MarkitOnDemandApiDriver getInstance() {
		if(Instance == null) {
			Instance = new MarkitOnDemandApiDriver();
		}
		
		return Instance;
	}
	
	protected MarkitOnDemandApiDriver() {
		this.companies = new ArrayList<>();
		companyStockValues = new ArrayList<MarkitOnDemandModel>();
	}
	
	public void addCompanyStock(String stockName) {
		this.companies.add(stockName);
	}

	@Override
	public void queryService() {
		if(!companyStockValues.isEmpty()) {
			return;
		}
		
		// TODO Auto-generated method stub
		this.companies.forEach((String stockName)->{
			ApiServiceCallDriver apiScrapper = new ApiServiceCallDriver(this.config.get("service"));
			apiScrapper.setMethodGet();
			apiScrapper.setRequestData("symbol", stockName);
			try {
				apiScrapper.connect();
				String response = apiScrapper.getContent();
				System.out.println(response);
				//MarkitOnDemandModel stockValue = (MarkitOnDemandModel)JSON.parse(response);
				
				//companyStockValues.add(stockValue);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//Failed to connect to api server
			}
		});
		
	}
	
	public List<MarkitOnDemandModel> getData() {
		return this.companyStockValues;
	}
	
	public void emptyData() {
		this.companyStockValues.clear();
	}
}
