package com.org.gnos.scheduler.processor;

public class Record {
    
	public static final int ORIGIN_PIT = 1;
	public static final int ORIGIN_SP  = 2;
	
	public static final int DESTINATION_PROCESS = 1;
	public static final int DESTINATION_SP = 2;
	public static final int DESTINATION_WASTE = 3;
	
	int originType;
	int pitNo;
	int blockNo;
	int originSpNo;
	int timePeriod;
	int destinationType;
	int processNo;
	int destSpNo;
	int wasteNo;
	double value;
	
	public int getOriginType() {
		return originType;
	}
	public void setOriginType(int originType) {
		this.originType = originType;
	}
	public int getPitNo() {
		return pitNo;
	}
	public void setPitNo(int pitNo) {
		this.pitNo = pitNo;
	}
	public int getBlockNo() {
		return blockNo;
	}
	public void setBlockNo(int blockNo) {
		this.blockNo = blockNo;
	}
	public int getOriginSpNo() {
		return originSpNo;
	}
	public void setOriginSpNo(int originSpNo) {
		this.originSpNo = originSpNo;
	}
	public int getTimePeriod() {
		return timePeriod;
	}
	public void setTimePeriod(int timePeriod) {
		this.timePeriod = timePeriod;
	}
	public int getDestinationType() {
		return destinationType;
	}
	public void setDestinationType(int destinationType) {
		this.destinationType = destinationType;
	}
	public int getProcessNo() {
		return processNo;
	}
	public void setProcessNo(int processNo) {
		this.processNo = processNo;
	}
	public int getDestSpNo() {
		return destSpNo;
	}
	public void setDestSpNo(int destSpNo) {
		this.destSpNo = destSpNo;
	}
	public int getWasteNo() {
		return wasteNo;
	}
	public void setWasteNo(int wasteNo) {
		this.wasteNo = wasteNo;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		if(this.originType == ORIGIN_PIT){
			buff.append(pitNo+","+blockNo+",");
		} else {
			buff.append(originSpNo+",,");
		}
		if(this.destinationType == DESTINATION_PROCESS){
			buff.append("p"+processNo+",");
		} else if(this.destinationType == DESTINATION_SP){
			buff.append("s"+destSpNo+",");
		} else if(this.destinationType == DESTINATION_WASTE){
			buff.append("w"+wasteNo+",");
		}
		buff.append(timePeriod+",");
		buff.append(value+"\r\n");
		
		
		return buff.toString();
	}
	
	
}
