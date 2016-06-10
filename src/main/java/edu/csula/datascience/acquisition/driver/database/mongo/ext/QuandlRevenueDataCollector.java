package edu.csula.datascience.acquisition.driver.database.mongo.ext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;

import edu.csula.datascience.acquisition.driver.BaseComparableCallable;
import edu.csula.datascience.acquisition.driver.database.mongo.BaseMongoDbDataCollector;
import edu.csula.datascience.acquisition.model.database.QuandlRevenueModel;

public class QuandlRevenueDataCollector extends BaseMongoDbDataCollector<QuandlRevenueModel,QuandlRevenueModel> {
	public QuandlRevenueDataCollector(String dbHost,String dbCollection) {
		super(dbHost,dbCollection);
	}
	public QuandlRevenueDataCollector(String dbHost) {
		this(dbHost,"quandl_revenue");
	}
	
	@Override
	public Collection<QuandlRevenueModel> mungee(Collection<QuandlRevenueModel> src) {
		List<QuandlRevenueModel> ret = new ArrayList<>();
		ret.addAll(src);
		return ret;
	}

	@Override
	public void save(Collection<QuandlRevenueModel> data) {
		List<Document> documents = data.stream()
			.map(item -> new Document()
					.append("name", item.name)
					.append("date", item.date)
					.append("value", item.value)
				)
	            .collect(Collectors.toList());

		this.insertMany(documents);
	}
	
	public void fetchAll(BaseComparableCallable<QuandlRevenueModel> callback,QuandlRevenueModel model) {
		FindIterable<Document> results = collection.find();
    	results.noCursorTimeout(true);
    	MongoCursor<Document> itr = results.iterator();
    	while(itr.hasNext()) {
    		Document row = itr.next();
    		model.parseJSONObject(new JSONObject(row.toJson()));
    		try {
				callback.call(model);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
    	}
    }
}
