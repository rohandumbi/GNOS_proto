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
import com.org.gnos.db.model.ProcessConstraintData;
import com.org.gnos.db.model.ProcessJoin;

public class ProcessConstraintEquationGenerator {

	static final int BYTES_PER_LINE = 256;
	
	private BufferedOutputStream output;
	private ProjectConfigutration projectConfiguration;
	private ScenarioConfigutration scenarioConfigutration;
	private List<Process> porcesses;
	private List<ProcessConstraintData> processConstraintDataList;
	
	private int bytesWritten = 0;

	
	public void generate() {
		projectConfiguration = ProjectConfigutration.getInstance();
		scenarioConfigutration = ScenarioConfigutration.getInstance();
		porcesses = projectConfiguration.getProcessList();
		processConstraintDataList = scenarioConfigutration.getProcessConstraintDataList();
		
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
		int startYear = scenarioConfigutration.getStartYear();
		for(ProcessJoin processjoin:processJoins) {
			List<ProcessConstraintData> processConstraintList = getProcessConstraintData(processjoin);
			if(processConstraintList.size() < 1) continue;
			for(ProcessConstraintData data: processConstraintList){
				String expressionName = data.getExpression().getName().replaceAll("\\s+","_");
				
				for(int i=1; i<= timePeriod; i++){
					String eq = "";
					boolean firstVariable = true;
					List<Model> childModels = processjoin.getlistChildProcesses();
					for(Model model: childModels) {
						Process process = getProcessFromModel(model);
						if(process == null) continue;
						List<Block> blocks = process.getBlocks();
						for(Block block: blocks){
							//String expressionName = model.getExpression().getName().replaceAll("\\s+","_");
							float processRatio = block.getRatioField(expressionName);
							String tmpEq= processRatio+"p"+block.getPitNo()+"x"+block.getBlockNo()+"p"+process.getProcessNo()+"t"+i;
							if(firstVariable) {
								firstVariable = false;
							} else {
								tmpEq = " + "+tmpEq;
							}
							eq = eq + tmpEq;
						}
					}
					if(data.isMax()){
						eq = eq + "<=" +data.getConstraintData().get(startYear+i -1);
					} else {
						eq = eq + ">=" +data.getConstraintData().get(startYear+i -1);
					}
					write(eq);
				}
			}			
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
	
	private List<ProcessConstraintData> getProcessConstraintData(ProcessJoin processjoin) {
		List<ProcessConstraintData> data = new ArrayList<ProcessConstraintData>();
		for(ProcessConstraintData processConstraintData: processConstraintDataList){
			if(processConstraintData.isInUse() && processConstraintData.getProcessJoin().getName().equals(processjoin.getName())){
				data.add(processConstraintData);
			}
		}
		
		return data;
	}
	private void write(String s) {

		try {
			s = s +"\r\n";
			byte[] bytes = s.getBytes();
			/*if(bytes.length + bytesWritten > BYTES_PER_LINE){
				output.write("\r\n".getBytes());
				output.flush();
				bytesWritten = 0;
			}*/
			output.write(bytes);
			output.flush();
			//bytesWritten = bytesWritten + bytes.length;
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}	
	
}
