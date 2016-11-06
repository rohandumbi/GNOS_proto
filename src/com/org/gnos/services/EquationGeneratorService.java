package com.org.gnos.services;

import com.org.gnos.equation.BenchConstraintEquationGenerator;
import com.org.gnos.equation.BinaryVariableGenerator;
import com.org.gnos.equation.BoundaryVariableGenerator;
import com.org.gnos.equation.CapexEquationGenerator;
import com.org.gnos.equation.DumpCapacityEquationGenerator;
import com.org.gnos.equation.DumpDependencyEquationGenerator;
import com.org.gnos.equation.GradeConstraintEquationGenerator;
import com.org.gnos.equation.InstanceData;
import com.org.gnos.equation.ObjectiveFunctionEquationGenerator;
import com.org.gnos.equation.PitDependencyEquationGenerator;
import com.org.gnos.equation.ProcessConstraintEquationGenerator;


public class EquationGeneratorService {

	final static EquationGeneratorService instance = new EquationGeneratorService();
	
	public static EquationGeneratorService getInstance(){
		return instance;
	}

	public void execute() {
		InstanceData  data = new InstanceData();
		
		new ObjectiveFunctionEquationGenerator(data).generate();
		new ProcessConstraintEquationGenerator(data).generate();
		new GradeConstraintEquationGenerator(data).generate();
		new BinaryVariableGenerator(data).generate();
		new BenchConstraintEquationGenerator(data).generate();
		new BoundaryVariableGenerator(data).generate();
		new PitDependencyEquationGenerator(data).generate();
		new CapexEquationGenerator(data).generate();
		new DumpDependencyEquationGenerator(data).generate();
		new DumpCapacityEquationGenerator(data).generate();
		
	}	
}
