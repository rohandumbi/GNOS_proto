package com.org.gnos.custom.models;

import java.util.List;
import java.util.Map;

import com.org.gnos.services.csv.ColumnHeader;

public class ProjectModel {
	private Map<String, String> projectMetaData;
	private String[] allProjectFields;
	
	public ProjectModel(){
		super();
	}
	public ProjectModel(Map<String, String> attributeMap){
		super();
		this.projectMetaData = attributeMap;
	}
	
	public void put(String key, String value){
		this.projectMetaData.put(key, value);
	}
	
	public String get(String key){
		return this.projectMetaData.get(key);
	}
	public String[] getAllProjectFields() {
		return allProjectFields;
	}
	public void setAllProjectFields(String[] allProjectFields) {
		this.allProjectFields = allProjectFields;
	}
}
