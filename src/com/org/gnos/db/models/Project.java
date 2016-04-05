package com.org.gnos.db.models;

import java.util.Date;

public class Project {

	private int id;
	private String name;
	private String desc;
	private Date createdDate;
	private Date modifiedDate;
	private String fileName;
	
	public Project(String name, String desc) {
		super();
		this.name = name;
		this.desc = desc;
		
		Date curr_date = new Date();
		
		this.createdDate = curr_date;
		this.modifiedDate = curr_date;
	}
	
	
	public Project(int id, String name, String desc, String fileName, Date createdDate, Date modifiedDate) {
		super();
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.fileName = fileName;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
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
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public Date getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
}
