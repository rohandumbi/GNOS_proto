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
import com.org.gnos.db.model.Grade;
import com.org.gnos.db.model.GradeConstraintData;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.Pit;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Process;
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
			String selectedGradeName = gradeConstraintData.getSelectedGradeName();
			Map<String, List<String>> processExprMap = new HashMap<String, List<String>>();
			Map<String, List<Grade>> processGradeMap = new HashMap<String, List<Grade>>();
			ProductJoin pj = projectConfiguration.getProductJoinByName(gradeConstraintData.getProductJoinName());
			if(pj == null || pj.getGradeNames().size() == 0) continue;		
			int selectedGradeIndex = -1;
			int loopCount = 0;
			for(String gradeName: pj.getGradeNames()){
				if(gradeName.equals(selectedGradeName)){
					selectedGradeIndex = loopCount;
					break;
				}			
				loopCount++;
			}
			List<Product> products = getProductsFromProductJoin(pj);
			for(Product p: products){
				String processName = p.getAssociatedProcess().getName();
				List<String> coefficients = processExprMap.get(processName);
				if(coefficients == null){
					coefficients = new ArrayList<String>();
					processExprMap.put(processName, coefficients);
				}
				for(Expression e : p.getListOfExpressions()){
					coefficients.add(e.getName());
				}
				
				List<Grade> grades = processGradeMap.get(processName);
				if( grades == null ) {
					grades = new ArrayList<Grade>();
					processGradeMap.put(processName, grades);
				}
				grades.add(p.getListOfGrades().get(selectedGradeIndex));
			}
			
			for(int i=1; i<= timePeriod; i++){
				String eq = "";
				float targetGrade = gradeConstraintData.getConstraintData().get(startYear+i -1);
				if(selectorType == GradeConstraintData.SELECTION_PROCESS_JOIN) {
					ProcessJoin processJoin = projectConfiguration.getProcessJoinByName(gradeConstraintData.getSelectorName());
					if(processJoin != null) {
						for(Model model: processJoin.getlistChildProcesses()){
							for( Process p: processList){
								String processName = p.getModel().getName();
								if(processName.equals(model.getName())){
									List<String> coefficients = processExprMap.get(processName);
									List<Grade> grades = processGradeMap.get(processName);
									for(Grade grade: grades){
										eq += buildGradeConstraintVariables(p.getProcessNo(), coefficients, grade, p.getBlocks(), i, targetGrade);
									}
									
									break;
								}
							}
						}
					}
				}else if(selectorType == GradeConstraintData.SELECTION_PROCESS) {
					for( Process p: processList){
						String processName = p.getModel().getName();
						if(p.getModel().getName().equals(gradeConstraintData.getSelectorName())){
							List<String> coefficients = processExprMap.get(processName);
							List<Grade> grades = processGradeMap.get(processName);
							for(Grade grade: grades){
								eq += buildGradeConstraintVariables(p.getProcessNo(), coefficients, grade, p.getBlocks(), i, targetGrade);
							}
							break;
						}
					}
				} else if(selectorType == GradeConstraintData.SELECTION_PIT) {
					String pitName = gradeConstraintData.getSelectorName();
					Pit pit = projectConfiguration.getPitfromPitName(pitName);
					if(pit != null) {
						for( Process p: processList){
							String processName = p.getModel().getName();
							List<String> coefficients = processExprMap.get(processName);
							List<Grade> grades = processGradeMap.get(processName);
							List<Block> blocks = new ArrayList<Block>();
							for(Block b: p.getBlocks()){
								if(b.getPitNo() == pit.getPitNumber()){
									blocks.add(b);
								}
							}
							for(Grade grade: grades){
								eq += buildGradeConstraintVariables(p.getProcessNo(), coefficients, grade, blocks, i, targetGrade);
							}
						}
					}
					
					
					
				} else if(selectorType == GradeConstraintData.SELECTION_PIT_GROUP) {
					PitGroup pg = projectConfiguration.getPitGroupfromName(gradeConstraintData.getSelectorName());
					Set pitNumbers = getPitsFromPitGroup(pg);
					for( Process p: processList){
						String processName = p.getModel().getName();
						List<String> coefficients = processExprMap.get(processName);
						List<Grade> grades = processGradeMap.get(processName);
						List<Block> blocks = new ArrayList<Block>();
						for(Block b: p.getBlocks()){
							if(pitNumbers.contains(b.getPitNo())){
								blocks.add(b);
							}
						}
						for(Grade grade: grades){
							eq += buildGradeConstraintVariables(p.getProcessNo(), coefficients, grade, blocks, i, targetGrade);
						}
					}
				} else {
					for( Process p: processList){
						String processName = p.getModel().getName();
						List<String> coefficients = processExprMap.get(processName);
						List<Grade> grades = processGradeMap.get(processName);
						for(Grade grade: grades){
							eq += buildGradeConstraintVariables(p.getProcessNo(), coefficients, grade, p.getBlocks(), i, targetGrade);
						}
					}
				}
				
				if(eq.length() > 0) {
					eq = eq.substring(1);
					if(!gradeConstraintData.isMax()){
						eq = eq + " <= 0 ";
					} else {
						eq = eq + " >= 0 ";
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
	
	public String buildGradeConstraintVariables(int processNumber, List<String> coefficients, Grade grade, List<Block> blocks, int period, float targetGrade) {
		
		String eq = "";
		for(Block block: blocks){		
			float processRatio = 0;
			for(String coefficient: coefficients){
				String expressionName = coefficient.replaceAll("\\s+","_");
				processRatio += block.getRatioField(expressionName);					
			}

			if(processRatio == 0) continue;
			if(grade.getExpression() == null) continue;
			String gradeExpr = grade.getExpression().getName().replaceAll("\\s+","_");
			float bloackGrade = block.getRatioField(gradeExpr);
			if(targetGrade > bloackGrade) {
				eq +=  "+ ";
			} 
			eq +=   processRatio*(targetGrade-bloackGrade)+"p"+block.getPitNo()+"x"+block.getBlockNo()+"p"+processNumber+"t"+period;
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
