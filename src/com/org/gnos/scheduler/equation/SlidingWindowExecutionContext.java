package com.org.gnos.scheduler.equation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.org.gnos.core.Block;
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.db.model.Expression;

public class SlidingWindowExecutionContext extends ExecutionContext {

	private short period;
	private short window;
	private short stepsize;
	private short currPeriod;
	
	private Map<Integer, Double> blockMinedTonnesMapping = new HashMap<Integer, Double>();
	private Map<Integer, Double> spTonnesWigthMapping = new HashMap<Integer, Double>();
	
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
	
	@Override
	public BigDecimal getExpressionValueforBlock(Block b, Expression expr) {
		String expressionName = expr.getName().replaceAll("\\s+","_");
		if(expr.isGrade()) {
			return b.getComputedField(expressionName);		
		} else {
			double totalTonnes =  Double.valueOf(b.getField(getTonnesWeightAlisName()));
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
	
	@Override
	public boolean isGlobalMode() {
		return false;
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
	
	public void addTonnesWeightForStockpile(int spNo, double tonnesWeight) {
		Double storedTonnesW = spTonnesWigthMapping.get(spNo);
		if(storedTonnesW == null){
			spTonnesWigthMapping.put(spNo, tonnesWeight);
		} else {
			spTonnesWigthMapping.put(spNo, storedTonnesW+tonnesWeight);
		}
	}
	
	public void reclaimTonnesWeightForStockpile(int spNo, double tonnesWeight) {
		Double storedTonnesW = spTonnesWigthMapping.get(spNo);
		if(storedTonnesW != null){
			spTonnesWigthMapping.put(spNo, storedTonnesW-tonnesWeight);
		}
	}
	
	
	public double getRemainingTonnesForSp(int spNo) {
		Double storedTonnesW = spTonnesWigthMapping.get(spNo);
		if(storedTonnesW == null) return 0;
		return storedTonnesW;
	}
	
	
}
