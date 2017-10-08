package com.org.gnos.db.model;

public class CapexInstance {
	private int id;
	private int capexId;
	private String name;
	private String groupingName;
	private int groupingType;
	private long capexAmount;
	private long expansionCapacity;
	
	public static final int SELECTION_NONE = 0;
	public static final int SELECTION_PROCESS_JOIN = 1;
	public static final int SELECTION_PROCESS = 2;
	public static final int SELECTION_PIT = 3;
	public static final int SELECTION_PIT_GROUP = 4;
	
	public CapexInstance(){
		super();
		this.id = -1;
		this.capexId = -1;
		this.groupingType = -1;
		this.capexAmount = -1;
		this.expansionCapacity = -1;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCapexId() {
		return capexId;
	}
	public void setCapexId(int capexId) {
		this.capexId = capexId;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getGroupingName() {
		return groupingName;
	}
	public void setGroupingName(String groupingName) {
		this.groupingName = groupingName;
	}
	public int getGroupingType() {
		return groupingType;
	}
	public void setGroupingType(int groupingType) {
		this.groupingType = groupingType;
	}
	public long getCapexAmount() {
		return capexAmount;
	}
	public void setCapexAmount(long capexAmount) {
		this.capexAmount = capexAmount;
	}
	public long getExpansionCapacity() {
		return expansionCapacity;
	}
	public void setExpansionCapacity(long expansionCapacity) {
		this.expansionCapacity = expansionCapacity;
	}

	@Override
	public String toString() {
		return name + "," + groupingName + "," + groupingType + "," + capexAmount + "," + expansionCapacity;
	}
	
	
}
