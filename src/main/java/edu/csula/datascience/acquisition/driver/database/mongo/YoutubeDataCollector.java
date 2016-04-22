package edu.csula.datascience.acquisition.driver.database.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;

import edu.csula.datascience.acquisition.model.YoutubeModel;

public class YoutubeDataCollector<T extends YoutubeModel,A extends T> extends BaseMongoDbDataCollector<T,A> {
	public YoutubeDataCollector(String dbHost) {
		super(dbHost,"youtubes");
	}
	@Override
	public Collection<T> mungee(Collection<A> src) {
		List<T> ret = new ArrayList<>();
		ret.addAll(src);
		//Cleanup
		src.clear();
		return ret;
	}

	@Override
	public void save(Collection<T> data) {
		List<Document> documents = data.stream()
			.map(item -> new Document()
					.append("id", item.id)
					.append("channel_id", item.channel_id)
					.append("title", item.title)
					.append("description", item.description)
					.append("published", item.published.getValue())
				)
	            .collect(Collectors.toList());

		this.insertMany(documents);		
	}
}
