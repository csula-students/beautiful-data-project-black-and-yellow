package edu.csula.datascience.acquisition.driver.database.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.Document;
import edu.csula.datascience.acquisition.model.QuandlRevenueModel;

public class QuandlRevenueDataCollector<T extends QuandlRevenueModel, A extends T> extends BaseMongoDbDataCollector<T,A> {
	public QuandlRevenueDataCollector(String dbHost,String dbCollection) {
		super(dbHost,dbCollection);
	}
	public QuandlRevenueDataCollector(String dbHost) {
		this(dbHost,"quandl_revenue");
	}
	
	@Override
	public Collection<T> mungee(Collection<A> src) {
		List<T> ret = new ArrayList<>();
		ret.addAll(src);
		return ret;
	}

	@Override
	public void save(Collection<T> data) {
		List<Document> documents = data.stream()
			.map(item -> new Document()
					.append("name", item.name)
					.append("date", item.date)
					.append("value", item.value)
				)
	            .collect(Collectors.toList());

		this.insertMany(documents);
	}
}
