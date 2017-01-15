package com.org.gnos.scheduler;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.org.gnos.scheduler.equation.ExecutionContext;
import com.org.gnos.scheduler.equation.EquationGeneratorFactory;
import com.org.gnos.scheduler.solver.ISolver;


public abstract class BaseScheduler {

	static final int bufferSize = 8 * 1024;
	
	protected ExecutionContext context;
	protected ISolver solver;
	private BufferedOutputStream writer;
	
	protected void generateEquations(int period) {
		try {
			
			writer = new BufferedOutputStream(new FileOutputStream("gtp_dump_"+context.getTimePeriodStart()+".lp"), bufferSize);
			write("maximize");
			EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.OBJECTIVE_FUNCTION, context, writer).generate();
			write("\r\nSubject To");
			write("\\ reserve constraints");
			EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.BOUNDARY_VARIABLE, context, writer).generate();
			write("\\ bench constraints");
			EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.BENCH_CONSTRAINT, context, writer).generate();
			
			EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.BENCH_PROPORTIONS, context, writer).generate();						
			EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.CAPEX, context, writer).generate();
			EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.DUMP_DEPENDENCY, context, writer).generate();
			EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.SP_RECLAIM, context, writer).generate();
			
			write("\\ process constraints");
			EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.PROCESS_CONSTRAINT, context, writer).generate();
			write("\\ pit dependency");
			EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.PIT_DEPENDENCY, context, writer).generate();
			write("\\ grade constraints");
			EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.GRADE_CONSTRAINT, context, writer).generate();
			write("\\ Dump capacities");
			EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.DUMP_CAPACITY, context, writer).generate();
			write("Binaries");
			EquationGeneratorFactory.getGenerator(EquationGeneratorFactory.BINARY_VARIABLE, context, writer).generate();		
			write("\r\nend");
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void write(String s) {

		try {
			s = s +"\r\n";
			byte[] bytes = s.getBytes();
			writer.write(bytes);
			writer.flush();			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}	

	public void runSolver(int timePriodStart) {
		solver.solve("gtp_dump_"+timePriodStart+".lp");
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
