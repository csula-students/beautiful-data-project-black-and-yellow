package edu.csula.datascience.acquisition.driver;

import java.util.concurrent.Callable;

import edu.csula.datascience.interfaces.PercentageDifference;

public abstract class BaseComparableCallable<T extends PercentageDifference<T>> implements Callable<Boolean> {
	abstract public void call(T row) throws Exception;
}
