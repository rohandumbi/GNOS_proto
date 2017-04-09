package com.org.gnos.scheduler.equation.generator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Block;
import com.org.gnos.core.Pit;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Field;
import com.org.gnos.db.model.Grade;
import com.org.gnos.db.model.GradeConstraintData;
import com.org.gnos.db.model.Model;
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
		gradeConstraintDataList = context.getGradeConstraintDataList();
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
		List<Process> processList = context.getProcessList();
		for(GradeConstraintData gradeConstraintData: gradeConstraintDataList) {
			if(!gradeConstraintData.isInUse()) continue;
			int selectorType = gradeConstraintData.getSelectionType();
			String selectedGradeName = gradeConstraintData.getSelectedGradeName();
			Map<String, List<Object>> processExprMap = new HashMap<String, List<Object>>();
			Map<String, List<Grade>> processGradeMap = new HashMap<String, List<Grade>>();
			ProductJoin pj = context.getProductJoinFromName(gradeConstraintData.getProductJoinName());
			if(pj == null) continue;		
			int selectedGradeIndex = -1;

			Set<String> productNames = getProductsFromProductJoin(pj);
			for(String productName: productNames){
				
				if(selectedGradeIndex == -1 ) {
					List<Grade> grades = context.getGradesForProduct(productName);
					int loopCount = 0;
					for(Grade grade: grades){
						if(grade.getName().equals(selectedGradeName)){
							selectedGradeIndex = loopCount;
							break;
						}			
						loopCount++;
					}
				}
				Product p = context.getProductFromName(productName);
				Integer processId = p.getModelId();
				String processName = context.getModelById(processId).getName();
				List<Object> coefficients = processExprMap.get(processName);
				if(coefficients == null){
					coefficients = new ArrayList<Object>();
					processExprMap.put(processName, coefficients);
				}
				for(Integer eid : p.getExpressionIdList()){
					Expression e = context.getExpressionById(eid);
					coefficients.add(e);
				}
				
				for(Integer fieldId : p.getFieldIdList()){
					Field f = context.getFieldById(fieldId);
					coefficients.add(f);
				}
				
				List<Grade> grades = processGradeMap.get(processName);
				if( grades == null ) {
					grades = new ArrayList<Grade>();
					processGradeMap.put(processName, grades);
				}
				grades.add(context.getGradesForProduct(productName).get(selectedGradeIndex));
			}
			
			for(int i=timePeriodStart; i<= timePeriodEnd; i++){
				String eq = "";
				BigDecimal targetGrade = new BigDecimal(gradeConstraintData.getConstraintData().get(startYear+i -1));
				if(selectorType == GradeConstraintData.SELECTION_PROCESS_JOIN) {
					ProcessJoin processJoin = context.getProcessJoinByName(gradeConstraintData.getSelectorName());
					if(processJoin != null) {
						for(Integer modelId: processJoin.getChildProcessList()){
							if(modelId == 0) continue;
							Model model = context.getModelById(modelId);
							for( Process p: processList){
								String processName = p.getModel().getName();
								if(processName.equals(model.getName())){
									List<Object> coefficients = processExprMap.get(processName);
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
							List<Object> coefficients = processExprMap.get(processName);
							List<Grade> grades = processGradeMap.get(processName);
							for(Grade grade: grades){
								eq += buildGradeConstraintVariables(p.getProcessNo(), coefficients, grade, p.getBlocks(), i, targetGrade);
							}
							break;
						}
					}
				} else if(selectorType == GradeConstraintData.SELECTION_PIT) {
					String pitName = gradeConstraintData.getSelectorName();
					Pit pit = context.getPitNameMap().get(pitName);
					if(pit != null) {
						for( Process p: processList){
							String processName = p.getModel().getName();
							List<Object> coefficients = processExprMap.get(processName);
							List<Grade> grades = processGradeMap.get(processName);
							List<Block> blocks = new ArrayList<Block>();
							for(Block b: p.getBlocks()){
								if(b.getPitNo() == pit.getPitNo()){
									blocks.add(b);
								}
							}
							for(Grade grade: grades){
								eq += buildGradeConstraintVariables(p.getProcessNo(), coefficients, grade, blocks, i, targetGrade);
							}
						}
					}
					
					
					
				} else if(selectorType == GradeConstraintData.SELECTION_PIT_GROUP) {
					PitGroup pg = context.getPitGroupfromName(gradeConstraintData.getSelectorName());
					Set<Integer> pitNumbers = getPitsFromPitGroup(pg);
					for( Process p: processList){
						String processName = p.getModel().getName();
						List<Object> coefficients = processExprMap.get(processName);
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
						List<Object> coefficients = processExprMap.get(processName);
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

	
	public String buildGradeConstraintVariables(int processNumber, List<Object> coefficients, Grade grade, List<Block> blocks, int period, BigDecimal targetGrade) {
		
		String eq = "";
		for(Block block: blocks){		
			BigDecimal processRatio = new BigDecimal(0);
			for(Object coefficient: coefficients){
				short unitType = 0;
				int unitId = 0;
				if(coefficient instanceof Expression) {
					unitType = Product.UNIT_EXPRESSION;
					unitId = ((Expression)coefficient).getId();
				} else {
					unitType = Product.UNIT_FIELD;
					unitId = ((Field)coefficient).getId();
				}
				
				processRatio =  processRatio.add(context.getUnitValueforBlock(block, unitId, unitType));					
			}

			if(processRatio.compareTo(new BigDecimal(0)) == 0) continue;
			BigDecimal blockGrade = context.getGradeValueforBlock(block, grade.getMappedName(), grade.getType());
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
				for(Object coefficient: coefficients){
					short unitType = 0;
					int unitId = 0;
					if(coefficient instanceof Expression) {
						unitType = Product.UNIT_EXPRESSION;
						unitId = ((Expression)coefficient).getId();
					} else {
						unitType = Product.UNIT_FIELD;
						unitId = ((Field)coefficient).getId();
					}
					processRatio =  processRatio.add(swctx.getUnitValueforBlock(spb, unitId, unitType));					
				}
				
				if(processRatio.compareTo(new BigDecimal(0)) == 0) continue;
				BigDecimal blockGrade = swctx.getGradeValueforBlock(spb, grade.getMappedName(), grade.getType());
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
		List<Stockpile> stockpiles = context.getStockpiles();
		
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
