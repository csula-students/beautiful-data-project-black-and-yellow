package edu.csula.datascience.acquisition.model;

import java.util.List;

public class Company {
	public String name;
	public List<String> alias;
	public List<String> stock;
	
	public Company(String name, List<String> alias, List<String> stock) {
		this.name = name;
		this.alias = alias;
		this.stock = stock;
	}
}
