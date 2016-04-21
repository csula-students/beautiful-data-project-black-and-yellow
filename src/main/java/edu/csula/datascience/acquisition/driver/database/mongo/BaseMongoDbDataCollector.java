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
	private String collectionName;
    
	public BaseMongoDbDataCollector(String dbHost, String collectionName) {
		// establish database connection to MongoDB
        mongoClient = new MongoClient(dbHost);
        database = mongoClient.getDatabase(dbName);
        collection = database.getCollection(collectionName);
        this.collectionName = collectionName;
	}
    
    protected void insertMany(List<Document> documents) {
    	System.out.println("Saving many documents to " + this.collectionName);
    	collection.insertMany(documents);
    }
    
    protected void insertOne(Document document) {
    	System.out.println("Saving one document to " + this.collectionName);
    	collection.insertOne(document);
    }
}
