package com.org.gnos.scheduler.equation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.org.gnos.core.Block;
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.db.model.Expression;

public class SlidingWindowExecutionContext extends ExecutionContext {

	private static final double TONNAGE_TOLERANCE = 0.01;
	private short period;
	private short window;
	private short stepsize;
	private short currPeriod;

	private Map<Integer, List<String>> spblockVariableMapping = new HashMap<Integer, List<String>>();
	
	private Map<Integer, Double> blockMinedTonnesMapping = new HashMap<Integer, Double>();
	private Map<Integer, Map<Integer, Double>> spTonnesWigthMapping = new HashMap<Integer,  Map<Integer, Double>>();
	
	private Map<Integer, SPBlock> spBlockMapping = new HashMap<Integer, SPBlock>();
	
	private List<Expression> gradeExpressions;
	private Set<String> gradeFields;
	
	public SlidingWindowExecutionContext() {
		super();
		parseGradeExpression();
	}
	public short getPeriod() {
		return period;
	}
	public void setPeriod(short period) {
		this.period = period;
	}
	public short getWindow() {
		return window;
	}
	public void setWindow(short window) {
		this.window = window;
	}
	public short getStepsize() {
		return stepsize;
	}
	public void setStepsize(short stepsize) {
		this.stepsize = stepsize;
	}
	public short getCurrPeriod() {
		return currPeriod;
	}
	public void setCurrPeriod(short currPeriod) {
		this.currPeriod = currPeriod;
	}
	
	public int getStartYear() {
		return ScenarioConfigutration.getInstance().getStartYear();
	}
	
	public int getTimePeriod() {
		return this.window;
	}
	
	private boolean isGradeExpression(String name) {
		for(Expression expr: gradeExpressions){
			if(expr.getName().equals(name)){
				return true;
			}
		}		
		return false;
	}

	public Map<Integer, SPBlock> getSpBlockMapping() {
		return spBlockMapping;
	}
	public void setSpBlockMapping(Map<Integer, SPBlock> spBlockMapping) {
		this.spBlockMapping = spBlockMapping;
	}
	public BigDecimal getExpressionValueforBlock(SPBlock spb, Expression expr) {
		String expressionName = expr.getName().replaceAll("\\s+","_");			
		return spb.getComputedField(expressionName);		
	}
	
	public BigDecimal getExpressionValueforBlock(SPBlock spb, String exprName) {
		String expressionName = exprName.replaceAll("\\s+","_");			
		return spb.getComputedField(expressionName);		
	}
	@Override
	public boolean isGlobalMode() {
		return false;
	}
	
	
	@Override
	public boolean hasRemainingTonnage(Block b) {
		
		return (getTonnesWtForBlock(b) > TONNAGE_TOLERANCE); //0.01 is the tolerance
	}
	
	@Override
	public double getTonnesWtForBlock(Block b){
		double tonnage = Double.valueOf(b.getField(tonnesWtFieldName));
		double remainingTOnnage = tonnage - getMinedTonnesForBlock(b.getBlockNo());
		if(remainingTOnnage < TONNAGE_TOLERANCE) return 0;
		return remainingTOnnage;
	}
	
	public Map<Integer, List<String>> getSPBlockVariableMapping() {
		return spblockVariableMapping;
	}

	public void addVariable(int spNo, String variable){
		List<String> variables = spblockVariableMapping.get(spNo);
		if(variables == null){
			variables = new ArrayList<String>();
			spblockVariableMapping.put(spNo, variables);
		}
		variables.add(variable);
	}
	
	public void addMinedTonnesWeightForBlock(int blockNo, double tonnesWeight) {
		Double minedTonnesW = blockMinedTonnesMapping.get(blockNo);
		if(minedTonnesW == null){
			blockMinedTonnesMapping.put(blockNo, tonnesWeight);
		} else {
			blockMinedTonnesMapping.put(blockNo, minedTonnesW+tonnesWeight);
		}
	}
	
	public double getMinedTonnesForBlock(int blockNo) {
		Double minedTonnesW = blockMinedTonnesMapping.get(blockNo);
		if(minedTonnesW == null) return 0;
		return minedTonnesW;
	}
	
	public void addTonnesWeightForStockpile(int spNo, int blockNo, double tonnesWeight) {
		Map<Integer, Double> storedTonnesW = spTonnesWigthMapping.get(spNo);
		if(storedTonnesW == null){
			storedTonnesW = new HashMap<Integer, Double>();
			storedTonnesW.put(blockNo, tonnesWeight);
			spTonnesWigthMapping.put(spNo, storedTonnesW);
		} else {
			Double tonnesWt = storedTonnesW.get(blockNo);
			if(tonnesWt == null) {
				tonnesWt = tonnesWeight;
			} else {
				tonnesWt += tonnesWeight;
			}
			storedTonnesW.put(blockNo, tonnesWt);
		}
		
		SPBlock spb = spBlockMapping.get(spNo);
		if(spb == null) {
			spb = new SPBlock();
			spBlockMapping.put(spNo, spb);
		}
		spb.tonnesWt += tonnesWeight;
	}
	
	public void reclaimTonnesWeightForStockpile(int spNo, double tonnesWeight) {
		SPBlock spb = spBlockMapping.get(spNo);
		if(spb != null) {
			spb.tonnesWt -= tonnesWeight;
			spb.reclaimedTonnesWt += tonnesWeight;
			if(spb.reclaimedTonnesWt == spb.lasttonnesWt){
				spb.reset();
			}
		}		
	}
	
	public double getRemainingTonnesForSp(int spNo) {
		SPBlock spb = spBlockMapping.get(spNo);
		if(spb == null) return 0;
		return spb.tonnesWt;
	}
	
	public int getSPBlockPayload(int spNo) {
		SPBlock spb = spBlockMapping.get(spNo);		
		if(spb == null) return 0;
		return spb.getPayload();
	}
	
	public SPBlock getSPBlock(int spNo) {
		return spBlockMapping.get(spNo);
	}
	
	public void processStockpiles() {
		Set<Integer> spNos = spTonnesWigthMapping.keySet();
		for(int spNo: spNos){
			SPBlock spb = spBlockMapping.get(spNo);
			if(spb == null) continue;
			Map<Integer, Double> blockTonnage = spTonnesWigthMapping.get(spNo);			
			if(blockTonnage == null || spb.getLasttonnesWt() == spb.getTonnesWt()) continue;
			balanceStockpileBlock(spb);
			Set<Integer> blockNos = blockTonnage.keySet();
			Set<String> gradeExprs = new HashSet<String>();
			for(int blockNo: blockNos){
				Block b = getBlocks().get(blockNo);
				int payload = getBlockPayloadMapping().get(b.getId());
				spb.setPayload(payload);
				double blockTonnesWt = blockTonnage.get(blockNo);
				double ratio = blockTonnesWt/spb.getTonnesWt();
				Map<String, BigDecimal> spblockcomputedFields = spb.getComputedFields();
				Map<String, String> computedFields = b.getComputedFields();
				Set<String> fieldNames = computedFields.keySet();
				for(String fieldName: fieldNames) {
					if(isGradeExpression(fieldName)) {
						gradeExprs.add(fieldName);
					} else {
						BigDecimal fieldVal = new BigDecimal(computedFields.get(fieldName));
						fieldVal = fieldVal.multiply(new BigDecimal(ratio));
						BigDecimal existingVal = spblockcomputedFields.get(fieldName);
						if(existingVal != null) {
							fieldVal = fieldVal.add(existingVal);
						}
						spblockcomputedFields.put(fieldName, fieldVal);
					}
					
				}
				// calculate grade fields
				Map<String, BigDecimal> spbgradeFields = spb.getGradeFields();
				double totalBlockTonnage = Double.valueOf(b.getField(tonnesWtFieldName));
				double blockTonnageRatio = blockTonnesWt/totalBlockTonnage;
				for(String fieldName: gradeFields ){
					BigDecimal gradeVal = new BigDecimal(b.getField(fieldName));
					gradeVal = gradeVal.multiply(new BigDecimal(blockTonnageRatio));
					BigDecimal spbGradeVal = spbgradeFields.get(fieldName);
					if(spbGradeVal != null){
						spbGradeVal = spbGradeVal.add(gradeVal);
					} else {
						spbGradeVal = gradeVal;
					}
					spbgradeFields.put(fieldName, spbGradeVal);
					//System.out.format("Sp No: %s, block No: %s, fieldName: %s, value: %s \n", spNo, blockNo, fieldName, gradeVal);
				}
				spb.setComputedFields(spblockcomputedFields);
				spb.getProcesses().addAll(b.getProcesses());
			}
			processGrades(spb, gradeExprs);
			spb.setLasttonnesWt(spb.getTonnesWt());
			spb.setReclaimedTonnesWt(0);
		}
		
		spTonnesWigthMapping = new HashMap<Integer,  Map<Integer, Double>>();
	}
	
	private void balanceStockpileBlock(SPBlock spb) {
		double spbratio = (spb.getLasttonnesWt() - spb.getReclaimedTonnesWt())/spb.getTonnesWt();
		double spbremainingratio = (spb.getLasttonnesWt() - spb.getReclaimedTonnesWt())/spb.getLasttonnesWt();
		Map<String, BigDecimal> spbcomputedFields = spb.getComputedFields();
		Map<String, BigDecimal> gradeFields = spb.getGradeFields();
		Set<String> computedfieldNames = spbcomputedFields.keySet();
		Set<String> gradefieldNames = gradeFields.keySet();
		for(String fieldName: computedfieldNames) {
			BigDecimal fieldVal = spbcomputedFields.get(fieldName);
			if(fieldVal != null) {
				fieldVal = fieldVal.multiply(new BigDecimal(spbratio));
				spbcomputedFields.put(fieldName, fieldVal);
			}		
		}
		for(String fieldName: gradefieldNames) {
			BigDecimal fieldVal = gradeFields.get(fieldName);
			if(fieldVal != null) {
				fieldVal = fieldVal.multiply(new BigDecimal(spbremainingratio));
				spbcomputedFields.put(fieldName, fieldVal);
			}
			
		}
	}
	private void processGrades(SPBlock spb, Set<String> gradeExprs) {
		for(String exprName: gradeExprs) {
			Expression expr = ProjectConfigutration.getInstance().getExpressionByName(exprName);
			String exprValue = expr.getExprvalue();
			if(expr.isComplex()) {
				String[] arr = exprValue.split("[+-/*]");
				String f1 = arr[0].trim();
				String f2 = arr[1].trim();
				BigDecimal value = new BigDecimal(0);
				if(exprValue.indexOf("/")!= -1){
					value = new BigDecimal(spb.getGradeField(f1).doubleValue()/spb.getGradeField(f2).doubleValue());
				} else if(exprValue.indexOf("*")!= -1){
					value = spb.getGradeField(f1).multiply(spb.getGradeField(f2));
				} else if(exprValue.indexOf("+")!= -1){
					value = spb.getGradeField(f1).add(spb.getGradeField(f2));
				} else if(exprValue.indexOf("1")!= -1){
					value = spb.getGradeField(f1).subtract(spb.getGradeField(f2));
				}
				spb.getComputedFields().put(exprName, value);
				//System.out.println("TestSP: exprName: "+exprName+" f1:"+spb.getGradeField(f1)+" f2:"+spb.getGradeField(f2)+" value:"+value);
			} else {
				spb.getComputedFields().put(exprName, spb.getGradeField(exprValue.trim()));
			}		
		}		
	}
	
	private Set<String> parseExpressionValue(String exprStr) {
		Set<String> fields = new HashSet<String>();
		if(exprStr != null) {
			String[] arr = exprStr.split("[+-/*]");
			if(arr != null){
				for(int i=0; i< arr.length;i++ ){
					fields.add(arr[i].trim());
				}			
			}
		}
		return fields;
	}
	private void parseGradeExpression() {
		gradeExpressions = new ArrayList<Expression>();
		gradeFields = new HashSet<String>();
		List<Expression> expressions = ProjectConfigutration.getInstance().getExpressions();		
		for(Expression expression: expressions) {
			if(expression.isGrade()){
				gradeExpressions.add(expression);
				if(expression.isComplex()) {
					gradeFields.addAll(parseExpressionValue(expression.getExprvalue()));
				} else {
					gradeFields.add(expression.getExprvalue());
				}
			}
		}
	}
}
