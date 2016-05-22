package edu.csula.datascience.acquisition.model.query;

import org.json.JSONObject;

public class MongoConditionModel extends MongoQueryModel{
	public MongoConditionModel() {
		super();
		query.remove(COMMENT);
	}
	
	public MongoConditionModel(JSONObject _json) {
		super();
		query = _json;
	}
	
	public JSONObject getQuery() {
		return this.query;
	}
}
