package edu.csula.datascience.acquisition.driver.callable;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.csula.datascience.acquisition.driver.BaseCallable;

public class SaveToElasticSearchCallable extends BaseCallable {
	protected BulkProcessor bulkProcessor;
	protected String indexName;
	protected String typeName;
	
	public SaveToElasticSearchCallable(BulkProcessor bulkProcessor, String indexName, String typeName) {
		this.bulkProcessor = bulkProcessor;
		this.indexName = indexName;
		this.typeName = typeName;
	}	

	@Override
	public Boolean call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean call(JSONObject data) {
		// TODO Auto-generated method stub
		bulkProcessor.add(new IndexRequest(indexName, typeName)
			.source(data)
		);
		return true;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	@Override
	public Boolean call(JSONArray list) {
		for(int i = 0 ;i < list.length(); i++) {
			bulkProcessor.add(new IndexRequest(indexName, typeName)
				.source(list.getJSONObject(i))
			);
		}
		return true;
	}

}
