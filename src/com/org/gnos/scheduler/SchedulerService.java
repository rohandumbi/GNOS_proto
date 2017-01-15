package com.org.gnos.scheduler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.org.gnos.scheduler.equation.SlidingWindowExecutionContext;
import com.org.gnos.scheduler.solver.ISolver;
import com.org.gnos.scheduler.solver.cplex.CplexSolver;


public class SchedulerService {

	private static final short GLOBAL_MODE = 1;
	private static final short SLIDING_WINDOW_MODE = 2;
	
	final static SchedulerService instance = new SchedulerService();
	private BaseScheduler scheduler;
	
	public static SchedulerService getInstance(){
		return instance;
	}

	
	public void execute() {
		
		loadConfiguration();
		
		ISolver solver = new CplexSolver();
		scheduler.setSolver(solver);
		scheduler.execute();
	}
	
	
	private void loadConfiguration() {
		Properties prop = new Properties();
		InputStream in = null;

		try {

			in = new FileInputStream("c:\\gnos_run_config.properties");
			prop.load(in);
			// get the mode
			short run_mode = Short.parseShort(prop.getProperty("run_mode", "1"));
			createScheduler(run_mode);
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
		}
	 }
	


	private void createScheduler(short mode) {		
		switch (mode) {
		case SLIDING_WINDOW_MODE:			
			scheduler = new SlidingWindowModeScheduler();
			break;
		case GLOBAL_MODE:
		default:
			scheduler = new GLobalModeScheduler();
			break;
		}
	}
}
