package edu.csula.datascience.acquisition.driver.network.api;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.json.JSONObject;

import edu.csula.datascience.acquisition.driver.BaseApiDriver;
import edu.csula.datascience.acquisition.driver.BaseCallable;
import edu.csula.datascience.acquisition.driver.network.HTTPServiceDriver;
import edu.csula.datascience.acquisition.model.QuandlRevenueModel;

public class QuandlRevenueApiDriver extends BaseApiDriver<QuandlRevenueModel> {
	protected List<String> companies;

	public QuandlRevenueApiDriver() {
		super();
		this.companies = new ArrayList<>();
	}

	public void addCompanyStock(String stockName) {
		this.companies.add(stockName);
	}

	@Override
	public void queryService() {
		// TODO Auto-generated method stub
		for(String stockName : this.companies) {
			//https://www.quandl.com/api/v3/datasets/SEC/AAPL_SALESREVENUENET_Q.csv?api_key=YOURAPIKEY
			HTTPServiceDriver apiScrapper = new HTTPServiceDriver(this.config.get("revenue")+stockName+"_SALESREVENUENET_Q.csv");
			apiScrapper.setMethodGet();
			apiScrapper.setRequestData("api_key", this.config.get("key"));
			try {
				apiScrapper.connect();
				InputStream iStream = apiScrapper.getInputStream();
				if(iStream != null) {
					Scanner reader = new Scanner(iStream);
					//Dump the first line
					if(reader.hasNext()) {
						reader.nextLine();
					}

					while(reader.hasNext()) {
						String line = reader.nextLine();
						if(line != null && line.matches("^[0-9]+.*")) {
							String[] parts = line.split(",");
							HashMap<String,String> data = new HashMap<String,String>();
							if(parts.length == 2) {
								data.put("name", stockName);
								data.put("date", parts[0]);
								data.put("value", parts[1]);
								this.data.add(data);
							}
						}						
					}

					reader.close();
					iStream.close();
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
			//https://www.quandl.com/api/v3/datasets/SEC/AAPL_SALESREVENUENET_Q.csv?api_key=YOURAPIKEY
			HTTPServiceDriver apiScrapper = new HTTPServiceDriver(this.config.get("revenue")+stockName+"_SALESREVENUENET_Q.csv");
			apiScrapper.setMethodGet();
			apiScrapper.setRequestData("api_key", this.config.get("key"));
			try {
				apiScrapper.connect();
				InputStream iStream = apiScrapper.getInputStream();
				if(iStream != null) {
					Scanner reader = new Scanner(iStream);
					while(reader.hasNext()) {
						String line = reader.nextLine();
						if(line != null && (line.matches("^[0-9]+.*") || line.matches("^(Date|date).*"))) {
							String[] parts = line.split(",");							
							if(parts.length == 2) {
								JSONObject data = new JSONObject();
								if(line.matches("^(Date|date).*")) {
									data.put("name", "Name");
								} else {
									data.put("name", stockName);
								}
								data.put("date", parts[0]);
								data.put("value", parts[1]);
								callable.call(data);
							}
						}
					}

					reader.close();
					iStream.close();
				}
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
	}

	@Override
	public Collection<QuandlRevenueModel> next() {
		List<QuandlRevenueModel> ret = new ArrayList<>();
		while(this.data.size() > 0 && ret.size() < this.batchSize) {
			HashMap<String,String> row = this.data.remove(0);			
			
			try {
				QuandlRevenueModel model = new QuandlRevenueModel();
				model.name = row.get("name");
				model.date = row.get("date");
				model.value = Double.valueOf(row.get("value"));
				ret.add(model);
			} catch(NumberFormatException e) {
				//Dirty Data
			}			
		}
		return ret;
	}
}
