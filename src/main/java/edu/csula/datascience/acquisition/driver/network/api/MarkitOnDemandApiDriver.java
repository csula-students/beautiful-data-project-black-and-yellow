package edu.csula.datascience.acquisition.driver.network.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import edu.csula.datascience.acquisition.driver.BaseApiDriver;
import edu.csula.datascience.acquisition.driver.BaseCallable;
import edu.csula.datascience.acquisition.driver.network.HTTPServiceDriver;
import edu.csula.datascience.acquisition.model.database.MarkitOnDemandModel;

/**
 * @see http://dev.markitondemand.com/MODApis/
 */
public class MarkitOnDemandApiDriver extends BaseApiDriver<MarkitOnDemandModel> {
	private static MarkitOnDemandApiDriver Instance = null;
	protected String responseFormat;
	
	protected List<String> companies;
	
	public static MarkitOnDemandApiDriver getInstance() {
		if(Instance == null) {
			Instance = new MarkitOnDemandApiDriver();
		}
		
		return Instance;
	}
	
	protected MarkitOnDemandApiDriver() {
		super();
		this.companies = new ArrayList<>();
	}
	
	public void addCompanyStock(String stockName) {
		this.companies.add(stockName);
	}

	@Override
	public void queryService() {		
		// TODO Auto-generated method stub
		for(int i = 0; i < this.companies.size(); i++) {
			String stockName = this.companies.get(i);
			HTTPServiceDriver apiScrapper = new HTTPServiceDriver(this.config.get("service")+this.config.get("type"));
			apiScrapper.setMethodGet();
			apiScrapper.setRequestData("symbol", stockName);
			try {
				apiScrapper.connect();
				String response = apiScrapper.getContent();
				JSONObject json = new JSONObject(response);
				if(json != null && !json.has("Message")) {
					HashMap<String,String> data = new HashMap<>();
					Iterator<String> keyItr = json.keySet().iterator();
					while(keyItr.hasNext()) {
						String key = keyItr.next();
						data.put(key, json.get(key).toString());
					}
					this.data.add(data);
				}
			} catch (JSONException e) {
				if(e.getLocalizedMessage().startsWith("A JSONObject text must begin with '{'")) {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e1) {
						//Do nothing
					}
					i--;
					continue;
				} else {
					System.out.println("Error with parsing JSON: " + e.getLocalizedMessage());
					e.printStackTrace();
				}				
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
				e.printStackTrace();
			}
		}		
	}
	
	@Override
	public void queryService(BaseCallable callable) {		
		// TODO Auto-generated method stub
		for(String stockName : this.companies) {
			HTTPServiceDriver apiScrapper = new HTTPServiceDriver(this.config.get("service")+this.config.get("type"));
			apiScrapper.setMethodGet();
			apiScrapper.setRequestData("symbol", stockName);
			try {
				apiScrapper.connect();
				String response = apiScrapper.getContent();
				JSONObject json = new JSONObject(response);
				if(json != null) {
					callable.call(json);
				}
			} catch (JSONException e) {
				System.out.println("Error with parsing JSON: " + e.getLocalizedMessage());
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(e.getLocalizedMessage());
				e.printStackTrace();
			}
		}		
	}

	@Override
	public Collection<MarkitOnDemandModel> next() {
		List<MarkitOnDemandModel> ret = new ArrayList<MarkitOnDemandModel>();
		while(this.data.size() > 0 && ret.size() < this.batchSize) {
			HashMap<String,String> row = this.data.remove(0);
			
			try {
				MarkitOnDemandModel model = new MarkitOnDemandModel();
				model.name = row.get("Name");
				model.change = Double.valueOf(row.get("Change"));
				model.change_percent = Double.valueOf(row.get("ChangePercent"));
				model.change_percent_ytd = Double.valueOf(row.get("ChangePercentYTD"));
				model.high = Double.valueOf(row.get("High"));
				model.last_price = Double.valueOf(row.get("LastPrice"));
				model.low = Double.valueOf(row.get("Low"));
				model.market_cap = Double.valueOf(row.get("MarketCap"));
				model.ms_date = Double.valueOf(row.get("MSDate"));
				model.open = Double.valueOf(row.get("Open"));
				model.symbol = row.get("Symbol");
				model.timestamp = row.get("Timestamp");
				model.volume = Double.valueOf(row.get("Volume"));
				
				ret.add(model);
			} catch(NullPointerException e) {
				//Dirty Data
			}			
		}
		
		return ret;
	}
}
