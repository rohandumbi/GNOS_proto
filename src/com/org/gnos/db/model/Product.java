package com.org.gnos.db.model;

import java.util.List;

public class Product {
	private String name;
	private Model associatedProcess;
	private List<Expression> listOfExpressions;
	
	public Product(String name, Model associatedProcess){
		this.associatedProcess = associatedProcess;
		this.name = name;
	}

	public Model getAssociatedProcess() {
		return associatedProcess;
	}

	public List<Expression> getListOfExpressions() {
		return listOfExpressions;
	}

	public void setListOfExpressions(List<Expression> listOfExpressions) {
		this.listOfExpressions = listOfExpressions;
	}

	public String getName() {
		return name;
	}

	
	
}
