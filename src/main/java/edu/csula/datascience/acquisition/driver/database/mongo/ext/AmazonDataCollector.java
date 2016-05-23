package edu.csula.datascience.acquisition.driver.database.mongo.ext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;

import edu.csula.datascience.acquisition.driver.database.mongo.BaseMongoDbDataCollector;
import edu.csula.datascience.acquisition.model.database.AmazonModel;

public class AmazonDataCollector extends BaseMongoDbDataCollector<AmazonModel,AmazonModel> {

	public AmazonDataCollector(String dbHost) {
		this(dbHost,"amazon");
	}
	
	public AmazonDataCollector(String dbHost, String collectionName) {
		super(dbHost, collectionName);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Collection<AmazonModel> mungee(Collection<AmazonModel> src) {
		List<AmazonModel> ret = new ArrayList<>();
		ret.addAll(src);
		return ret;
	}
	
	@Override
	public void save(Collection<AmazonModel> data) {
		List<Document> documents = data.stream()
				.map(item -> new Document()
						.append("ticker", item.ticker)
						.append("code", item.code)
						.append("name", item.name)
					)
		            .collect(Collectors.toList());

			this.insertMany(documents);
	}

}
