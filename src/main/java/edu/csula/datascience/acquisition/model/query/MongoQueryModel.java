package edu.csula.datascience.acquisition.model.query;

import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Date;

import edu.csula.datascience.acquisition.driver.database.BaseQueryModel;

public class MongoQueryModel extends BaseQueryModel {
	public static String COMMENT = "$comment";
	public static String EXPLAIN = "$explain";
	public static String QUERY = "$query";
	public static String MAX_SCAN = "$maxScan";
	public static String MAX_TIME_MS = "$maxTimeMS";
	public static String MAX = "$max";
	public static String MIN = "$min";
	public static String ORDER_BY = "$orderby";

	public enum QueryParamComparison {EQ, GT, GTE, LT, LTE, NE};
	public enum QueryParamComparisonArray {IN, NIN};
	public enum QueryParamLogical {NOT};
	public enum QueryParamLogicalArray {OR, AND, NOR};
	public enum QueryParamElement {EXISTS, TYPE};
	//public enum QueryParamEvaluation {MOD, REGEX, TEXT, WHERE};
	//public enum QueryParamGeospatial {GEO_WITHIN, GEO_INTERSECTS, NEAR, NEAR_SPHERE};
	//public enum QueryParamArray {ALL, ELEM_MATCH, SIZE};
	public enum Type {DOUBLE,STRING,OBJECT,ARRAY,BINARY,UNDEFINED,OBJECT_ID,BOOLEAN,DATE,NULL,REGEX,JS,SYMBOL,JS_SCOPE,INT_32,TIMESTAMP,INT_64,MIN_KEY,MAX_KEY};	
	
	protected JSONObject query;
	public MongoQueryModel() {
		query = new JSONObject();
		query.put(COMMENT, new JSONObject());
	}
	
	protected int getType(Type choice) {
		int type = 0;
		switch(choice) {
			case DOUBLE:
				type = 1;
				break;
			case STRING:
				type = 2;
				break;
			case OBJECT:
				type = 3;
				break;
			case ARRAY:
				type = 4;
				break;
			case BINARY:
				type = 5;
				break;
			case UNDEFINED:
				type = 6;
				break;
			case OBJECT_ID:
				type = 7;
				break;
			case BOOLEAN:
				type = 8;
				break;
			case DATE:
				type = 9;
				break;
			case NULL:
				type = 10;
				break;
			case REGEX:
				type = 11;
				break;
			case JS:
				type = 13;
				break;
			case SYMBOL:
				type = 14;
				break;
			case JS_SCOPE:
				type = 15;
				break;
			case INT_32:
				type = 16;
				break;
			case TIMESTAMP:
				type = 17;
				break;
			case INT_64:
				type = 18;
				break;
			case MIN_KEY:
				type = -1;
				break;
			case MAX_KEY:
				type = 127;
				break;
		}
		return type;
	}
	
	protected String getComparison(QueryParamComparison choice) {
		String message = "";
		switch(choice) {
			case EQ:
				message = "$eq";
				break;
			case GT:
				message = "$gt";
				break;
			case GTE:
				message = "$gte";
				break;
			case LT:
				message = "$lt";
				break;
			case LTE:
				message = "$lte";
				break;
			case NE:
				message = "$ne";
				break;
		}
		return message;
	}
	
	protected String getComparisonArray(QueryParamComparisonArray choice) {
		String message = "";
		switch(choice) {
			case IN:
				message = "$in";
				break;
			case NIN:
				message = "$nin";
				break;
		}
		return message;
	}
	
	protected String getLogcial(QueryParamLogical choice) {
		String message = "";
		switch(choice) {
			case NOT:
				message = "$not";
				break;
		}
		return message;
	}
	
	protected String getLogcialArray(QueryParamLogicalArray choice) {
		String message = "";
		switch(choice) {
			case OR:
				message = "$or";
				break;
			case AND:
				message = "$and";
				break;
			case NOR:
				message = "$nor";
				break;
		}
		return message;
	}
	
	protected String getElement(QueryParamElement choice) {
		String message = "";
		switch(choice) {
			case EXISTS:
				message = "$exists";
				break;
			case TYPE:
				message = "$type";
				break;
		}
		return message;
	}
	
	public void setComment(String message) {
		this.query.put(COMMENT, message);
	}
	
	public String getComment() {
		return this.query.getString(COMMENT);
	}
	
	public void setExplain(boolean bool) {
		this.query.put(EXPLAIN, bool);
	}
	
	public boolean getExplain() {
		return this.query.getBoolean(EXPLAIN);
	}
	
	public void setMaxScan(int num) {
		this.query.put(MAX_SCAN, num);
	}
	
	public int getMaxScan() {
		return this.query.getInt(MAX_SCAN);
	}
	
	public void setMaxTime(long ms) {
		this.query.put(MAX_TIME_MS, ms);
	}
	
	public long getMaxTime() {
		return this.query.getLong(MAX_TIME_MS);
	}
	
	public void setMax(int num) {
		this.query.put(MAX, num);
	}
	
	public int getMax() {
		return this.query.getInt(MAX);
	}
	
	public void setMin(int num) {
		this.query.put(MIN, num);
	}
	
	public int getMin() {
		return this.query.getInt(MIN);
	}
	
	public void setQuery(JSONObject json) {
		this.query.put(QUERY, json);
	}
	
	public JSONObject getQuery() {
		return this.query.getJSONObject(QUERY);
	}	
	
	public void setParamEqualTo(String param, String value) {
		JSONObject json = this.getQuery();
		json.put(param, value);
		this.setQuery(json);
	}
	
	public String getParamAsString(String param) throws JSONException {
		return this.getQuery().getString(param);
	}
	
	public void setParamEqualTo(String param, int value) {
		JSONObject json = this.getQuery();
		json.put(param, value);
		this.setQuery(json);
	}
	
	public int getParamAsInt(String param) throws JSONException {
		return this.getQuery().getInt(param);
	}
	
	public void setParamEqualTo(String param, long value) {
		JSONObject json = this.getQuery();
		json.put(param, value);
		this.setQuery(json);
	}
	
	public long getParamAsLong(String param) throws JSONException {
		return this.getQuery().getLong(param);
	}
	
	public void setParamEqualTo(String param, double value) {
		JSONObject json = this.getQuery();
		json.put(param, value);
		this.setQuery(json);
	}
	
	public double getParamAsDouble(String param) throws JSONException {
		return this.getQuery().getDouble(param);
	}
	
	public void setParamEqualTo(String param, Date value) {
		JSONObject json = this.getQuery();
		json.put(param, value);
		this.setQuery(json);
	}
	
	public Date getParamAsDate(String param) throws JSONException {
		return (Date)this.getQuery().get(param);
	}
	
	public void setParamEqualTo(String param, Object value) {
		JSONObject json = this.getQuery();
		json.put(param, value);
		this.setQuery(json);
	}
	
	public Object getParamAsObject(String param) throws JSONException {
		return this.getQuery().get(param);
	}
	
	public void setParamEqualTo(String param, JSONObject value) {
		JSONObject json = this.getQuery();
		json.put(param, value);
		this.setQuery(json);
	}
	
	public JSONObject getParamAsJSONObject(String param) throws JSONException {
		return this.getQuery().getJSONObject(param);
	}
	
	public void setParamEqualTo(String param, JSONArray value) {
		JSONObject json = this.getQuery();
		json.put(param, value);
		this.setQuery(json);
	}
	
	public JSONArray getParamAsJSONArray(String param) throws JSONException {
		return this.getQuery().getJSONArray(param);
	}
	
	public void setParamComparison(String param, QueryParamComparison choice, boolean value) {
		JSONObject json;
		try {
			json = this.getParamAsJSONObject(param);
		} catch(JSONException e) {
			json = new JSONObject();
		}
		json.put(this.getComparison(choice), value);
		this.setParamEqualTo(param,json);
	}
	
	public void setParamComparison(String param, QueryParamComparison choice, String value) {
		JSONObject json;
		try {
			json = this.getParamAsJSONObject(param);
		} catch(JSONException e) {
			json = new JSONObject();
		}
		json.put(this.getComparison(choice), value);
		this.setParamEqualTo(param,json);
	}
	
	public void setParamComparison(String param, QueryParamComparison choice, int value) {
		JSONObject json;
		try {
			json = this.getParamAsJSONObject(param);
		} catch(JSONException e) {
			json = new JSONObject();
		}
		json.put(this.getComparison(choice), value);
		this.setParamEqualTo(param,json);
	}
	
	public void setParamComparison(String param, QueryParamComparison choice, long value) {
		JSONObject json;
		try {
			json = this.getParamAsJSONObject(param);
		} catch(JSONException e) {
			json = new JSONObject();
		}
		json.put(this.getComparison(choice), value);
		this.setParamEqualTo(param,json);
	}
	
	public void setParamComparison(String param, QueryParamComparison choice, double value) {
		JSONObject json;
		try {
			json = this.getParamAsJSONObject(param);
		} catch(JSONException e) {
			json = new JSONObject();
		}
		json.put(this.getComparison(choice), value);
		this.setParamEqualTo(param,json);
	}
	
	public void setParamComparison(String param, QueryParamComparison choice, Date value) {
		JSONObject json;
		try {
			json = this.getParamAsJSONObject(param);
		} catch(JSONException e) {
			json = new JSONObject();
		}
		json.put(this.getComparison(choice), value);
		this.setParamEqualTo(param,json);
	}
	
	public void setParamComparisonArray(String param, QueryParamComparisonArray choice, JSONArray value) {
		JSONObject json;
		try {
			json = this.getParamAsJSONObject(param);
		} catch(JSONException e) {
			json = new JSONObject();
		}
		json.put(this.getComparisonArray(choice), value);
		this.setParamEqualTo(param,json);
	}
	
	public void setParamComparisonArray(String param, QueryParamComparisonArray choice, Collection<?> value) {
		JSONObject json;
		try {
			json = this.getParamAsJSONObject(param);
		} catch(JSONException e) {
			json = new JSONObject();
		}
		JSONArray _list = new JSONArray();
		_list.put(value);
		json.put(this.getComparisonArray(choice), _list);
		this.setParamEqualTo(param,json);
	}
	
	public void setParamComparisonArray(String param, QueryParamComparisonArray choice, MongoConditionModel value1, MongoConditionModel value2) {
		JSONArray _list = new JSONArray();
		_list.put(0,value1.toJSONObject());
		_list.put(1,value2.toJSONObject());
		this.setParamComparisonArray(param, choice, _list);
	}
	
	public void setParamComparisonArray(String param, QueryParamComparisonArray choice, MongoConditionModel value1, MongoConditionModel value2, MongoConditionModel value3) {
		JSONArray _list = new JSONArray();
		_list.put(0,value1.toJSONObject());
		_list.put(1,value2.toJSONObject());
		_list.put(2,value3.toJSONObject());
		this.setParamComparisonArray(param, choice, _list);
	}
	
	public void setParamLogical(String param, QueryParamLogical choice, JSONObject value) {
		JSONObject json;
		try {
			json = this.getParamAsJSONObject(param);
		} catch(JSONException e) {
			json = new JSONObject();
		}
		json.put(this.getLogcial(choice), value);
		this.setParamEqualTo(param,json);
	}
	
	public void setParamLogicalArray(String param, QueryParamLogicalArray choice, JSONArray value) {
		JSONObject json;
		try {
			json = this.getParamAsJSONObject(param);
		} catch(JSONException e) {
			json = new JSONObject();
		}
		json.put(this.getLogcialArray(choice), value);
		this.setParamEqualTo(param,json);
	}
	
	public void setParamLogicalArray(String param, QueryParamLogicalArray choice, Collection<JSONObject> value) {
		JSONObject json;
		try {
			json = this.getParamAsJSONObject(param);
		} catch(JSONException e) {
			json = new JSONObject();
		}
		JSONArray _list = new JSONArray();
		_list.put(value);
		json.put(this.getLogcialArray(choice), _list);
		this.setParamEqualTo(param,json);
	}
	
	public void setParamLogicalArray(String param, QueryParamLogicalArray choice, MongoQueryModel value1, MongoQueryModel value2) {
		JSONArray _list = new JSONArray();
		_list.put(0,value1.toJSONObject());
		_list.put(1,value2.toJSONObject());
		this.setParamLogicalArray(param, choice, _list);
	}
	
	public void setParamLogicalArray(String param, QueryParamLogicalArray choice, MongoQueryModel value1, MongoQueryModel value2, MongoQueryModel value3) {
		JSONArray _list = new JSONArray();
		_list.put(0,value1.toJSONObject());
		_list.put(1,value2.toJSONObject());
		_list.put(2,value3.toJSONObject());
		this.setParamLogicalArray(param, choice, _list);
	}
	
	public void setParamExists(String param, boolean value) {
		JSONObject json;
		try {
			json = this.getParamAsJSONObject(param);
		} catch(JSONException e) {
			json = new JSONObject();
		}
		json.put(this.getElement(QueryParamElement.EXISTS), value);
		this.setQuery(json);
	}
	
	public void setParamType(String param, Type choice) {
		JSONObject json;
		try {
			json = this.getParamAsJSONObject(param);
		} catch(JSONException e) {
			json = new JSONObject();
		}
		json.put(this.getElement(QueryParamElement.TYPE), this.getType(choice));
		this.setQuery(json);
	}
	
	public String toString() {
		return query.toString();
	}
	public JSONObject toJSONObject() {
		return query;
	}
	public Bson toBson() {
		return BsonDocument.parse(this.toJSONObject().toString());
	}
}
