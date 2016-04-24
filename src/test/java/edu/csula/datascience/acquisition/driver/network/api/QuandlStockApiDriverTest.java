package edu.csula.datascience.acquisition.driver.network.api;

import java.util.Collection;

import org.junit.*;

import edu.csula.datascience.acquisition.Source;
import edu.csula.datascience.acquisition.driver.BaseApiDriver;
import edu.csula.datascience.acquisition.driver.BaseApiDriverTest;
import edu.csula.datascience.acquisition.model.Company;
import edu.csula.datascience.acquisition.model.QuandlStockModel;

public class QuandlStockApiDriverTest extends BaseApiDriverTest {
	QuandlStockApiDriver Instance;
	@Before
	public void setup() {
		this.loadConfig();
		Instance = QuandlStockApiDriver.getInstance();
		Instance.setConfigData(apiConfigs.get(QUANDL));
	}
	
	@Test
	public void testInheritence() {
		Assert.assertTrue(Instance instanceof BaseApiDriver);
		Assert.assertTrue(Instance instanceof Source);
		Assert.assertEquals(Instance.getConfigData(), apiConfigs.get(QUANDL));
	}
	
	@Test
	public void testHasData() {		
		Instance.queryService();
		Assert.assertFalse(Instance.hasNext());
		
		for(Company company : companies) {
			for(String name : company.stock) {
				Instance.addCompanyStock(name);
			}			
		}
		
		Instance.queryService();
		
		Assert.assertTrue(Instance.hasNext());
		Collection<QuandlStockModel> list = Instance.next();
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
	}
}
