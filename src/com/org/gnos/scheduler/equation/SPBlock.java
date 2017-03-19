package com.org.gnos.scheduler.equation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.org.gnos.db.model.Process;

public class SPBlock {

	double tonnesWt;
	double lasttonnesWt;
	double reclaimedTonnesWt;
	private Map<String, BigDecimal> fields;
	private Map<String, BigDecimal> computedFields;
//	private Map<String, BigDecimal> gradeFields;
	int payload;
	private Set<Process> processes;

	
	public SPBlock() {
		tonnesWt = 0.0;
		computedFields = new HashMap<String, BigDecimal>();
		fields = new HashMap<String, BigDecimal>(); 
//		gradeFields = new HashMap<String, BigDecimal>(); 
		processes = new HashSet<Process>();
	}

	public double getTonnesWt() {
		return tonnesWt;
	}

	public void setTonnesWt(double tonnesWt) {
		this.tonnesWt = tonnesWt;
	}

	public Map<String, BigDecimal> getComputedFields() {
		return computedFields;
	}

	public BigDecimal getComputedField(String fieldName) {
		BigDecimal val = this.computedFields.get(fieldName);
		if(val == null ) return new BigDecimal(0);
		else return val;
	}
	
	public void setComputedFields(Map<String, BigDecimal> computedFields) {
		this.computedFields = computedFields;
	}

/*	public Map<String, BigDecimal> getGradeFields() {
		return gradeFields;
	}

	public BigDecimal getGradeField(String fieldName) {
		BigDecimal val = this.gradeFields.get(fieldName);
		if(val == null ) return new BigDecimal(0);
		else return val;
	}
		
	public void setGradeFields(Map<String, BigDecimal> gradeFields) {
		this.gradeFields = gradeFields;
	}*/

	
	public Map<String, BigDecimal> getFields() {
		return fields;
	}

	public void setFields(Map<String, BigDecimal> fields) {
		this.fields = fields;
	}

	public BigDecimal getField(String fieldName) {
		BigDecimal val = this.fields.get(fieldName);
		if(val == null ) return new BigDecimal(0);
		else return val;
	}
	
	public int getPayload() {
		return payload;
	}

	public void setPayload(int payload) {
		this.payload = payload;
	}
	public Set<Process> getProcesses() {
		return processes;
	}

	public void addProcess(Process p){
		processes.add(p);
	}

	public double getLasttonnesWt() {
		return lasttonnesWt;
	}

	public void setLasttonnesWt(double lasttonnesWt) {
		this.lasttonnesWt = lasttonnesWt;
	}

	public double getReclaimedTonnesWt() {
		return reclaimedTonnesWt;
	}

	public void setReclaimedTonnesWt(double reclaimedTonnesWt) {
		this.reclaimedTonnesWt = reclaimedTonnesWt;
	}
	
	public double getRemainingTonnesWt() {
		return reclaimedTonnesWt;
	}
	
	public void reset() {
		lasttonnesWt = 0;
		reclaimedTonnesWt = 0;
		computedFields = new HashMap<String, BigDecimal>();
		//gradeFields = new HashMap<String, BigDecimal>(); 
		processes = new HashSet<Process>();
	}

}
