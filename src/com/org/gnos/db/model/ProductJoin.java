package com.org.gnos.db.model;

import java.util.ArrayList;
import java.util.List;

public class ProductJoin {
	private String name;
	private List<Product> listChildProducts;
	private List<ProductJoin> listChildProductJoins;

	public ProductJoin(String name){
		super();
		this.name = name;
		this.listChildProducts = new ArrayList<Product>();
		this.listChildProductJoins = new ArrayList<ProductJoin>();
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

	public List<ProductJoin> getListChildProductJoins() {
		return listChildProductJoins;
	}

	public void setListChildProductJoins(List<ProductJoin> listChildProductJoins) {
		this.listChildProductJoins = listChildProductJoins;
	}

	public void addProductJoin(ProductJoin productJoin){
		this.listChildProductJoins.add(productJoin);
	}
}
