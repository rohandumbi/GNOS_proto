package com.org.gnos.scheduler;

import com.org.gnos.db.model.RunConfig;
import com.org.gnos.scheduler.solver.ISolver;
import com.org.gnos.scheduler.solver.cplex.CplexSolver;


public class SchedulerService {

	final static SchedulerService instance = new SchedulerService();
	private BaseScheduler scheduler;
	
	public static SchedulerService getInstance(){
		return instance;
	}

	
	public void execute(RunConfig runconfig) {
		
		ISolver solver = new CplexSolver();
		createScheduler(runconfig);
		scheduler.getContext().setEquationEnableMap(runconfig.getEqnenablestate());
		scheduler.setSolver(solver);
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
}
