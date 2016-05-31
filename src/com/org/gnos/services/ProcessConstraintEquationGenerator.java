package com.org.gnos.services;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.org.gnos.core.Block;
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.Process;
import com.org.gnos.db.model.ProcessJoin;

public class ProcessConstraintEquationGenerator {

	static final int BYTES_PER_LINE = 256;
	
	private BufferedOutputStream output;
	private ProjectConfigutration projectConfiguration;
	private ScenarioConfigutration scenarioConfigutration;
	private List<Process> porcesses;
	
	private int bytesWritten = 0;

	
	public void generate() {
		projectConfiguration = ProjectConfigutration.getInstance();
		scenarioConfigutration = ScenarioConfigutration.getInstance();
		porcesses = projectConfiguration.getProcessList();

		int bufferSize = 8 * 1024;
		try {
			output = new BufferedOutputStream(new FileOutputStream("processConstraint.txt"), bufferSize);
			bytesWritten = 0;
			buildProcessJoinVariables();
			output.flush();
			output.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	public void buildProcessJoinVariables() {
		List<ProcessJoin> processJoins = projectConfiguration.getProcessJoins();
		int timePeriod = scenarioConfigutration.getTimePeriod();
		for(ProcessJoin processjoin:processJoins) {
			boolean firstVariable = true;
			List<Model> childModels = processjoin.getlistChildProcesses();
			for(Model model: childModels) {
				Process process = getProcessFromModel(model);
				if(process == null) continue;
				List<Block> blocks = process.getBlocks();
				for(Block block: blocks){
					String expressionName = model.getExpression().getName().replaceAll("\\s+","_");
					float processRatio = block.getRatioField(expressionName);
					for(int i=1; i<= timePeriod; i++){

						String eq = processRatio+"p"+block.getPitNo()+"x"+block.getBlockNo()+"p"+process.getProcessNo()+"t"+i;
						if(firstVariable) {
							firstVariable = false;
						} else {
							eq = " + "+eq;
						}
						write(eq);
					}
				}
			}
			write(" <= 1000");
		}
	}
	
	private Process getProcessFromModel(Model model) {

		for(Process process: this.porcesses) {
			if(process.getModel().getId() == model.getId()){
				return process;
			}
		}
		
		return null;
	}
	private void write(String s) {

		try {
			byte[] bytes = s.getBytes();
			if(bytes.length + bytesWritten > BYTES_PER_LINE){
				output.write("\r\n".getBytes());
				output.flush();
				bytesWritten = 0;
			}
			output.write(bytes);
			bytesWritten = bytesWritten + bytes.length;
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}	
	
}
