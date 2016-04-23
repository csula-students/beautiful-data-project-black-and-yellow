package edu.csula.datascience.acquisition.driver;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import edu.csula.datascience.acquisition.model.Company;

public abstract class BaseApiDriverTest {
	public static final String MOD = "markitOnDemand";
	public static final String YOUTUBE = "youtube";
	public static final String TWITTER = "twitter";
	public static final String GOOGLE = "google";
	public static final String QUANDL = "quandl";
	
	protected String dbHost;
	protected List<Company> companies;
	protected HashMap<String,HashMap<String,String>> apiConfigs;
	
	protected final void loadConfig() {		
		companies = new ArrayList<>();
		apiConfigs = new HashMap<>();
		
		//Read Config File
		try {
			//Load Config file into string
			Scanner scan = new Scanner(new FileReader("./config.json"));
			String jsonStr = "";
			while(scan.hasNext()) {
				jsonStr += scan.nextLine();
			}
			scan.close();
			
			// Parse the JSON string to a JSONObject
			JSONObject json = new JSONObject(jsonStr);
			JSONArray rows = null;
			JSONObject item = null;
			
			rows = json.getJSONArray("companies");
			if(rows != null) {
				for(int i = 0 ; i < rows.length(); i++) {
					item = rows.getJSONObject(i);
					JSONArray _rows = null;
					
					List<String> alias = new ArrayList<>();
					List<String> stock = new ArrayList<>();
					
					_rows = item.getJSONArray("alias");
					for(int j = 0; j < _rows.length(); j++) {
						alias.add(_rows.getString(j));
					}
					
					_rows = item.getJSONArray("stock");
					for(int j = 0; j < _rows.length(); j++) {
						stock.add(_rows.getString(j));
					}
					
					companies.add(new Company(item.getString("name"),alias,stock));
				}
			}
			
			item = json.getJSONObject("api");
			if(item != null) {
				String[] names = {GOOGLE,TWITTER,MOD,QUANDL};
				for(String name : names) {
					JSONObject _item = item.getJSONObject(name);
					HashMap<String,String> data = new HashMap<>();
					
					for(String prop : JSONObject.getNames(_item)) {
						data.put(prop,_item.getString(prop));
					}
					
					apiConfigs.put(name,data);
				}
			}
			
			dbHost = json.getString("dbHost");
		} catch (FileNotFoundException e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}
}
