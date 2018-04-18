package com.org.gnos.scheduler;

import com.org.gnos.scheduler.equation.EquationGeneratorFactory;
import com.org.gnos.scheduler.equation.ExecutionContext;
import com.org.gnos.scheduler.processor.IStorageHelper;
import com.org.gnos.scheduler.solver.ISolver;


public abstract class BaseScheduler {

	protected ExecutionContext context;
	protected ISolver solver;
	protected IStorageHelper helper;
	
	protected void generateEquations(int period) {
			
		EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.OBJECTIVE_FUNCTION, context).generate();
		
		if(context.getEquationEnableMap().get("BOUNDARY_VARIABLES")) {
			EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.BOUNDARY_VARIABLE, context).generate();
		}
		if(context.getEquationEnableMap().get("PROCESS_CONSTRAINT")) {
			EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.PROCESS_CONSTRAINT, context).generate();
		}
		if(context.getEquationEnableMap().get("BENCH_CONSTRAINT")) {
			EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.BENCH_CONSTRAINT, context).generate();				
		}
		if(context.getEquationEnableMap().get("BENCH_PROPORTION")) {
			EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.BENCH_PROPORTIONS, context).generate();	
		}
		if(context.getEquationEnableMap().get("DUMP_CAPACITIES")) {
			EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.DUMP_CAPACITY, context).generate();
		}
		EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.SP_RECLAIM, context).generate();
		if(context.getEquationEnableMap().get("CAPEX")) {
			EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.CAPEX, context).generate();
		}
		if(context.getEquationEnableMap().get("DUMP_DEPENDENCY")) {
			EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.DUMP_DEPENDENCY, context).generate();
		}
		if(context.getEquationEnableMap().get("PIT_DEPENDENCY")) {
			EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.PIT_DEPENDENCY, context).generate();
		}
		if(context.getEquationEnableMap().get("GRADE_CONSTRAINT")) {
			EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.GRADE_CONSTRAINT, context).generate();
		}
		
		EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.BINARY_VARIABLE, context).generate();		
		
	}

	public void runSolver(int timePriodStart) {
		solver.setStorageHelper(helper);
		solver.solve("gtp_dump_"+timePriodStart+".lp", timePriodStart);
	}
	
	public ExecutionContext getContext() {
		return context;
	}

	public void setContext(ExecutionContext context) {
		this.context = context;
	}

	public ISolver getSolver() {
		return solver;
	}

	public void setSolver(ISolver solver) {
		this.solver = solver;
	}

	
	public abstract void execute();
}
