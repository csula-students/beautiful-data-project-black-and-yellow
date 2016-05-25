package edu.csula.datascience.interfaces;

public interface PercentageDifference<T> extends Comparable<T> {
	public double difference(T o);
	public double difference();
}
