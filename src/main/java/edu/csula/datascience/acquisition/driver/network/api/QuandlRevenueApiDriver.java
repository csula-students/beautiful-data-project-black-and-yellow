package edu.csula.datascience.acquisition.driver.network.api;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import edu.csula.datascience.acquisition.driver.BaseApiDriver;
import edu.csula.datascience.acquisition.driver.network.HTTPServiceDriver;
import edu.csula.datascience.acquisition.model.QuandlRevenueModel;

public class QuandlRevenueApiDriver extends BaseApiDriver<QuandlRevenueModel> {
	private static QuandlRevenueApiDriver Instance = null;

	protected List<String> companies;
	protected List<QuandlRevenueModel> dataValues;

	public static QuandlRevenueApiDriver getInstance() {
		if(Instance == null) {
			Instance = new QuandlRevenueApiDriver();
		}

		return Instance;
	}

	public QuandlRevenueApiDriver() {
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
						reader.next();
					}

					while(reader.hasNext()) {
						String line = reader.next();
						String[] parts = line.split(",");
						if(parts.length >= 2) {
							QuandlRevenueModel companyValue = new QuandlRevenueModel();
							companyValue.name = stockName;
							companyValue.date = parts[0];
							companyValue.value = parts[1];
							dataValues.add(companyValue);
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
	public Collection<QuandlRevenueModel> next() {
		List<QuandlRevenueModel> ret = new ArrayList<>();
		for(int i = 0 ; i < batchSize && this.dataValues.size() > 0; i++) {
			ret.add(this.dataValues.remove(0));
		}
		return ret;
	}
}
