package com.org.gnos.scheduler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import sun.org.mozilla.javascript.internal.Context;

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

			Map<String, Boolean> eqnenablestate = new HashMap<String, Boolean>();
			eqnenablestate.put("PROCESS_CONSTRAINT", Boolean.valueOf(prop.getProperty("PROCESS_CONSTRAINT", "TRUE")));
			eqnenablestate.put("BENCH_CONSTRAINT", Boolean.valueOf(prop.getProperty("BENCH_CONSTRAINT", "TRUE")));
			eqnenablestate.put("GRADE_CONSTRAINT", Boolean.valueOf(prop.getProperty("GRADE_CONSTRAINT", "TRUE")));
			eqnenablestate.put("BENCH_PROPORTION", Boolean.valueOf(prop.getProperty("BENCH_PROPORTION", "TRUE")));
			eqnenablestate.put("DUMP_CAPACITIES", Boolean.valueOf(prop.getProperty("DUMP_CAPACITIES", "TRUE")));
			eqnenablestate.put("CAPEX", Boolean.valueOf(prop.getProperty("CAPEX", "TRUE")));
			eqnenablestate.put("DUMP_DEPENDENCY", Boolean.valueOf(prop.getProperty("DUMP_DEPENDENCY", "TRUE")));
			eqnenablestate.put("PIT_DEPENDENCY", Boolean.valueOf(prop.getProperty("PIT_DEPENDENCY", "TRUE")));
			eqnenablestate.put("BOUNDARY_VARIABLES", Boolean.valueOf(prop.getProperty("BOUNDARY_VARIABLES", "TRUE")));
			
			scheduler.getContext().setEquationgEnableMap(eqnenablestate);

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
