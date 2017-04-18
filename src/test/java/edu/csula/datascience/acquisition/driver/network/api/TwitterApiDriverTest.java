package edu.csula.datascience.acquisition.driver.network.api;

import java.util.Collection;
import org.junit.*;
import edu.csula.datascience.acquisition.Source;
import edu.csula.datascience.acquisition.driver.BaseApiDriver;
import edu.csula.datascience.acquisition.driver.BaseApiDriverTest;
import edu.csula.datascience.acquisition.model.Company;
import edu.csula.datascience.acquisition.model.TweetModel;

public class TwitterApiDriverTest extends BaseApiDriverTest {
	TwitterApiDriver Instance;
	@Before
	public void setup() {
		this.loadConfig();
		Instance = TwitterApiDriver.getInstance();
		Instance.setConfigData(apiConfigs.get(TWITTER));
	}
	
	@Test
	public void testInheritence() {
		Assert.assertTrue(Instance instanceof BaseApiDriver);
		Assert.assertTrue(Instance instanceof Source);
		Assert.assertEquals(Instance.getConfigData(), apiConfigs.get(TWITTER));
	}
	
	@Test
	public void testHasData() {
		Assert.assertTrue(Instance.authenticate());
		
		Instance.queryService();
		Assert.assertFalse(Instance.hasNext());
		
		for(Company company : companies) {
			Instance.addCompany(company);
		}
		
		Instance.queryService();
		
		Assert.assertTrue(Instance.hasNext());
		Collection<TweetModel> list = Instance.next();
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
	}
}
