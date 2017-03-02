package com.org.gnos.db.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductJoin {
	
	public static short CHILD_PRODUCT = 1 ;
	public static short CHILD_PRODUCT_JOIN = 2 ;
	
	private String name;
	private Set<String> productList;
	private Set<String> productJoinList;
	private List<String> gradeNames;

	public ProductJoin(){
		this.productList = new HashSet<String>();
		this.productJoinList = new HashSet<String>();
		this.gradeNames = new ArrayList<String>();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getProductList() {
		return productList;
	}

	public void setProductList(Set<String> productList) {
		this.productList = productList;
	}

	public Set<String> getProductJoinList() {
		return productJoinList;
	}

	public void setProductJoinList(Set<String> productJoinList) {
		this.productJoinList = productJoinList;
	}

	public List<String> getGradeNames() {
		return gradeNames;
	}

	public void setGradeNames(List<String> gradeNames) {
		this.gradeNames = gradeNames;
	}
	public void addGradeName(String gradeName) {
		this.gradeNames.add(gradeName);
	}
	
}
