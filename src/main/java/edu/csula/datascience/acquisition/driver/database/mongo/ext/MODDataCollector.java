package edu.csula.datascience.acquisition.driver.database.mongo.ext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;

import edu.csula.datascience.acquisition.driver.database.mongo.BaseMongoDbDataCollector;
import edu.csula.datascience.acquisition.model.database.MarkitOnDemandModel;

public class MODDataCollector extends BaseMongoDbDataCollector<MarkitOnDemandModel,MarkitOnDemandModel>  {
	public MODDataCollector(String dbHost, String dbCollection) {
		super(dbHost,dbCollection);
	}
	public MODDataCollector(String dbHost) {
		this(dbHost,"stocks");
	}
	
	@Override
	public Collection<MarkitOnDemandModel> mungee(Collection<MarkitOnDemandModel> src) {
		List<MarkitOnDemandModel> ret = new ArrayList<>();
		ret.addAll(src);
		return ret;
	}

	@Override
	public void save(Collection<MarkitOnDemandModel> data) {
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
