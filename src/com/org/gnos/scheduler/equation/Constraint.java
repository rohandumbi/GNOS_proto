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
	
	public final static short OBJECTIVE_FUNCTION = 1;
	public final static short PROCESS_CONSTRAINT = 2;
	public final static short GRADE_CONSTRAINT = 3;
	public final static short BINARY_VARIABLE = 4;
	public final static short BENCH_CONSTRAINT = 5;
	public final static short BENCH_PROPORTIONS = 6;
	public final static short BOUNDARY_VARIABLE = 7;
	public final static short PIT_DEPENDENCY = 8;
	public final static short CAPEX = 9;
	public final static short DUMP_DEPENDENCY = 10;
	public final static short DUMP_CAPACITY = 11;
	public final static short SP_RECLAIM = 12;
	
	
	private short type;
	private short equalityType;
	private List<String> variables;
	private List<BigDecimal> coefficients;
	private BigDecimal value;
	private boolean ignore;
	
	/*public Constraint() {
		variables = new ArrayList<String>();
		coefficients = new ArrayList<BigDecimal>();
		ignore = false;
	}*/
	
	public Constraint(short type) {
		this.type = type;
		variables = new ArrayList<String>();
		coefficients = new ArrayList<BigDecimal>();
		ignore = false;
	}
	
	public short getType() {
		return type;
	}
	public void setType(short type) {
		this.type = type;
	}
	public short getEqualityType() {
		return equalityType;
	}
	public void setEqualityType(short equalityType) {
		this.equalityType = equalityType;
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
	public boolean isIgnore() {
		return ignore;
	}
	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}
	public void addVariable(String variable, BigDecimal coeff) {
		variables.add(variable);
		coefficients.add(coeff);
	}

}
