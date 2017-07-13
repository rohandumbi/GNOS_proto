package com.org.gnos.db.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Project {

	private int id;
	private String name;
	private String desc;
	private Date createdDate;
	private Date modifiedDate;
	private List<String> files;
	
	public Project() {
		super();
		this.id = -1;
		Date curr_date = new Date();
		this.createdDate = curr_date;
		this.modifiedDate = curr_date;
		this.files = new ArrayList<String>();
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
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public List<String> getFiles() {
		return files;
	}
	public void setFiles(List<String> files) {
		this.files = files;
	}
	public void addFile(String fileName) {
		if(this.files != null) {
			this.files.add(fileName);
		}
	}

	@Override
	public String toString() {
		String str = name + "|" + desc + "|" + createdDate + "|" + modifiedDate + "|";
		for(String fileName: files) {
			str += fileName +"|";
		}
		return str;
	}
	
	
}
