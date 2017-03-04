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
		
		loadConfiguration();
		
		ISolver solver = new CplexSolver();
		createScheduler(runconfig.getMode());
		scheduler.getContext().setEquationgEnableMap(runconfig.getEqnenablestate());
		scheduler.setSolver(solver);
		scheduler.execute();
	}
	
	
	private void loadConfiguration() {
/*		Properties prop = new Properties();
		InputStream in = null;

		try {

			if(run_mode == 2) {
				SlidingWindowExecutionContext context = (SlidingWindowExecutionContext) scheduler.getContext();
				context.setPeriod(Short.parseShort(prop.getProperty("optimisation_period", "0")));
				context.setWindow(Short.parseShort(prop.getProperty("optimisation_window", "0")));
				context.setStepsize(Short.parseShort(prop.getProperty("step_size", "0")));
				context.setCurrPeriod(Short.parseShort(prop.getProperty("curr_period", "0")));
			}


		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}*/
	 }
	


	private void createScheduler(short mode) {		
		switch (mode) {
		case RunConfig.SLIDING_WINDOW_MODE:			
			scheduler = new SlidingWindowModeScheduler();
			break;
		case RunConfig.GLOBAL_MODE:
		default:
			scheduler = new GLobalModeScheduler();
			break;
		}
	}
}
