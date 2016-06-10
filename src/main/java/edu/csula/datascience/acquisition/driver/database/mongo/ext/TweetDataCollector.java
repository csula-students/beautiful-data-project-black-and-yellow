package edu.csula.datascience.acquisition.driver.database.mongo.ext;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;

import edu.csula.datascience.acquisition.driver.database.mongo.BaseMongoDbDataCollector;
import edu.csula.datascience.acquisition.model.database.TweetModel;

public class TweetDataCollector extends BaseMongoDbDataCollector<TweetModel,TweetModel> {
	private int minutes;
	private int seconds;
	
	public TweetDataCollector(String dbHost,String dbCollection) {
		super(dbHost,dbCollection);
		minutes = 0;
		seconds = 0;
	}
	
	public TweetDataCollector(String dbHost) {
		this(dbHost,"tweets");
	}
	
	public void setMinute(int minute){ 
		this.minutes = minute;
	}
	
	public void setSecond(int second){ 
		this.seconds = second;
	}

	@Override
	public Collection<TweetModel> mungee(Collection<TweetModel> src) {
		List<TweetModel> ret = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, minutes);
		calendar.add(Calendar.SECOND, seconds);
		src.forEach((item) -> {
			Calendar tweetDate = Calendar.getInstance();
			tweetDate.setTime(item.created_at);
			if(calendar.compareTo(tweetDate) <= 0) {
				ret.add((TweetModel)item);
			}
		});
		return ret;
	}

	@Override
	public void save(Collection<TweetModel> data) {
		List<Document> documents = data.stream()
			.map(item -> new Document()
					.append("id", item.id)
					.append("created_at", item.created_at)
					.append("text", item.text)
					.append("retweet_count", item.retweet_count)
					.append("favorite_count", item.favorite_count)
				)
	            .collect(Collectors.toList());

		this.insertMany(documents);		
	}
}
