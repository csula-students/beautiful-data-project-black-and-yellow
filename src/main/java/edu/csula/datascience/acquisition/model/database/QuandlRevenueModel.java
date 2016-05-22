package edu.csula.datascience.acquisition.model.database;
import java.util.Date;

import org.json.JSONObject;

import edu.csula.datascience.acquisition.driver.database.BaseDatabaseModel;
import edu.csula.datascience.interfaces.*;

public class QuandlRevenueModel extends BaseDatabaseModel<QuandlRevenueModel> implements PercentageDifference<QuandlRevenueModel> {
	public String name;
	public Date date;
	public double value;
	
	public void parseJSONObject(JSONObject model) {
		this.name = model.getString("name");
		this.date = (Date)model.get("date");
		this.value = model.getDouble("value");
	}
	
	public QuandlRevenueModel clone() {
		QuandlRevenueModel object = new QuandlRevenueModel();
		object.name = this.name.toString();
		object.date = (Date)this.date.clone();
		object.value = this.value;
		return object;
	}

	@Override
	public int compareTo(QuandlRevenueModel o) {
		return this.value > o.value ? 1 : (this.value < o.value ? -1 : 0);
	}
	
	@Override
	public double difference(QuandlRevenueModel o) {
		return Math.abs(this.value - o.value) / this.value * 100.0;
	}
}
