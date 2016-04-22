package edu.csula.datascience.acquisition.driver.database.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;

import edu.csula.datascience.acquisition.model.QuandlStockModel;

public class QuandlStockDataCollector<T extends QuandlStockModel, A extends T> extends BaseMongoDbDataCollector<T,A> {
	public QuandlStockDataCollector(String dbHost) {
		super(dbHost,"quandl_stocks");
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
					.append("stock", item.stock)
					.append("date", item.date)
					.append("open", item.open)
					.append("high", item.high)
					.append("low", item.low)
					.append("close", item.close)
					.append("volume", item.volume)
				)
	            .collect(Collectors.toList());

		this.insertMany(documents);
	}
}
