package edu.csula.datascience.acquisition.driver.network.api;

import java.io.IOException;
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
import edu.csula.datascience.acquisition.model.AmazonModel;

public class AmazonFinanceApiDriver extends BaseApiDriver<AmazonModel> {
	protected String exchange;
	public AmazonFinanceApiDriver(String exchange) {
		super();
		this.exchange = exchange;
	}

	@Override
	public Collection<AmazonModel> next() {
		List<AmazonModel> ret = new ArrayList<>();
		while(this.data.size() > 0 && ret.size() < this.batchSize) {
			AmazonModel model = new AmazonModel();
			HashMap<String,String> data = this.data.remove(0);
			model.ticker = data.get("ticker");
			model.code = data.get("code");
			model.name = data.get("name");
			ret.add(model);
		}
		return ret;
	}

	@Override
	public void queryService() {
		HTTPServiceDriver curl = new HTTPServiceDriver(this.config.get(this.exchange));
		curl.setMethodGet();
		try {
			curl.connect();
			InputStream iStream = curl.getInputStream();
			Scanner reader = new Scanner(iStream);
			if(reader.hasNext()) {
				//Dump first line
				reader.next();
			}
			
			while(reader.hasNext()) {
				String line = reader.nextLine();
				String[] parts = line.split(",");
				if(parts.length == 3) {
					HashMap<String,String> data = new HashMap<>();
					data.put("ticker",parts[0]);
					data.put("code",parts[1]);
					data.put("name",parts[2]);
					this.data.add(data);
				}
				
			}
			
			reader.close();
			iStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	@Override
	public void queryService(BaseCallable callback) {
		HTTPServiceDriver curl = new HTTPServiceDriver(this.config.get(this.exchange));
		curl.setMethodGet();
		try {
			curl.connect();
			InputStream iStream = curl.getInputStream();
			Scanner reader = new Scanner(iStream);
			if(reader.hasNext()) {
				//Dump first line
				reader.next();
			}
			
			while(reader.hasNext()) {
				String line = reader.nextLine();
				String[] parts = line.split(",");
				if(parts.length == 3) {
					JSONObject data = new JSONObject();
					data.put("ticker",parts[0]);
					data.put("code",parts[1]);
					data.put("name",parts[2]);
					callback.call(data);
				}
				
			}
			
			reader.close();
			iStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
