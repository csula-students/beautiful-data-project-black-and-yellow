package edu.csula.datascience.acquisition.model.database;
import java.util.Date;

import org.json.JSONObject;

import edu.csula.datascience.acquisition.driver.database.BaseDatabaseModel;
import edu.csula.datascience.interfaces.PercentageDifference;

public class QuandlStockModel extends BaseDatabaseModel<QuandlStockModel> implements PercentageDifference<QuandlStockModel> {
	public String name;
	public Date date;
	public double open;
	public double high;
	public double low;
	public double close;
	public double volume;
	
	public void parseJSONObject(JSONObject model) {
		this.name = model.getString("name");
		this.date = (Date)model.get("date");
		this.open = model.getDouble("open");
		this.high = model.getDouble("high");
		this.low = model.getDouble("low");
		this.close = model.getDouble("close");
		this.volume = model.getDouble("volume");
	}
	
	public QuandlStockModel clone() {
		QuandlStockModel object = new QuandlStockModel();
		object.name = this.name.toString();
		object.date = (Date)this.date.clone();
		object.open = this.open;
		object.high = this.high;
		object.low = this.low;
		object.close = this.close;
		object.volume = this.volume;
		return object;
	}

	@Override
	public int compareTo(QuandlStockModel o) {
		double _d1 = (this.open + this.close) / 2.0;
		double _d2 = (o.open + o.close) / 2.0;
		return _d1 > _d2 ? 1 : (_d1 < _d2 ? -1 : 0);
	}

	@Override
	public double difference(QuandlStockModel o) {
		double _d1 = (this.open + this.close) / 2.0;
		double _d2 = (o.open + o.close) / 2.0;
		return Math.abs(_d1 - _d2) / _d1 * 100.0;
	}
}
