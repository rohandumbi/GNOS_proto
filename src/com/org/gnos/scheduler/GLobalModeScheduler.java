package com.org.gnos.scheduler;

import com.org.gnos.scheduler.equation.GlobalModeExecutionContext;

public class GLobalModeScheduler extends BaseScheduler {

	
	public GLobalModeScheduler() {
		super();
		context = new GlobalModeExecutionContext();
	}

	@Override
	public void execute() {
		//loadData();
		generateEquations(1);
		runSolver(1);
		//processResults();
	}

}
