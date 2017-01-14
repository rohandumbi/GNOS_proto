package com.org.gnos.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.org.gnos.equation.EquationContext;
import com.org.gnos.equation.EquationGeneratorFactory;
import com.org.gnos.equation.GlobalModeExecutionContext;
import com.org.gnos.equation.GlobalModeResultProcessor;
import com.org.gnos.equation.ResultProcessor;
import com.org.gnos.equation.SlidingWindowEquationContext;
import com.org.gnos.equation.SlidingWindowResultProcessor;


public class EquationGeneratorService {

	private static final short GLOBAL_MODE = 1;
	private static final short SLIDING_WINDOW_MODE = 2;
	
	final static EquationGeneratorService instance = new EquationGeneratorService();
	private EquationContext context;
	private ResultProcessor resultProcessor;
	
	public static EquationGeneratorService getInstance(){
		return instance;
	}

	
	public void execute() {
		
		loadConfiguration();
		
		EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.OBJECTIVE_FUNCTION, context).generate();
		EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.PROCESS_CONSTRAINT, context).generate();
		EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.GRADE_CONSTRAINT, context).generate();
		EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.BINARY_VARIABLE, context).generate();
		EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.BENCH_CONSTRAINT, context).generate();
		EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.BENCH_PROPORTIONS, context).generate();
		EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.BOUNDARY_VARIABLE, context).generate();
		EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.PIT_DEPENDENCY, context).generate();
		EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.CAPEX, context).generate();
		EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.DUMP_DEPENDENCY, context).generate();
		EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.DUMP_CAPACITY, context).generate();
		EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.SP_RECLAIM, context).generate();
		
	}
	
	
	private void loadConfiguration() {
		Properties prop = new Properties();
		InputStream in = null;

		try {

			in = new FileInputStream("c:\\gnos_run_config.properties");
			prop.load(in);
			// get the mode
			short run_mode = Short.parseShort(prop.getProperty("run_mode", "0"));
			createContext(run_mode);
			if(run_mode == 2) {
				((SlidingWindowEquationContext)context).setPeriod(Short.parseShort(prop.getProperty("optimisation_period", "0")));
				((SlidingWindowEquationContext)context).setWindow(Short.parseShort(prop.getProperty("optimisation_window", "0")));
				((SlidingWindowEquationContext)context).setStepsize(Short.parseShort(prop.getProperty("step_size", "0")));
				((SlidingWindowEquationContext)context).setCurrPeriod(Short.parseShort(prop.getProperty("curr_period", "0")));
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
	


	private void createContext(short mode) {		
		switch (mode) {
		case SLIDING_WINDOW_MODE:
			context = new SlidingWindowEquationContext();
			resultProcessor = new GlobalModeResultProcessor();
			break;
		case GLOBAL_MODE:
		default:
			context = new GlobalModeExecutionContext();
			resultProcessor = new SlidingWindowResultProcessor();
			break;
		}
	}
}
