package edu.csula.datascience.acquisition.model.database;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import com.google.api.client.util.DateTime;

import edu.csula.datascience.acquisition.driver.database.BaseDatabaseModel;
import edu.csula.datascience.interfaces.PercentageDifference;

public class QuandlStockModel extends BaseDatabaseModel<QuandlStockModel> implements PercentageDifference<QuandlStockModel> {
	public String stock;
	public Date date;
	public double open;
	public double high;
	public double low;
	public double close;
	public double volume;
	
	public void parseJSONObject(JSONObject model) {
		this.stock = model.getString("stock");
		this.date = new Date(model.getJSONObject("date").getLong("$date"));
		this.open = model.getDouble("open");
		this.high = model.getDouble("high");
		this.low = model.getDouble("low");
		this.close = model.getDouble("close");
		this.volume = model.getDouble("volume");
	}
	
	public QuandlStockModel clone() {
		QuandlStockModel object = new QuandlStockModel();
		object.stock = this.stock.toString();
		object.date = new Date((new DateTime(this.date)).getValue());
		object.open = this.open;
		object.high = this.high;
		object.low = this.low;
		object.close = this.close;
		object.volume = this.volume;
		return object;
	}
	
	public JSONObject toJSONObject() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		JSONObject json = new JSONObject();
		json.put("stock", stock);
		json.put("date", calendar.getTimeInMillis());
		json.put("open", open);
		json.put("high", high);
		json.put("low", low);
		json.put("close", close);
		json.put("volume", volume);
		return json;
	}

	@Override
	public int compareTo(QuandlStockModel o) {
		if(this.stock.equals(o.stock)) {
			double _d1 = (this.high + this.low) / 2.0;
			double _d2 = (o.high + o.low) / 2.0;
			return _d1 > _d2 ? 1 : (_d1 < _d2 ? -1 : 0);
		}
		return 0;
	}

	@Override
	public double difference(QuandlStockModel o) {
		if(this.stock.equals(o.stock)) {
			double _d1 = (this.high + this.low) / 2.0;
			double _d2 = (o.high + o.low) / 2.0;
			return Math.abs(_d1 - _d2) / _d1 * 100.0;
		}
		return 0;
	}
	
	@Override
	public double difference() {
		return Math.abs(this.high - this.low) / this.open * 100.0;
	}
}
