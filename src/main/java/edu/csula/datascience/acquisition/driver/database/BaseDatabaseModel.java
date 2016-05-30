package edu.csula.datascience.acquisition.driver.database;

import org.json.JSONObject;

public abstract class BaseDatabaseModel<T> {
	public abstract void parseJSONObject(JSONObject model);
	public abstract JSONObject toJSONObject();
	public abstract T clone();
}
