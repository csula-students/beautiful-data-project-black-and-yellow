package edu.csula.datascience.acquisition.driver.database.mongo.ext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;

import edu.csula.datascience.acquisition.driver.database.mongo.BaseMongoDbDataCollector;
import edu.csula.datascience.acquisition.model.database.YoutubeModel;

public class YoutubeDataCollector extends BaseMongoDbDataCollector<YoutubeModel,YoutubeModel> {
	public YoutubeDataCollector(String dbHost,String dbCollection) {
		super(dbHost,dbCollection);
	}
	
	public YoutubeDataCollector(String dbHost) {
		this(dbHost,"youtubes");
	}
	@Override
	public Collection<YoutubeModel> mungee(Collection<YoutubeModel> src) {
		List<YoutubeModel> ret = new ArrayList<>();
		ret.addAll(src);
		return ret;
	}

	@Override
	public void save(Collection<YoutubeModel> data) {
		List<Document> documents = data.stream()
			.map(item -> new Document()
					.append("id", item.id)
					.append("channel_id", item.channel_id)
					.append("title", item.title)
					.append("description", item.description)
					.append("published", item.published)
				)
	            .collect(Collectors.toList());

		this.insertMany(documents);		
	}
}
