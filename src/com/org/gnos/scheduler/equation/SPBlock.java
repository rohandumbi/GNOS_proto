package com.org.gnos.scheduler.equation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.org.gnos.db.model.Process;

public class SPBlock {

	double tonnesWt;
	private Map<String, BigDecimal> computedFields;
	int payload;
	private Set<Process> processes;

	
	public SPBlock() {
		tonnesWt = 0.0;
		computedFields = new HashMap<String, BigDecimal>();
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
	
}
