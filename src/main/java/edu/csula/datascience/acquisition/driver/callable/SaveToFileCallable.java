package edu.csula.datascience.acquisition.driver.callable;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.csula.datascience.acquisition.driver.BaseCallable;

public class SaveToFileCallable extends BaseCallable {
	protected String filename;
	protected String seperator;
	protected FileWriter writer = null;
	
	public SaveToFileCallable(String filename,String seperator) {
		this.filename = filename;
		this.seperator = seperator;
	}
	
	public SaveToFileCallable(String filename) {
		this(filename,"|");
	}
	
	public void setSeperator(String seperator) {
		this.seperator = seperator;
	}
	
	public void open() throws IOException {
		writer = new FileWriter(this.filename);
	}
	
	public void close() throws IOException {
		writer.close();
	}
	
	public void write(String line) throws IOException {
		writer.write(line);
	}

	@Override
	public Boolean call(JSONObject data) {
		boolean ret = true;
		
		if(data.keySet().size() > 0) {
			try {
				Iterator<String> keyItr = data.keySet().iterator();
				String line = "";
				while(keyItr.hasNext()) {
					String key = keyItr.next();
					line += this.seperator + data.getString(key);
				}
				line = line.substring(this.seperator.length());
				writer.write(line + System.lineSeparator());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				this.logError(e.getLocalizedMessage());
				ret = false;
			}			
		}
		
		return ret;
	}

	@Override
	public Boolean call(JSONArray list) {
		boolean ret = true;
		
		if(list.length() > 0) {
			try {
				for(int i = 0; i < list.length(); i++) {
					JSONObject object = list.getJSONObject(i);
					Iterator<String> keyItr = object.keySet().iterator();
					String line = "";
					while(keyItr.hasNext()) {
						String key = keyItr.next();
						line += this.seperator + object.getString(key);
					}
					line = line.substring(this.seperator.length());
					writer.write(line + System.lineSeparator());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				this.logError(e.getLocalizedMessage());
				ret = false;
			}			
		}
		
		return ret;
	}

	@Override
	public Boolean call() throws Exception {
		return false;
	}

}
