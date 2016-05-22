package edu.csula.datascience.acquisition.driver.worker;

import org.elasticsearch.action.bulk.*;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.node.Node;

import edu.csula.datascience.acquisition.driver.callable.SaveToElasticSearchCallable;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.QuandlRevenueDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.QuandlStockDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.TweetDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.YoutubeDataCollector;
import edu.csula.datascience.acquisition.model.database.QuandlRevenueModel;
import edu.csula.datascience.acquisition.model.database.QuandlStockModel;
import edu.csula.datascience.acquisition.model.database.TweetModel;
import edu.csula.datascience.acquisition.model.database.YoutubeModel;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

public class ExportDataToElasticSearch extends Thread {
	protected String dbHost;
	protected String name;
	protected String address;
	protected Node esNode;
	
	public ExportDataToElasticSearch(String dbHost, String address, String name) {
		this.dbHost = dbHost;
		this.name = name;
		this.address = address;
	}
	
	public void run() {
		Node node = nodeBuilder().settings(Settings.builder()
            .put("cluster.name", "realEric")
            .put("path.home", "elasticsearch-data")).node();
        Client client = node.client();
        
     // create bulk processor
        BulkProcessor bulkProcessor = BulkProcessor.builder(
            client,
            new BulkProcessor.Listener() {
                @Override
                public void beforeBulk(long executionId,
                                       BulkRequest request) {
                }

                @Override
                public void afterBulk(long executionId,
                                      BulkRequest request,
                                      BulkResponse response) {
                }

                @Override
                public void afterBulk(long executionId,
                                      BulkRequest request,
                                      Throwable failure) {
                    System.out.println("Facing error while importing data to elastic search");
                    failure.printStackTrace();
                }
            })
            .setBulkActions(10000)
            .setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB))
            .setFlushInterval(TimeValue.timeValueSeconds(5))
            .setConcurrentRequests(1)
            .setBackoffPolicy(
                BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
            .build();
		//Connect to database
        SaveToElasticSearchCallable callable = new SaveToElasticSearchCallable(bulkProcessor,this.name,"");

        QuandlStockDataCollector dbQuandlStock = new QuandlStockDataCollector(this.dbHost);
        callable.setTypeName("quandl-stocks");
        dbQuandlStock.fetchAll(callable, new QuandlStockModel());
        
        QuandlRevenueDataCollector dbQuandlRevenue = new QuandlRevenueDataCollector(this.dbHost);
        callable.setTypeName("quandl-revenues");
        dbQuandlRevenue.fetchAll(callable, new QuandlRevenueModel());
        
        TweetDataCollector dbTweet = new TweetDataCollector(this.dbHost);
        callable.setTypeName("tweets");
        dbTweet.fetchAll(callable, new TweetModel());
        
        YoutubeDataCollector dbYoutube = new YoutubeDataCollector(this.dbHost);
        callable.setTypeName("youtube-videos");
        dbYoutube.fetchAll(callable, new YoutubeModel());
		
		//Fetch all rows
		
		//
	}
}
