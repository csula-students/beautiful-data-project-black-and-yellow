package edu.csula.datascience.acquisition.driver.network.api;

import java.util.Collection;

import org.junit.*;

import edu.csula.datascience.acquisition.Source;
import edu.csula.datascience.acquisition.driver.BaseApiDriver;
import edu.csula.datascience.acquisition.driver.BaseApiDriverTest;
import edu.csula.datascience.acquisition.model.Company;
import edu.csula.datascience.acquisition.model.database.YoutubeModel;

public class YoutubeApiDriverTest extends BaseApiDriverTest {
	YoutubeApiDriver Instance;
	@Before
	public void setup() {
		this.loadConfig();
		Instance = YoutubeApiDriver.getInstance();
		Instance.setConfigData(apiConfigs.get(GOOGLE));
	}
	
	@Test
	public void testInheritence() {
		Assert.assertTrue(Instance instanceof BaseApiDriver);
		Assert.assertTrue(Instance instanceof Source);
		Assert.assertEquals(Instance.getConfigData(), apiConfigs.get(GOOGLE));
	}
	
	@Test
	public void testHasData() {		
		Instance.queryService();
		Assert.assertFalse(Instance.hasNext());
		
		for(Company company : companies) {
			Instance.addCompanyStock(company);
		}
		
		Instance.queryService();
		
		Assert.assertTrue(Instance.hasNext());
		Collection<YoutubeModel> list = Instance.next();
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
	}
}
