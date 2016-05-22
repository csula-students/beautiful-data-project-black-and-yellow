package edu.csula.datascience.acquisition.driver.network.api;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.json.JSONObject;

import edu.csula.datascience.acquisition.driver.BaseApiDriver;
import edu.csula.datascience.acquisition.driver.BaseCallable;
import edu.csula.datascience.acquisition.driver.network.HTTPServiceDriver;
import edu.csula.datascience.acquisition.model.database.QuandlStockModel;

public class QuandlStockApiDriver extends BaseApiDriver<QuandlStockModel>  {
	protected List<String> companies;
	protected SimpleDateFormat dateParser;
	
	public QuandlStockApiDriver() {
		super();
		this.companies = new ArrayList<>();
		this.dateParser = new SimpleDateFormat("yyyy-MM-dd");
	}
	
	public void addCompanyStock(String stockName) {
		this.companies.add(stockName);
	}
	
	@Override
	public void queryService() {
		// TODO Auto-generated method stub
		for(String stockName : this.companies) {
			//https://www.quandl.com/api/v3/datasets/WIKI/AAPL.csv?api_key=YOURAPIKEY
			HTTPServiceDriver apiScrapper = new HTTPServiceDriver(this.config.get("wiki")+stockName+".csv");
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
							if(parts.length >= 13) {
								data.put("name", stockName);
								data.put("date",parts[0]);
								data.put("open", parts[1]);
								data.put("high", parts[2]);
								data.put("low", parts[3]);
								data.put("close", parts[4]);
								data.put("volume", parts[5]);
								data.put("ex-dividend", parts[6]);
								data.put("split ratio", parts[7]);
								data.put("adj. open", parts[8]);
								data.put("adj. high", parts[9]);
								data.put("adj. low", parts[10]);
								data.put("adj. close", parts[11]);
								data.put("adj. volume", parts[12]);
								
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
			//https://www.quandl.com/api/v3/datasets/WIKI/AAPL.csv?api_key=YOURAPIKEY
			HTTPServiceDriver apiScrapper = new HTTPServiceDriver(this.config.get("wiki")+stockName+".csv");
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
							JSONObject data = new JSONObject();
							if(parts.length >= 13) {
								if(line.matches("^(Date|date).*")) {
									data.put("name", "Name");
								} else {
									data.put("name", stockName);
								}
								data.put("date",parts[0]);
								data.put("open", parts[1]);
								data.put("high", parts[2]);
								data.put("low", parts[3]);
								data.put("close", parts[4]);
								data.put("volume", parts[5]);
								data.put("ex-dividend", parts[6]);
								data.put("split ratio", parts[7]);
								data.put("adj. open", parts[8]);
								data.put("adj. high", parts[9]);
								data.put("adj. low", parts[10]);
								data.put("adj. close", parts[11]);
								data.put("adj. volume", parts[12]);
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
	public Collection<QuandlStockModel> next() {
		List<QuandlStockModel> ret = new ArrayList<>();
		while(this.data.size() > 0 && ret.size() < this.batchSize) {
			HashMap<String,String> row = this.data.remove(0);
			
			try {
				QuandlStockModel model = new QuandlStockModel();
				model.name = row.get("name");
				model.date = this.dateParser.parse(row.get("date"));
				model.high = Double.valueOf(row.get("high"));
				model.low = Double.valueOf(row.get("low"));
				model.open = Double.valueOf(row.get("open"));
				model.volume = Double.valueOf(row.get("volume"));
				ret.add(model);
			} catch(NumberFormatException e) {
				//Dirty Data
			} catch (ParseException e) {
				//Dirty Date
			}			
		}
		return ret;
	}
}
