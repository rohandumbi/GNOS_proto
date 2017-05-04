package com.org.gnos.scheduler.solver;

import com.org.gnos.scheduler.processor.IStorageHelper;


public interface ISolver {

	
	public void solve(String fileName, int timePeriod);
	
	public void setStorageHelper(IStorageHelper helper);
	
	public void addVariable(short type);
	
	public void addConstraint(short type);
}
