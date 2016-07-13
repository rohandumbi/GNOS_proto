package com.org.gnos.db.model;

import java.util.ArrayList;
import java.util.List;

public class Product {
	private String name;
	private Model associatedProcess;
	private List<Expression> listOfExpressions;
	private ArrayList<Grade> listOfGrades;
	
	public Product(String name, Model associatedProcess){
		this.associatedProcess = associatedProcess;
		this.name = name;
		this.listOfExpressions = new ArrayList<Expression>();
		this.listOfGrades = new ArrayList<Grade>();
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

	public void addExpression(Expression expression){
		this.listOfExpressions.add(expression);
	}

	public List<Grade> getListOfGrades() {
		return listOfGrades;
	}

	public void setListOfGrades(ArrayList<Grade> listOfGrades) {
		this.listOfGrades = listOfGrades;
	}
	
	public boolean addGrade(Grade grade){
		for(Grade g: this.listOfGrades){
			if(g.getName().equals(grade.getName())){
				return false;
			}
		}
		this.listOfGrades.add(grade);
		return true;
	}
}
