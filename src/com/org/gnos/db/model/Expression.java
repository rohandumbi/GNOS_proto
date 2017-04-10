package com.org.gnos.db.model;


public class Expression {
	
	public static final short UNIT_NONE = 0;
	public static final short UNIT_FIELD = 1;
	public static final short UNIT_EXPRESSION = 2;
	
	private int id;
	private String name;
	private boolean isGrade;
	private boolean isComplex;
	private String exprvalue;
	private String filter;
	private String weightedField;
	private short weightedFieldType;
	
	public Expression() {
		super();
		this.id = -1;
	}
	
	public Expression(String name) {
		super();
		this.id = -1;
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

	public boolean isGrade() {
		return isGrade;
	}

	public void setGrade(boolean isGrade) {
		this.isGrade = isGrade;
	}

	public boolean isComplex() {
		return isComplex;
	}

	public void setComplex(boolean isComplex) {
		this.isComplex = isComplex;
	}

	public String getExprvalue() {
		return exprvalue;
	}

	public void setExprvalue(String exprvalue) {
		this.exprvalue = exprvalue;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getWeightedField() {
		return weightedField;
	}

	public void setWeightedField(String weightedField) {
		this.weightedField = weightedField;
	}

	public short getWeightedFieldType() {
		return weightedFieldType;
	}

	public void setWeightedFieldType(short weightedFieldType) {
		this.weightedFieldType = weightedFieldType;
	}

	@Override
	public String toString() {
		return "Expression [id=" + id + ", name=" + name + ", isGrade="
				+ isGrade + ", isComplex=" + isComplex + ", exprvalue="
				+ exprvalue + ", filter=" + filter + "]";
	}	

}
