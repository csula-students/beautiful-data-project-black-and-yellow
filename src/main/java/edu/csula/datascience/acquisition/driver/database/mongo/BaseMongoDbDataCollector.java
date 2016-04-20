package edu.csula.datascience.acquisition.driver.database.mongo;

import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import edu.csula.datascience.acquisition.driver.database.BaseDataCollector;

public abstract class BaseMongoDbDataCollector<T,A> extends BaseDataCollector<T,A> {
	private MongoClient mongoClient;
	private MongoDatabase database;
	private MongoCollection<Document> collection;
	private static String dbName = "homework2";
    
    public BaseMongoDbDataCollector() {
    	// establish database connection to MongoDB
        mongoClient = new MongoClient();
        database = mongoClient.getDatabase(dbName);
        collection = database.getCollection(this.getCollectionName());
    }
    
    protected void insertMany(List<Document> documents) {
    	collection.insertMany(documents);
    }
    
    protected void insertOne(Document document) {
    	collection.insertOne(document);
    }
    
    abstract protected String getCollectionName();
}
