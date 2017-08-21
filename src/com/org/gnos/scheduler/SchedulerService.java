package com.org.gnos.scheduler;

import com.org.gnos.core.LogManager;
import com.org.gnos.db.model.RunConfig;
import com.org.gnos.scheduler.solver.ISolver;
import com.org.gnos.scheduler.solver.cplex.CplexSolver;


public class SchedulerService implements Runnable {

	private BaseScheduler scheduler;
	private RunConfig runconfig;


	
	public void execute() {
		LogManager.logSchedule(runconfig.toString());
		ISolver solver = new CplexSolver();
		((CplexSolver)solver).setMIPGAP(runconfig.getMIPGAP());
		createScheduler(runconfig);
		scheduler.getContext().setEquationEnableMap(runconfig.getEqnenablestate());
		scheduler.setSolver(solver);
		solver.setContext(scheduler.getContext());
		scheduler.execute();
	}
	

	


	private void createScheduler(RunConfig runconfig) {		
		switch (runconfig.getMode()) {
		case RunConfig.SLIDING_WINDOW_MODE:			
			scheduler = new SlidingWindowModeScheduler(runconfig);
			break;
		case RunConfig.GLOBAL_MODE:
		default:
			scheduler = new GLobalModeScheduler(runconfig);
			break;
		}
	}

	public void setRunconfig(RunConfig runconfig) {
		this.runconfig = runconfig;
	}

	@Override
	public void run() {
		execute();
	}
}
