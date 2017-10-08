package com.org.gnos.db.model;

import java.util.HashSet;
import java.util.Set;

public class ProductJoin {
	
	public static short CHILD_PRODUCT = 1 ;
	public static short CHILD_PRODUCT_JOIN = 2 ;
	
	private String name;
	private Set<String> productList;
	private Set<String> productJoinList;


	public ProductJoin(){
		this.productList = new HashSet<String>();
		this.productJoinList = new HashSet<String>();
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

	@Override
	public String toString() {
		String str = name ;
		str += "|";
		for (String child: productList) {
			str += child +",";
		}
		str += "|";
		for (String child: productJoinList) {
			str += child +",";
		}
		
		return str;
	}

	
}
