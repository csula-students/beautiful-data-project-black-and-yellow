package edu.csula.datascience.acquisition.driver.network.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.csula.datascience.acquisition.driver.BaseApiDriver;
import edu.csula.datascience.acquisition.model.YoutubeModel;

public class YoutubeApiDriver extends BaseApiDriver<YoutubeModel> {
	private static YoutubeApiDriver Instance = null;
	protected String apiServiceUrl;
	protected List<YoutubeModel> data;
	
	public static YoutubeApiDriver getInstance() {
		if(Instance == null) {
			Instance = new YoutubeApiDriver();
		}
		
		return Instance;
	}
	
	protected YoutubeApiDriver() {
		this.data = new ArrayList<>();
	}

	@Override
	public void queryService() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasNext() {
		return this.data.size() > 0;
	}

	@Override
	public Collection<YoutubeModel> next() {
		List<YoutubeModel> ret = new ArrayList<YoutubeModel>();
		for(int i = 0 ; i < batchSize && i < this.data.size(); i++) {
			ret.add(this.data.remove(0));
		}
		return ret;
	}

}
