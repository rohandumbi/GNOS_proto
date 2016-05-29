package com.org.gnos.db.model;

import java.util.ArrayList;
import java.util.List;

public class ProductJoin {
	private String name;
	private List<Product> listChildProducts;

	public ProductJoin(String name){
		super();
		this.name = name;
		this.listChildProducts = new ArrayList<Product>();
	}
	
	public void addProduct(Product product){
		this.listChildProducts.add(product);
	}
	
	public String getName() {
		return name;
	}

	public List<Product> getlistChildProducts() {
		return listChildProducts;
	}
	
	public void setListChildProducts(List<Product> listChildProducts) {
		this.listChildProducts = listChildProducts;
	}

}
