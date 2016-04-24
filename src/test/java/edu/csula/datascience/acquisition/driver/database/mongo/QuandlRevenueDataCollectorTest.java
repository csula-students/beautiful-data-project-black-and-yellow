package edu.csula.datascience.acquisition.driver.database.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.*;

import edu.csula.datascience.acquisition.Collector;
import edu.csula.datascience.acquisition.driver.database.BaseDataCollector;
import edu.csula.datascience.acquisition.model.QuandlRevenueModel;

public class QuandlRevenueDataCollectorTest {	
	@Test
	public void testMungee() {
		QuandlRevenueDataCollector<QuandlRevenueModel, QuandlRevenueModel> dbDriver = new QuandlRevenueDataCollector<>("localhost","test");
		
		Assert.assertTrue(dbDriver instanceof BaseMongoDbDataCollector);
		Assert.assertTrue(dbDriver instanceof BaseDataCollector);
		Assert.assertTrue(dbDriver instanceof Collector);
		
		//Empty List
		Collection<QuandlRevenueModel> list1 = new ArrayList<>();
		Collection<QuandlRevenueModel> newList1 = dbDriver.mungee(list1);		
		Assert.assertEquals(0, newList1.size());
		
		//Single Item List
		Collection<QuandlRevenueModel> list2 = new ArrayList<>();
		list2.add(new QuandlRevenueModel());
		Collection<QuandlRevenueModel> newList2 = dbDriver.mungee(list2);	
		Assert.assertEquals(1, newList2.size());
		Assert.assertTrue(newList2.iterator().next() instanceof QuandlRevenueModel);
		
		//Multi Item List
		Collection<QuandlRevenueModel> list3 = new ArrayList<>();
		list3.add(new QuandlRevenueModel());
		list3.add(new QuandlRevenueModel());
		list3.add(new QuandlRevenueModel());
		Collection<QuandlRevenueModel> newList3 = dbDriver.mungee(list3);	
		Assert.assertEquals(3, newList3.size());
		
		List<QuandlRevenueModel> list4 = new ArrayList<>();
		for(int i = 0; i < 3; i++) {
			QuandlRevenueModel model = new QuandlRevenueModel();
			model.name = "item " + 1;
			list4.add(model);
		}
		List<QuandlRevenueModel> newList4 = (List<QuandlRevenueModel>)dbDriver.mungee(list4);	
		Assert.assertEquals(3, newList4.size());
		for(int i = 0; i < 3; i++) {
			Assert.assertEquals(list4.get(i).name, newList4.get(i).name);
		}
	}
}
