package edu.csula.datascience.acquisition.driver.database.mongo.ext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;

import edu.csula.datascience.acquisition.driver.BaseComparableCallable;
import edu.csula.datascience.acquisition.driver.database.mongo.BaseMongoDbDataCollector;
import edu.csula.datascience.acquisition.model.database.QuandlStockModel;

public class QuandlStockDataCollector extends BaseMongoDbDataCollector<QuandlStockModel,QuandlStockModel> {
	public QuandlStockDataCollector(String dbHost,String dbCollection) {
		super(dbHost,dbCollection);
	}
	public QuandlStockDataCollector(String dbHost) {
		this(dbHost,"quandl_stocks");
	}
	
	@Override
	public Collection<QuandlStockModel> mungee(Collection<QuandlStockModel> src) {
		List<QuandlStockModel> ret = new ArrayList<>();
		ret.addAll(src);
		return ret;
	}

	@Override
	public void save(Collection<QuandlStockModel> data) {
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
	
	public void findAll(BasicDBObject queryModel,QuandlStockModel model, BaseComparableCallable<QuandlStockModel> callback) {
		System.out.println("Query: " + queryModel.toString());
    	FindIterable<Document> results = collection.find(queryModel);
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
	
	public void findAll(BasicDBObject queryModel,BasicDBObject sortModel,QuandlStockModel model, BaseComparableCallable<QuandlStockModel> callback) {
		System.out.println("Query: " + queryModel.toString());
    	FindIterable<Document> results = collection.find(queryModel).sort(sortModel);
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
	
	public void fetchAll(BaseComparableCallable<QuandlStockModel> callback,QuandlStockModel model) {
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
