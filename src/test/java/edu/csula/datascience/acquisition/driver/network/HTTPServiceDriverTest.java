package edu.csula.datascience.acquisition.driver.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;
import org.junit.*;

import edu.csula.datascience.acquisition.driver.BaseNetworkDriver;

public class HTTPServiceDriverTest {
	@Test 
	public void testInheritance() {
		HTTPServiceDriver Instance = new HTTPServiceDriver("http://google.com");		
		Assert.assertTrue(Instance instanceof BaseNetworkDriver);
		Assert.assertEquals("GET", BaseNetworkDriver.GET);
		Assert.assertEquals("POST", BaseNetworkDriver.POST);
	}
	
	public String shuffle(String input){
        List<Character> characters = new ArrayList<Character>();
        for(char c:input.toCharArray()){
            characters.add(c);
        }
        StringBuilder output = new StringBuilder(input.length());
        while(characters.size()!=0){
            int randPicker = (int)(Math.random()*characters.size());
            output.append(characters.remove(randPicker));
        }
        return output.toString();
    }
	
	@Test
	public void testEmptyGet() {
		HTTPServiceDriver Instance = null;
		String response = null;
		
		Instance = new HTTPServiceDriver("http://45.55.6.188/get.php");
		Instance.setMethodGet();
		
		try {
			Instance.connect();
			response = Instance.getContent().trim();
			Assert.assertNotNull(response);
			JSONObject json = new JSONObject(response);
			Assert.assertNotNull(json.getJSONObject("args"));			
			Assert.assertNull(json.getJSONObject("args").names());
		} catch (IOException e) {
			Assert.fail(e.getLocalizedMessage());
		}
		
	}
	
	@Test
	public void testEmptyPost() {
		HTTPServiceDriver Instance = null;
		String response = null;
		
		Instance = new HTTPServiceDriver("http://45.55.6.188/post.php");
		Instance.setMethodPost();
		
		try {
			Instance.connect();
			response = Instance.getContent().trim();
			Assert.assertNotNull(response);
			JSONObject json = new JSONObject(response);
			Assert.assertNotNull(json.getJSONObject("args"));			
			Assert.assertNull(json.getJSONObject("args").names());
		} catch (IOException e) {
			Assert.fail(e.getLocalizedMessage());
		}
		
	}
	
	@Test
	public void testGet() {
		HTTPServiceDriver Instance = null;
		String response = null;
		Random rand = new Random();
		
		Instance = new HTTPServiceDriver("http://45.55.6.188/get.php");
		Instance.setMethodGet();
		
		int limit = rand.nextInt() % 5;
		HashMap<String,String> data = new HashMap<>();
		for(int i = 0; i < limit; i++) {
			String key = shuffle("abcdef123");
			String value = shuffle("abcdefghijklmnop");
			data.put(key,value);			
		}
		for(String key : data.keySet()) {
			Instance.setRequestData(key, data.get(key));
		}
		
		try {
			Instance.connect();
			response = Instance.getContent().trim();
			Assert.assertNotNull(response);
			
			JSONObject json = new JSONObject(response);
			JSONObject args = json.getJSONObject("args");
			Assert.assertNotNull(args);			
			for(String key : data.keySet()) {
				Assert.assertEquals(data.get(key), args.getString(key));
			}
		} catch (IOException e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void testPost() {
		HTTPServiceDriver Instance = null;
		String response = null;
		
		Instance = new HTTPServiceDriver("http://45.55.6.188/post.php");
		Instance.setMethodPost();
		
		int limit = 3;
		HashMap<String,String> data = new HashMap<>();
		for(int i = 0; i < limit; i++) {
			String key = shuffle("abcdef123");
			String value = shuffle("abcdefghijklmnop");
			data.put(key,value);			
		}
		for(String key : data.keySet()) {
			Instance.setRequestData(key, data.get(key));
		}
		try {
			Instance.connect();
			response = Instance.getContent().trim();
			Assert.assertNotNull(response);
			
			JSONObject json = new JSONObject(response);
			JSONObject args = json.getJSONObject("args");
			Assert.assertNotNull(args);			
			for(String key : data.keySet()) {
				Assert.assertEquals(data.get(key), args.getString(key));
			}
		} catch (IOException e) {
			Assert.fail(e.getLocalizedMessage());
		}		
	}
}
