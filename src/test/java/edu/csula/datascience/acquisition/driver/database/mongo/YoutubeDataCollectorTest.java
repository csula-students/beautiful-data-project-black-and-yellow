package edu.csula.datascience.acquisition.driver.database.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.*;

import edu.csula.datascience.acquisition.Collector;
import edu.csula.datascience.acquisition.driver.database.BaseDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.YoutubeDataCollector;
import edu.csula.datascience.acquisition.model.database.YoutubeModel;

public class YoutubeDataCollectorTest {	
	@Test
	public void testMungee() {
		YoutubeDataCollector dbDriver = new YoutubeDataCollector("localhost","test");
		
		Assert.assertTrue(dbDriver instanceof BaseMongoDbDataCollector);
		Assert.assertTrue(dbDriver instanceof BaseDataCollector);
		Assert.assertTrue(dbDriver instanceof Collector);
		
		//Empty List
		Collection<YoutubeModel> list1 = new ArrayList<>();
		Collection<YoutubeModel> newList1 = dbDriver.mungee(list1);		
		Assert.assertEquals(0, newList1.size());
		
		//Single Item List
		Collection<YoutubeModel> list2 = new ArrayList<>();
		list2.add(new YoutubeModel());
		Collection<YoutubeModel> newList2 = dbDriver.mungee(list2);	
		Assert.assertEquals(1, newList2.size());
		Assert.assertTrue(newList2.iterator().next() instanceof YoutubeModel);
		
		//Multi Item List
		Collection<YoutubeModel> list3 = new ArrayList<>();
		list3.add(new YoutubeModel());
		list3.add(new YoutubeModel());
		list3.add(new YoutubeModel());
		Collection<YoutubeModel> newList3 = dbDriver.mungee(list3);	
		Assert.assertEquals(3, newList3.size());
		
		List<YoutubeModel> list4 = new ArrayList<>();
		for(int i = 0; i < 3; i++) {
			YoutubeModel model = new YoutubeModel();
			model.channel_id = "item " + 1;
			list4.add(model);
		}
		List<YoutubeModel> newList4 = (List<YoutubeModel>)dbDriver.mungee(list4);	
		Assert.assertEquals(3, newList4.size());
		for(int i = 0; i < 3; i++) {
			Assert.assertEquals(list4.get(i).channel_id, newList4.get(i).channel_id);
		}
	}
}
