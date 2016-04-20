package edu.csula.datascience.acquisition.driver.network.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mongodb.util.JSON;

import edu.csula.datascience.acquisition.driver.BaseApiDriver;
import edu.csula.datascience.acquisition.driver.network.ApiServiceCallDriver;
import edu.csula.datascience.acquisition.model.MarkitOnDemandModel;

/**
 * @see http://dev.markitondemand.com/MODApis/
 */
public class MarkitOnDemandApiDriver extends BaseApiDriver<MarkitOnDemandModel> {
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

	@Override
	public boolean hasNext() {
		return this.companyStockValues.size() > 0;
	}

	@Override
	public Collection<MarkitOnDemandModel> next() {
		List<MarkitOnDemandModel> ret = new ArrayList<MarkitOnDemandModel>();
		for(int i = 0 ; i < batchSize && i < this.companyStockValues.size(); i++) {
			ret.add(this.companyStockValues.remove(0));
		}
		return ret;
	}
}
