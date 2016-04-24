package edu.csula.datascience.acquisition.driver.database.mongo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.junit.*;

import edu.csula.datascience.acquisition.Collector;
import edu.csula.datascience.acquisition.driver.database.BaseDataCollector;
import edu.csula.datascience.acquisition.model.TweetModel;

public class TwitterDataCollectorTest {	
	@Test
	public void testMungee() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
		TweetDataCollector<TweetModel, TweetModel> dbDriver = new TweetDataCollector<>("localhost","test");
		
		Assert.assertTrue(dbDriver instanceof BaseMongoDbDataCollector);
		Assert.assertTrue(dbDriver instanceof BaseDataCollector);
		Assert.assertTrue(dbDriver instanceof Collector);
		
		//Empty List
		Collection<TweetModel> list1 = new ArrayList<>();
		Collection<TweetModel> newList1 = dbDriver.mungee(list1);		
		Assert.assertEquals(0, newList1.size());
		
		//Single Item List
		Collection<TweetModel> list2 = new ArrayList<>();
		for(int i = 0; i < 1; i++) {
			TweetModel model = new TweetModel();
			model.id = i;
			model.created_at = dateFormat.format(new Date());
			list2.add(model);
		}
		Collection<TweetModel> newList2 = dbDriver.mungee(list2);	
		Assert.assertEquals(1, newList2.size());
		Assert.assertTrue(newList2.iterator().next() instanceof TweetModel);
		
		//Multi Item List Accepts
		List<TweetModel> list4 = new ArrayList<>();
		for(int i = 0; i < 3; i++) {
			TweetModel model = new TweetModel();
			model.id = i;
			model.created_at = dateFormat.format(new Date());
			list4.add(model);
		}
		List<TweetModel> newList4 = (List<TweetModel>)dbDriver.mungee(list4);	
		Assert.assertEquals(3, newList4.size());
		for(int i = 0; i < 3; i++) {
			Assert.assertEquals(list4.get(i).id, newList4.get(i).id);
		}
		
		//Multi Item List Rejects
		List<TweetModel> list5 = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -30);
		for(int i = 0; i < 3; i++) {
			TweetModel model = new TweetModel();
			model.id = i;
			model.created_at = dateFormat.format(calendar.getTime());
			list5.add(model);
		}
		Collection<TweetModel> newList5 = dbDriver.mungee(list5);	
		Assert.assertEquals(0, newList5.size());
	}
}
