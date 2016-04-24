package edu.csula.datascience.acquisition.driver.database.mongo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;

import edu.csula.datascience.acquisition.model.TweetModel;

public class TweetDataCollector<T extends TweetModel,A extends T> extends BaseMongoDbDataCollector<T,A> {
	private SimpleDateFormat dateParser;
	private int minutes;
	
	public TweetDataCollector(String dbHost,String dbCollection) {
		super(dbHost,dbCollection);
		dateParser = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
		minutes = -5;
	}
	
	public TweetDataCollector(String dbHost) {
		this(dbHost,"tweets");
	}
	
	public void setMinute(int minute){ 
		this.minutes = minute;
	}

	@Override
	public Collection<T> mungee(Collection<A> src) {
		List<T> ret = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, minutes);
		src.forEach((item) -> {
			try {
				Calendar tweetDate = Calendar.getInstance();
				tweetDate.setTime(dateParser.parse(item.created_at));
				if(calendar.compareTo(tweetDate) <= 0) {
					ret.add((T)item);
				}
				
			} catch (ParseException e) {
				//Do nothing
			}	
		});
		return ret;
	}

	@Override
	public void save(Collection<T> data) {
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
