package edu.csula.datascience.acquisition.model.database;

import org.json.JSONObject;

import edu.csula.datascience.acquisition.driver.database.BaseDatabaseModel;

public class MarkitOnDemandModel extends BaseDatabaseModel<MarkitOnDemandModel> {
	public String name;
	public String symbol;
	public double last_price;
	public double change;
	public double change_percent;
	public String timestamp;
	public double ms_date;
	public double market_cap;
	public double volume;
	public double change_ytd;
	public double change_percent_ytd;
	public double high;
	public double low;
	public double open;
	
	public void parseJSONObject(JSONObject model) {
		this.name = model.getString("name");
		this.symbol = model.getString("symbol");
		this.last_price = model.getDouble("last_price");
		this.change = model.getDouble("change");
		this.change_percent = model.getDouble("change_percent");
		this.timestamp = model.getString("timestamp");
		this.ms_date = model.getDouble("ms_date");
		this.market_cap = model.getDouble("market_cap");
		this.volume = model.getDouble("volume");
		this.change_ytd = model.getDouble("change_ytd");
		this.change_percent_ytd = model.getDouble("change_percent_ytd");
		this.high = model.getDouble("high");
		this.low = model.getDouble("low");
		this.open = model.getDouble("open");
	}
	
	public MarkitOnDemandModel clone() {
		MarkitOnDemandModel object = new MarkitOnDemandModel();
		object.name = this.name.toString();
		object.symbol = this.symbol.toString();
		object.last_price = this.last_price;
		object.change = this.change;
		object.change_percent = this.change_percent;
		object.timestamp = this.timestamp.toString();
		object.ms_date = this.ms_date;
		object.market_cap = this.market_cap;
		object.volume = this.volume;
		object.change_ytd = this.change_ytd;
		object.high = this.high;
		object.low = this.low;
		object.open = this.open;		
		return object;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		json.put("name", name);
		json.put("symbol", symbol);
		json.put("last_price", last_price);
		json.put("change", change);
		json.put("change_percent", change_percent);
		json.put("timestamp", timestamp);
		json.put("ms_date", ms_date);
		json.put("market_cap", market_cap);
		json.put("volume", volume);
		json.put("change_ytd",change_ytd);
		json.put("high", high);
		json.put("low", low);
		json.put("open",open);
		return json;
	}
}
