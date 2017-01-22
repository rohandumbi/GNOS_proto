package com.org.gnos.scheduler.equation.generator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Block;
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
import com.org.gnos.db.model.Stockpile;
import com.org.gnos.scheduler.equation.ExecutionContext;
import com.org.gnos.scheduler.equation.SPBlock;
import com.org.gnos.scheduler.equation.SlidingWindowExecutionContext;

public class GradeConstraintEquationGenerator extends EquationGenerator{

	private List<GradeConstraintData> gradeConstraintDataList;
	
	public GradeConstraintEquationGenerator(ExecutionContext data) {
		super(data);
	}
	
	@Override
	public void generate() {
		gradeConstraintDataList = context.getScenarioConfig().getGradeConstraintDataList();
		try {
			buildGradeConstraintVariables();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public void buildGradeConstraintVariables() {
		
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		int startYear = context.getStartYear();
		List<Process> processList = context.getProjectConfig().getProcessList();
		for(GradeConstraintData gradeConstraintData: gradeConstraintDataList) {
			if(!gradeConstraintData.isInUse()) continue;
			int selectorType = gradeConstraintData.getSelectionType();
			String selectedGradeName = gradeConstraintData.getSelectedGradeName();
			Map<String, List<String>> processExprMap = new HashMap<String, List<String>>();
			Map<String, List<Grade>> processGradeMap = new HashMap<String, List<Grade>>();
			ProductJoin pj = context.getProjectConfig().getProductJoinByName(gradeConstraintData.getProductJoinName());
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
			
			for(int i=timePeriodStart; i<= timePeriodEnd; i++){
				String eq = "";
				BigDecimal targetGrade = new BigDecimal(gradeConstraintData.getConstraintData().get(startYear+i -1));
				if(selectorType == GradeConstraintData.SELECTION_PROCESS_JOIN) {
					ProcessJoin processJoin = context.getProjectConfig().getProcessJoinByName(gradeConstraintData.getSelectorName());
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
					Pit pit = context.getProjectConfig().getPitfromPitName(pitName);
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
					PitGroup pg = context.getProjectConfig().getPitGroupfromName(gradeConstraintData.getSelectorName());
					Set<Integer> pitNumbers = getPitsFromPitGroup(pg);
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

	
	public String buildGradeConstraintVariables(int processNumber, List<String> coefficients, Grade grade, List<Block> blocks, int period, BigDecimal targetGrade) {
		
		String eq = "";
		for(Block block: blocks){		
			BigDecimal processRatio = new BigDecimal(0);
			for(String coefficient: coefficients){
				processRatio =  processRatio.add(context.getExpressionValueforBlock(block, coefficient));					
			}

			if(processRatio.compareTo(new BigDecimal(0)) == 0) continue;
			BigDecimal blockGrade = context.getExpressionValueforBlock(block, grade.getExpression());
			BigDecimal coeff = processRatio.multiply(targetGrade.subtract(blockGrade));
			if(coeff.doubleValue() == 0) continue;
			if(targetGrade.compareTo(blockGrade) == 1) {
				eq +=  "+ ";
			}
			eq +=   formatDecimalValue(coeff)+"p"+block.getPitNo()+"x"+block.getBlockNo()+"p"+processNumber+"t"+period;
			
			if(context.isSpReclaimEnabled() && period > 1 && context.isGlobalMode()) {
				int stockpileNo = getStockpileNo(block);
				if(stockpileNo > 0) {
					if(coeff.doubleValue() > 0) {
						eq +=   "+ ";
					}
					eq +=  formatDecimalValue(coeff)+"sp"+stockpileNo+"x"+block.getBlockNo()+"p"+processNumber+"t"+period;
				}			
			}
		}
		
		if(context.isSpReclaimEnabled() && period > 1 && !context.isGlobalMode()) {			
			
			SlidingWindowExecutionContext swctx = (SlidingWindowExecutionContext) context;
			Map<Integer, SPBlock> spBlockMapping = swctx.getSpBlockMapping();
			Set<Integer> spNos = spBlockMapping.keySet();			
			for(int spNo: spNos){
				SPBlock spb = spBlockMapping.get(spNo);
				if(spb == null) continue;
				
				BigDecimal processRatio = new BigDecimal(0);
				for(String coefficient: coefficients){
					processRatio =  processRatio.add(swctx.getExpressionValueforBlock(spb, coefficient));					
				}
				
				if(processRatio.compareTo(new BigDecimal(0)) == 0) continue;
				BigDecimal blockGrade = swctx.getExpressionValueforBlock(spb, grade.getExpression());
				BigDecimal coeff = processRatio.multiply(targetGrade.subtract(blockGrade));
				if(coeff.doubleValue() == 0) continue;

				Set<Process> processes = spb.getProcesses();
				for(Process process: processes){
					if(process.getProcessNo() == processNumber) {
						if(coeff.doubleValue() > 0) {
							eq +=   "+ ";
						}
						eq +=  formatDecimalValue(coeff)+"sp"+spNo+"x0p"+processNumber+"t"+period;
					}
				}
			}
		}
		return eq;
	}
	
	private int getStockpileNo(Block b){
		List<Stockpile> stockpiles = context.getProjectConfig().getStockPileList();
		
		for(Stockpile sp: stockpiles){
			if(!sp.isReclaim()) continue;
			Set<Block> blocks = sp.getBlocks();
			for(Block block: blocks){
				if(block.getBlockNo() == b.getBlockNo()){
					return sp.getStockpileNumber();
				}
			}
		}
		
		return -1;
	}
}
