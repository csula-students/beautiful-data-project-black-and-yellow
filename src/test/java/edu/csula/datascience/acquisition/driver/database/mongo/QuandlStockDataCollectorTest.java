package edu.csula.datascience.acquisition.driver.database.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.*;

import edu.csula.datascience.acquisition.Collector;
import edu.csula.datascience.acquisition.driver.database.BaseDataCollector;
import edu.csula.datascience.acquisition.driver.database.mongo.ext.QuandlStockDataCollector;
import edu.csula.datascience.acquisition.model.database.QuandlStockModel;

public class QuandlStockDataCollectorTest {	
	@Test
	public void testMungee() {
		QuandlStockDataCollector dbDriver = new QuandlStockDataCollector("localhost","test");
		
		Assert.assertTrue(dbDriver instanceof BaseMongoDbDataCollector);
		Assert.assertTrue(dbDriver instanceof BaseDataCollector);
		Assert.assertTrue(dbDriver instanceof Collector);
		
		//Empty List
		Collection<QuandlStockModel> list1 = new ArrayList<>();
		Collection<QuandlStockModel> newList1 = dbDriver.mungee(list1);		
		Assert.assertEquals(0, newList1.size());
		
		//Single Item List
		Collection<QuandlStockModel> list2 = new ArrayList<>();
		list2.add(new QuandlStockModel());
		Collection<QuandlStockModel> newList2 = dbDriver.mungee(list2);	
		Assert.assertEquals(1, newList2.size());
		Assert.assertTrue(newList2.iterator().next() instanceof QuandlStockModel);
		
		//Multi Item List
		Collection<QuandlStockModel> list3 = new ArrayList<>();
		list3.add(new QuandlStockModel());
		list3.add(new QuandlStockModel());
		list3.add(new QuandlStockModel());
		Collection<QuandlStockModel> newList3 = dbDriver.mungee(list3);	
		Assert.assertEquals(3, newList3.size());
		
		List<QuandlStockModel> list4 = new ArrayList<>();
		for(int i = 0; i < 3; i++) {
			QuandlStockModel model = new QuandlStockModel();
			model.name = "item " + 1;
			list4.add(model);
		}
		List<QuandlStockModel> newList4 = (List<QuandlStockModel>)dbDriver.mungee(list4);	
		Assert.assertEquals(3, newList4.size());
		for(int i = 0; i < 3; i++) {
			Assert.assertEquals(list4.get(i).name, newList4.get(i).name);
		}
	}
}
