package com.org.gnos.services;

public class Operation {

	private int id;
	private int operand_left;
	private int operand_right;
	private int operator;
	
	public int getOperand_left() {
		return operand_left;
	}
	public void setOperand_left(int operand_left) {
		this.operand_left = operand_left;
	}
	public int getOperand_right() {
		return operand_right;
	}
	public void setOperand_right(int operand_right) {
		this.operand_right = operand_right;
	}
	public int getOperator() {
		return operator;
	}
	public void setOperator(int operator) {
		this.operator = operator;
	}
	public int getId() {
		return id;
	}	
	
}
