package com.org.gnos.custom.models;

import java.util.Map;

public class ProjectMetaDataModel {
	private Map<String, String> projectMetaData;
	public ProjectMetaDataModel(){
		super();
	}
	public ProjectMetaDataModel(Map<String, String> attributeMap){
		super();
		this.projectMetaData = attributeMap;
	}
	
	public void put(String key, String value){
		this.projectMetaData.put(key, value);
	}
	
	public String get(String key){
		return this.projectMetaData.get(key);
	}
}
