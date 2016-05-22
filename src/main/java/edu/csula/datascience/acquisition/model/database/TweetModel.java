package edu.csula.datascience.acquisition.model.database;

import java.util.Date;

import org.json.JSONObject;

import com.google.api.client.util.DateTime;

import edu.csula.datascience.acquisition.driver.database.BaseDatabaseModel;

public class TweetModel extends BaseDatabaseModel<TweetModel>{
	public Date created_at;
	public long id;
	public String text;
	public int retweet_count;
	public int favorite_count;
	
	public void parseJSONObject(JSONObject model) {
		this.created_at = new Date(model.getJSONObject("created_at").getLong("$date"));;
		this.id = model.getLong("id");
		this.text = model.getString("text");
		this.retweet_count = model.getInt("retweet_count");
		this.favorite_count = model.getInt("favorite_count");		
	}
	
	public TweetModel clone() {
		TweetModel object = new TweetModel();
		object.created_at = new Date((new DateTime(this.created_at)).getValue());
		object.id = this.id;
		object.text = this.text.toString();
		object.retweet_count = this.retweet_count;
		object.favorite_count = this.favorite_count;
		return object;
	}
	
}
