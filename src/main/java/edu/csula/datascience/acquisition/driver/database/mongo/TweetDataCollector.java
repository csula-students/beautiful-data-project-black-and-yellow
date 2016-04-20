package edu.csula.datascience.acquisition.driver.database.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;

import edu.csula.datascience.acquisition.model.TweetModel;

public class TweetDataCollector<T extends TweetModel,A extends T> extends BaseMongoDbDataCollector<T,A> {
	protected String collectionName;

	public TweetDataCollector() {
		this.collectionName = "tweets";
	}

	@Override
	public Collection<T> mungee(Collection<A> src) {
		List<T> ret = new ArrayList<>();
		for(A item : src) {
			ret.add((T)item);
		}
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

	@Override
	protected String getCollectionName() {
		return this.collectionName;
	}
}
