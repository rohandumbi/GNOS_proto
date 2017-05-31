package com.org.gnos.scheduler.equation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Constraint {

	public static final short GREATER = 1;
	public static final short LESS = 2;
	public static final short GREATER_EQUAL = 3;
	public static final short LESS_EQUAL = 4;
	public static final short EQUAL = 5;
	
	private short type;
	private List<String> variables;
	private List<BigDecimal> coefficients;
	private BigDecimal value;
	
	public Constraint() {
		variables = new ArrayList<String>();
		coefficients = new ArrayList<BigDecimal>();
	}
	public short getType() {
		return type;
	}
	public void setType(short type) {
		this.type = type;
	}
	public List<String> getVariables() {
		return variables;
	}
	public void setVariables(List<String> variables) {
		this.variables = variables;
	}
	public List<BigDecimal> getCoefficients() {
		return coefficients;
	}
	public void setCoefficients(List<BigDecimal> coefficients) {
		this.coefficients = coefficients;
	}
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	
	public void addVariable(String variable, BigDecimal coeff) {
		variables.add(variable);
		coefficients.add(coeff);
	}

}
