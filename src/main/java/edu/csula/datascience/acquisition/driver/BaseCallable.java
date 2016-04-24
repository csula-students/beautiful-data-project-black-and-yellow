package edu.csula.datascience.acquisition.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class BaseCallable implements Callable<Boolean> {
	private List<String> errors;
	protected BaseCallable() {
		this.errors = new ArrayList<>();
	}
	protected final void logError(String message) {
		this.errors.add(message);
	}
	
	public List<String> getErrors() {
		return this.errors;
	}
	
	public abstract Boolean call(JSONObject data);
	public abstract Boolean call(JSONArray list);
}
