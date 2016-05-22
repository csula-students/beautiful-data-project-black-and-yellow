package edu.csula.datascience.acquisition.driver.callable;

import edu.csula.datascience.acquisition.driver.BaseComparableCallable;
import edu.csula.datascience.acquisition.driver.database.BaseDatabaseModel;
import edu.csula.datascience.interfaces.PercentageDifference;

public class CompareRowsCallable<T extends BaseDatabaseModel<T> & PercentageDifference<T>> extends BaseComparableCallable<T> {
	T prevRow = null;
	double upperLimit = 0;
	double lowerLimit = 0;
	BaseComparableCallable<T> callback;
	
	public CompareRowsCallable(double upperLimit, double lowerLimit, BaseComparableCallable<T> callback) {
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
		if(prevRow != null) {
			if(prevRow.difference(row) >= upperLimit) {
				callback.call(row);
			}
			
			if(prevRow.difference(row) <= lowerLimit) {
				callback.call(row);
			}			
		}
		
		prevRow = row;
	}

}
