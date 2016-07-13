package com.org.gnos.services;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Block;
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.GradeConstraintData;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.Pit;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Process;
import com.org.gnos.db.model.ProcessConstraintData;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.db.model.Product;
import com.org.gnos.db.model.ProductJoin;

public class GradeConstraintEquationGenerator {

	static final int BYTES_PER_LINE = 256;
	
	private BufferedOutputStream output;
	private ProjectConfigutration projectConfiguration;
	private ScenarioConfigutration scenarioConfigutration;
	private List<GradeConstraintData> gradeConstraintDataList;
	
	private int bytesWritten = 0;

	
	public void generate() {
		projectConfiguration = ProjectConfigutration.getInstance();
		scenarioConfigutration = ScenarioConfigutration.getInstance();
		gradeConstraintDataList = scenarioConfigutration.getGradeConstraintDataList();
		
		int bufferSize = 8 * 1024;
		try {
			output = new BufferedOutputStream(new FileOutputStream("gradeConstraint.txt"), bufferSize);
			bytesWritten = 0;
			buildGradeConstraintVariables();
			output.flush();
			output.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public void buildGradeConstraintVariables() {
		
		int timePeriod = scenarioConfigutration.getTimePeriod();
		int startYear = scenarioConfigutration.getStartYear();
		List<Process> processList = projectConfiguration.getProcessList();
		for(GradeConstraintData gradeConstraintData: gradeConstraintDataList) {
			if(!gradeConstraintData.isInUse()) continue;
			int selectorType = gradeConstraintData.getSelectionType();

			ProductJoin pj = projectConfiguration.getProductJoinByName(gradeConstraintData.getProductJoinName());
			List<Product> products = getProductsFromProductJoin(pj);
			
			for(int i=1; i<= timePeriod; i++){
				String eq = "";
				if(selectorType == GradeConstraintData.SELECTION_PROCESS_JOIN) {
					ProcessJoin processJoin = projectConfiguration.getProcessJoinByName(gradeConstraintData.getSelectorName());
					if(processJoin != null) {
						for(Model model: processJoin.getlistChildProcesses()){
							for( Process p: processList){
								if(p.getModel().getName().equals(model.getName())){
									String coefficient = "";
									eq += buildGradeConstraintVariables(p.getProcessNo(), coefficient, p.getBlocks(), i);
									break;
								}
							}
						}
					}
				}else if(selectorType == GradeConstraintData.SELECTION_PROCESS) {
					for( Process p: processList){
						if(p.getModel().getName().equals(gradeConstraintData.getSelectorName())){
							String coefficient = "";
							eq += buildGradeConstraintVariables(p.getProcessNo(), coefficient, p.getBlocks(), i);
							break;
						}
					}
				} else if(selectorType == GradeConstraintData.SELECTION_PIT) {
					String pitName = gradeConstraintData.getSelectorName();
					Pit pit = projectConfiguration.getPitfromPitName(pitName);
					if(pit != null) {
						for( Process p: processList){
							String coefficient = "";
							List<Block> blocks = new ArrayList<Block>();
							for(Block b: p.getBlocks()){
								if(b.getPitNo() == pit.getPitNumber()){
									blocks.add(b);
								}
							}
							eq += buildGradeConstraintVariables(p.getProcessNo(), coefficient, blocks, i);
						}
					}
					
					
					
				} else if(selectorType == GradeConstraintData.SELECTION_PIT_GROUP) {
					PitGroup pg = projectConfiguration.getPitGroupfromName(gradeConstraintData.getSelectorName());
					Set pitNumbers = getPitsFromPitGroup(pg);
					for( Process p: processList){
						String coefficient = "";
						List<Block> blocks = new ArrayList<Block>();
						for(Block b: p.getBlocks()){
							if(pitNumbers.contains(b.getPitNo())){
								blocks.add(b);
							}
						}
						eq += buildGradeConstraintVariables(p.getProcessNo(), coefficient, blocks, i);
					}
				} else {
					for( Process p: processList){
						String coefficient = "";
						eq += buildGradeConstraintVariables(p.getProcessNo(), coefficient, p.getBlocks(), i);
					}
				}
				
				if(eq.length() > 0) {
					eq = eq.substring(1);
					if(gradeConstraintData.isMax()){
						eq = eq + " <= " +gradeConstraintData.getConstraintData().get(startYear+i -1);
					} else {
						eq = eq + " >= " +gradeConstraintData.getConstraintData().get(startYear+i -1);
					}
					write(eq);
				}
			}
			//
		}
		
	}

	private List<Product> getProductsFromProductJoin(ProductJoin pj) {
		List<Product> products = new ArrayList<Product>();
		if(pj == null) return products;
		products.addAll(pj.getlistChildProducts());
		if(pj.getListChildProductJoins().size() > 0){
			for(ProductJoin pji: pj.getListChildProductJoins()) {
				products.addAll(getProductsFromProductJoin(pji));
			}
		}
		return products;
	}
	
	private Set<Integer> getPitsFromPitGroup(PitGroup pg) {
		Set<Integer> pitNumbers = new HashSet<Integer>();
		if(pg == null) return pitNumbers;
		for(Pit p: pg.getListChildPits()){
			pitNumbers.add(p.getPitNumber());
		}
		for(PitGroup pgi: pg.getListChildPitGroups()){
			pitNumbers.addAll(getPitsFromPitGroup(pgi));
		}
		
		return pitNumbers;
	}
	private Set<String> getProcessListFromProductJoin(ProductJoin pj){
		Set<String> processes = new HashSet<String>();
		 for(Product childProduct: pj.getlistChildProducts()){
			 processes.add(childProduct.getAssociatedProcess().getName());
		 }
		 for(ProductJoin childJoin: pj.getListChildProductJoins()) {
			 processes.addAll(getProcessListFromProductJoin(childJoin));
		 }
		 return processes;
	}
	
	public String buildGradeConstraintVariables(int processNumber, String coefficient, List<Block> blocks, int period) {
		
		String eq = "";
		for(Block block: blocks){
			float processRatio = 0;
			String expressionName = coefficient.replaceAll("\\s+","_");
			processRatio = block.getRatioField(expressionName);	
			if(processRatio == 0) continue;
			
			eq +=  "+ "+ processRatio+"p"+block.getPitNo()+"x"+block.getBlockNo()+"p"+processNumber+"t"+period;
		}			
		return eq;
	}
	

	private void write(String s) {

		try {
			s = s +"\r\n";
			byte[] bytes = s.getBytes();
			output.write(bytes);
			output.flush();			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}	
	
}
