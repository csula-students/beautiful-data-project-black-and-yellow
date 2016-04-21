package edu.csula.datascience.acquisition.driver.database.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;

import edu.csula.datascience.acquisition.model.MarkitOnDemandModel;

public class MODDataCollector<T extends MarkitOnDemandModel,A extends T> extends BaseMongoDbDataCollector<T,A>  {
	public MODDataCollector(String dbHost) {
		super(dbHost,"stocks");
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
					.append("name", item.name)
					.append("symbol", item.symbol)
					.append("last_price", item.last_price)
					.append("change", item.change)
					.append("change_percent", item.change_percent)
					.append("timestamp", item.timestamp)
					.append("msdate", item.ms_date)
					.append("market_cap", item.market_cap)
					.append("volume", item.volume)
					.append("change_ytd", item.change_ytd)
					.append("change_percent_ytd", item.change_percent_ytd)
					.append("high",item.high)
					.append("low", item.low)
					.append("open", item.open)
				)
	            .collect(Collectors.toList());

		this.insertMany(documents);
	}
}
