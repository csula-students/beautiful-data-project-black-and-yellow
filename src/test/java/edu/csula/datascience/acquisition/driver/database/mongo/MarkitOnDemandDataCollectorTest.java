package edu.csula.datascience.acquisition.driver.database.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.*;

import edu.csula.datascience.acquisition.Collector;
import edu.csula.datascience.acquisition.driver.database.BaseDataCollector;
import edu.csula.datascience.acquisition.model.MarkitOnDemandModel;

public class MarkitOnDemandDataCollectorTest {	
	@Test
	public void testMungee() {
		MODDataCollector<MarkitOnDemandModel, MarkitOnDemandModel> dbDriver = new MODDataCollector<>("localhost","test");
		
		Assert.assertTrue(dbDriver instanceof BaseMongoDbDataCollector);
		Assert.assertTrue(dbDriver instanceof BaseDataCollector);
		Assert.assertTrue(dbDriver instanceof Collector);
		
		//Empty List
		Collection<MarkitOnDemandModel> list1 = new ArrayList<>();
		Collection<MarkitOnDemandModel> newList1 = dbDriver.mungee(list1);		
		Assert.assertEquals(0, newList1.size());
		
		//Single Item List
		Collection<MarkitOnDemandModel> list2 = new ArrayList<>();
		list2.add(new MarkitOnDemandModel());
		Collection<MarkitOnDemandModel> newList2 = dbDriver.mungee(list2);	
		Assert.assertEquals(1, newList2.size());
		Assert.assertTrue(newList2.iterator().next() instanceof MarkitOnDemandModel);
		
		//Multi Item List
		Collection<MarkitOnDemandModel> list3 = new ArrayList<>();
		list3.add(new MarkitOnDemandModel());
		list3.add(new MarkitOnDemandModel());
		list3.add(new MarkitOnDemandModel());
		Collection<MarkitOnDemandModel> newList3 = dbDriver.mungee(list3);	
		Assert.assertEquals(3, newList3.size());
		
		List<MarkitOnDemandModel> list4 = new ArrayList<>();
		for(int i = 0; i < 3; i++) {
			MarkitOnDemandModel model = new MarkitOnDemandModel();
			model.name = "item " + (i+1);
			list4.add(model);
		}
		List<MarkitOnDemandModel> newList4 = (List<MarkitOnDemandModel>)dbDriver.mungee(list4);	
		Assert.assertEquals(3, newList4.size());
		for(int i = 0; i < 3; i++) {
			Assert.assertEquals(list4.get(i).name, newList4.get(i).name);
		}
	}
}
