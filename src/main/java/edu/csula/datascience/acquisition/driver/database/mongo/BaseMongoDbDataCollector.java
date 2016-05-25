package edu.csula.datascience.acquisition.driver.database.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import edu.csula.datascience.acquisition.driver.database.BaseDataCollector;
import edu.csula.datascience.acquisition.driver.database.BaseDatabaseModel;
import edu.csula.datascience.acquisition.model.query.MongoQueryModel;

public abstract class BaseMongoDbDataCollector<T extends BaseDatabaseModel<T> ,A extends T> extends BaseDataCollector<T,A,MongoQueryModel> {
	protected MongoClient mongoClient;
	protected MongoDatabase database;
	protected MongoCollection<Document> collection;
	protected static String dbName = "datascience";
	protected String collectionName;
    
	public BaseMongoDbDataCollector(String dbHost, String collectionName) {
		// establish database connection to MongoDB
		Builder options = MongoClientOptions.builder();
        mongoClient = new MongoClient(new ServerAddress(dbHost),options.build());
        database = mongoClient.getDatabase(dbName);
        this.collectionName = collectionName;
        this.collection = this.database.getCollection(collectionName);
	}
    
    protected void insertMany(List<Document> documents) {
    	if(documents.size() == 0) {
    		return;
    	}
    	System.out.println("Saving many documents to " + this.collectionName);
    	collection.insertMany(documents);
    }
    
    protected void insertOne(Document document) {
    	System.out.println("Saving one document to " + this.collectionName);
    	collection.insertOne(document);
    }
    
    public final void close() {
    	mongoClient.close();
    }
    
    public void dropCollection() {
    	collection.drop();
    }
    
	public T find(MongoQueryModel queryModel,T model) {
    	FindIterable<Document> results = collection.find(queryModel.toBson());
    	for(Document row : results) {
    		model.parseJSONObject(new JSONObject(row.toJson()));
    		return model;
    	}
    	return null;
    }
    
	public List<T> findAll(MongoQueryModel queryModel,T model) {
    	List<T> list = new ArrayList<T>();
    	FindIterable<Document> results = collection.find(queryModel.toBson());
    	for(Document row : results) {
    		model.parseJSONObject(new JSONObject(row.toJson()));
    		list.add((T)model.clone());
    	}
    	return null;
    }
    
    public void fetchAll(Callable<?> callback,T model) {
    	FindIterable<Document> results = collection.find();
    	results.noCursorTimeout(true);
    	MongoCursor<Document> itr = results.iterator();
    	while(itr.hasNext()) {
    		Document row = itr.next();
    		model.parseJSONObject(new JSONObject(row.toJson()));
    		try {
				callback.call();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
    	}
    }
}
