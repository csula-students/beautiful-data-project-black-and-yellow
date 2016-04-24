package edu.csula.datascience.acquisition.driver.network.api;

import java.util.Collection;

import org.junit.*;

import edu.csula.datascience.acquisition.Source;
import edu.csula.datascience.acquisition.driver.BaseApiDriver;
import edu.csula.datascience.acquisition.driver.BaseApiDriverTest;
import edu.csula.datascience.acquisition.model.MarkitOnDemandModel;

public class MarkitOnDemandApiDriverTest extends BaseApiDriverTest {
	MarkitOnDemandApiDriver Instance;
	@Before
	public void setup() {
		this.loadConfig();
		Instance = MarkitOnDemandApiDriver.getInstance();
		Instance.setConfigData(apiConfigs.get(MOD));
	}
	
	@Test
	public void testInheritence() {
		Assert.assertTrue(Instance instanceof BaseApiDriver);
		Assert.assertTrue(Instance instanceof Source);
		Assert.assertEquals(Instance.getConfigData(), apiConfigs.get(MOD));
	}
	
	@Test
	public void testHasData() {
		Instance.queryService();
		Assert.assertFalse(Instance.hasNext());		
		
		Instance.addCompanyStock("AAPL");
		Instance.queryService();
		Assert.assertTrue(Instance.hasNext());
		
		Collection<MarkitOnDemandModel> list = Instance.next();
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == 1);
		
		for(MarkitOnDemandModel model : list) {
			Assert.assertEquals("AAPL", model.symbol);
		}
		
		Assert.assertFalse(Instance.hasNext());
	}
}
