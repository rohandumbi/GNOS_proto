package com.org.gnos.scheduler.solver;

import com.org.gnos.scheduler.equation.ExecutionContext;
import com.org.gnos.scheduler.processor.IStorageHelper;


public interface ISolver {

	
	public void solve(String fileName, int timePeriod);
	
	public void setContext(ExecutionContext context);
	
	public void setStorageHelper(IStorageHelper helper);
}
