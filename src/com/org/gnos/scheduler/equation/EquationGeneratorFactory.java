package com.org.gnos.scheduler.equation;

import java.io.BufferedOutputStream;

import com.org.gnos.scheduler.equation.generator.BenchConstraintEquationGenerator;
import com.org.gnos.scheduler.equation.generator.BenchProportionEquationGenerator;
import com.org.gnos.scheduler.equation.generator.BinaryVariableGenerator;
import com.org.gnos.scheduler.equation.generator.BoundaryVariableGenerator;
import com.org.gnos.scheduler.equation.generator.CapexEquationGenerator;
import com.org.gnos.scheduler.equation.generator.DumpCapacityEquationGenerator;
import com.org.gnos.scheduler.equation.generator.DumpDependencyEquationGenerator;
import com.org.gnos.scheduler.equation.generator.EquationGenerator;
import com.org.gnos.scheduler.equation.generator.GradeConstraintEquationGenerator;
import com.org.gnos.scheduler.equation.generator.ObjectiveFunctionEquationGenerator;
import com.org.gnos.scheduler.equation.generator.PitDependencyEquationGenerator;
import com.org.gnos.scheduler.equation.generator.ProcessConstraintEquationGenerator;
import com.org.gnos.scheduler.equation.generator.SPReclaimEquationGenerator;


public class EquationGeneratorFactory {
	
	public final static short OBJECTIVE_FUNCTION = 1;
	public final static short PROCESS_CONSTRAINT = 2;
	public final static short GRADE_CONSTRAINT = 3;
	public final static short BINARY_VARIABLE = 4;
	public final static short BENCH_CONSTRAINT = 5;
	public final static short BENCH_PROPORTIONS = 6;
	public final static short BOUNDARY_VARIABLE = 7;
	public final static short PIT_DEPENDENCY = 8;
	public final static short CAPEX = 9;
	public final static short DUMP_DEPENDENCY = 10;
	public final static short DUMP_CAPACITY = 11;
	public final static short SP_RECLAIM = 12;
	
	
	public static EquationGenerator getGenerator(short type, ExecutionContext context, BufferedOutputStream writer) {
		EquationGenerator generator = null;
		
		switch (type) {
			case OBJECTIVE_FUNCTION:
				generator = new ObjectiveFunctionEquationGenerator(context);
				break;
			case PROCESS_CONSTRAINT:
				generator = new ProcessConstraintEquationGenerator(context);
				break;
			case GRADE_CONSTRAINT:
				generator = new GradeConstraintEquationGenerator(context);
				break;
			case BINARY_VARIABLE:
				generator = new BinaryVariableGenerator(context);
				break;
			case BENCH_CONSTRAINT:
				generator = new BenchConstraintEquationGenerator(context);
				break;
			case BENCH_PROPORTIONS:
				generator = new BenchProportionEquationGenerator(context);
				break;
			case BOUNDARY_VARIABLE:
				generator = new BoundaryVariableGenerator(context);
				break;
			case PIT_DEPENDENCY:
				generator = new PitDependencyEquationGenerator(context);
				break;
			case CAPEX:
				generator = new CapexEquationGenerator(context);
				break;
			case DUMP_DEPENDENCY:
				generator = new DumpDependencyEquationGenerator(context);
				break;
			case DUMP_CAPACITY:
				generator = new DumpCapacityEquationGenerator(context);
				break;
			case SP_RECLAIM:
				if(context instanceof GlobalModeExecutionContext){
					generator = new SPReclaimEquationGenerator(context);
				} else {
					generator = new SPReclaimEquationGenerator(context);
				}				
				break;
		}
		generator.setOutput(writer);
		return generator;
	}

}
