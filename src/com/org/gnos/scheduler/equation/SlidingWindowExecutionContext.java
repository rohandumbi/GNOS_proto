package com.org.gnos.scheduler.equation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Block;
import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.db.model.Expression;

public class SlidingWindowExecutionContext extends ExecutionContext {

	private short period;
	private short window;
	private short stepsize;
	private short currPeriod;
	
	private Map<Integer, List<String>> spblockVariableMapping = new HashMap<Integer, List<String>>();
	
	private Map<Integer, Double> blockMinedTonnesMapping = new HashMap<Integer, Double>();
	private Map<Integer, Map<Integer, Double>> spTonnesWigthMapping = new HashMap<Integer,  Map<Integer, Double>>();
	
	private Map<Integer, SPBlock> spBlockMapping = new HashMap<Integer, SPBlock>();
	
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
/*	
	@Override
	public BigDecimal getExpressionValueforBlock(Block b, Expression expr) {
		String expressionName = expr.getName().replaceAll("\\s+","_");
		if(expr.isGrade()) {
			return b.getComputedField(expressionName);		
		} else {
			double totalTonnes =  Double.valueOf(b.getField(tonnesWtFieldName));
			double remainingTonnes = totalTonnes - getMinedTonnesForBlock(b.getBlockNo());
			
			if(remainingTonnes <= 0) return new BigDecimal(0);
			double ratio = totalTonnes/remainingTonnes;

			return b.getComputedField(expressionName).multiply(new BigDecimal(ratio));
		}
		
	}
	
	@Override
	public BigDecimal getExpressionValueforBlock(Block b, String exprName) {
		String expressionName = exprName.replaceAll("\\s+","_");
		Expression expr = ProjectConfigutration.getInstance().getExpressionByName(expressionName);
		return getExpressionValueforBlock(b, expr);		
	}
	*/

	public BigDecimal getExpressionValueforBlock(SPBlock spb, Expression expr) {
		String expressionName = expr.getName().replaceAll("\\s+","_");			
		return spb.getComputedField(expressionName);		
	}
	
	@Override
	public boolean isGlobalMode() {
		return false;
	}
	
	
	@Override
	public boolean hasRemainingTonnage(Block b) {
		
		return (getTonnesWtForBlock(b) > 0.01); //0.01 is the tolerance
	}
	
	@Override
	public double getTonnesWtForBlock(Block b){
		double tonnage = Double.valueOf(b.getField(tonnesWtFieldName));
		double remainingTOnnage = tonnage - getMinedTonnesForBlock(b.getBlockNo());
		
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
			
		}
		
		spTonnesWigthMapping = new HashMap<Integer,  Map<Integer, Double>>();
	}
}
