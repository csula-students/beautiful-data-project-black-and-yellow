package edu.csula.datascience.acquisition.driver.callable;

import edu.csula.datascience.acquisition.driver.BaseComparableCallable;
import edu.csula.datascience.acquisition.driver.database.BaseDatabaseModel;
import edu.csula.datascience.interfaces.PercentageDifference;

public class CompareColumnsCallable<T extends BaseDatabaseModel<T> & PercentageDifference<T>> extends BaseComparableCallable<T> {
	double upperLimit = 0;
	double lowerLimit = 0;
	long count = 0;
	BaseComparableCallable<T> callback;
	
	public CompareColumnsCallable(double upperLimit, double lowerLimit, BaseComparableCallable<T> callback) {
		this.upperLimit = upperLimit;
		this.lowerLimit = lowerLimit;
		this.callback = callback;
	}
	@Override
	public Boolean call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public void call(T row) throws Exception {
		count++;
		System.out.println("Iteration " + count);
		if(row.difference() >= upperLimit) {
			System.out.println("Hit upper boundary: " +row.difference());
			callback.call(row);
		}
		
		if(row.difference() <= lowerLimit) {
			System.out.println("Hit lower boundary: " +row.difference());
			callback.call(row);
		}
	}
}
