package edu.csula.datascience.acquisition.model.database;

import org.json.JSONObject;

import edu.csula.datascience.acquisition.driver.database.BaseDatabaseModel;

public class AmazonModel extends BaseDatabaseModel<AmazonModel> {
	public String ticker;
	public String code;
	public String name;
	@Override
	public void parseJSONObject(JSONObject model) {
		this.ticker = model.getString("ticker");
		this.code = model.getString("code");
		this.name = model.getString("name");
		
	}
	@Override
	public AmazonModel clone() {
		AmazonModel object = new AmazonModel();
		object.ticker = this.ticker.toString();
		object.code = this.code.toString();
		object.name = this.name.toString();
		return object;
	}
	@Override
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		json.put("ticker", ticker);
		json.put("code", code);
		json.put("name", name);
		return json;
	}
}
