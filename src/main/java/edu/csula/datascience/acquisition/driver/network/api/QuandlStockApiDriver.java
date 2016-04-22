package edu.csula.datascience.acquisition.driver.network.api;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import edu.csula.datascience.acquisition.driver.BaseApiDriver;
import edu.csula.datascience.acquisition.driver.network.HTTPServiceDriver;
import edu.csula.datascience.acquisition.model.QuandlStockModel;

public class QuandlStockApiDriver extends BaseApiDriver<QuandlStockModel> {
	private static QuandlStockApiDriver Instance = null;
	
	protected List<String> companies;
	protected List<QuandlStockModel> dataValues;
	
	public static QuandlStockApiDriver getInstance() {
		if(Instance == null) {
			Instance = new QuandlStockApiDriver();
		}
		
		return Instance;
	}
	
	public QuandlStockApiDriver() {
		this.companies = new ArrayList<>();
		this.dataValues = new ArrayList<>();
	}
	
	public void addCompanyStock(String stockName) {
		this.companies.add(stockName);
	}
	
	@Override
	public void queryService() {
		// TODO Auto-generated method stub
		this.companies.forEach((String stockName)->{
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
						reader.next();
					}
					
					while(reader.hasNext()) {
						String line = reader.next();
						String[] parts = line.split(",");
						if(parts.length > 5) {
							QuandlStockModel stockValue = new QuandlStockModel();
							stockValue.stock = stockName;
							stockValue.date = parts[0];
							stockValue.open = parts[1];
							stockValue.high = parts[2];
							stockValue.low = parts[3];
							stockValue.close = parts[4];
							stockValue.volume = parts[5];
							dataValues.add(stockValue);
						}						
					}
					
					reader.close();
					iStream.close();
				}
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
				e.printStackTrace();
			}
		});
		
	}
	@Override
	public boolean hasNext() {
		return dataValues.size() > 0;
	}

	@Override
	public Collection<QuandlStockModel> next() {
		List<QuandlStockModel> ret = new ArrayList<>();
		for(int i = 0 ; i < batchSize && i < this.dataValues.size(); i++) {
			ret.add(this.dataValues.remove(0));
		}
		return ret;
	}
}
