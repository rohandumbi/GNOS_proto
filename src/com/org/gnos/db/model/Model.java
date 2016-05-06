package com.org.gnos.db.model;

public class Model {
	private int id;
	private String name;
	private Expression expression;
	private String condition;
	
	
	public Model(String name) {
		super();
		this.id = -1;
		this.name = name;
	}
	
	public Model(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Expression getExpression() {
		return expression;
	}
	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	
}
