package com.org.gnos.equation;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Block;
import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.Pit;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Process;
import com.org.gnos.db.model.ProcessConstraintData;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.db.model.Product;
import com.org.gnos.db.model.ProductJoin;
import com.org.gnos.db.model.Stockpile;

public class ProcessConstraintEquationGenerator extends EquationGenerator{

	private List<ProcessConstraintData> processConstraintDataList;

	public ProcessConstraintEquationGenerator(InstanceData data) {
		super(data);
	}
	
	@Override
	public void generate() {
		projectConfiguration = ProjectConfigutration.getInstance();
		scenarioConfigutration = ScenarioConfigutration.getInstance();
		processConstraintDataList = scenarioConfigutration.getProcessConstraintDataList();
		
		int bufferSize = 8 * 1024;
		try {
			output = new BufferedOutputStream(new FileOutputStream("processConstraint.txt"), bufferSize);
			buildProcessConstraintVariables();
			output.flush();
			output.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public void buildProcessConstraintVariables() {
		
		int timePeriod = scenarioConfigutration.getTimePeriod();
		int startYear = scenarioConfigutration.getStartYear();
		List<Process> processList = projectConfiguration.getProcessList();
		for(ProcessConstraintData processConstraintData: processConstraintDataList) {
			if(!processConstraintData.isInUse()) continue;
			int selectorType = processConstraintData.getSelectionType();
			int coefficientType = processConstraintData.getCoefficientType();
			Map<String, List<String>> processExprMap = new HashMap<String, List<String>>();
			boolean applyProcessRestrictions = false;
			boolean usetruckHourCoeffcient = false;
			if(coefficientType == ProcessConstraintData.COEFFICIENT_PRODUCT) {
				Product p = projectConfiguration.getProductByName(processConstraintData.getCoefficient_name());
				if(p != null){
					List<String> coefficients = new ArrayList<String>();
					for(Expression e : p.getListOfExpressions()){
						coefficients.add(e.getName());
					}
					processExprMap.put(p.getAssociatedProcess().getName(), coefficients);
					applyProcessRestrictions = true;
				}
			} else if(coefficientType == ProcessConstraintData.COEFFICIENT_PRODUCT_JOIN) {
				ProductJoin pj = projectConfiguration.getProductJoinByName(processConstraintData.getCoefficient_name());
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
				}
				applyProcessRestrictions = true;
			} else if( coefficientType == ProcessConstraintData.COEFFICIENT_TRUCK_HOUR ) {
				usetruckHourCoeffcient = true;
			}
			
			for(int i=1; i<= timePeriod; i++){
				String eq = "";
				if(selectorType == ProcessConstraintData.SELECTION_PROCESS_JOIN) {
					ProcessJoin processJoin = projectConfiguration.getProcessJoinByName(processConstraintData.getSelector_name());
					if(processJoin != null) {
						for(Model model: processJoin.getlistChildProcesses()){
							for( Process p: processList){
								if(p.getModel().getName().equals(model.getName())){
									List<String> coefficients;
									if(applyProcessRestrictions){
										coefficients = processExprMap.get(p.getModel().getName());
										if(coefficients == null || coefficients.size() == 0) {
											continue;
										}
									} else {
										coefficients = new ArrayList<>();
										coefficients.add(processConstraintData.getCoefficient_name());
									}
									eq += buildProcessConstraintVariables(p, coefficients, usetruckHourCoeffcient, p.getBlocks(), i);
									break;
								}
							}
						}
					}
				}else if(selectorType == ProcessConstraintData.SELECTION_PROCESS) {
					for( Process p: processList){
						if(p.getModel().getName().equals(processConstraintData.getSelector_name())){
							List<String> coefficients;
							if(applyProcessRestrictions){
								coefficients = processExprMap.get(p.getModel().getName());
								if(coefficients == null || coefficients.size() == 0) {
									continue;
								}
							} else {
								coefficients = new ArrayList<>();
								coefficients.add(processConstraintData.getCoefficient_name());
							}
							eq += buildProcessConstraintVariables(p, coefficients, usetruckHourCoeffcient, p.getBlocks(), i);
							break;
						}
					}
				} else if(selectorType == ProcessConstraintData.SELECTION_PIT) {
					String pitName = processConstraintData.getSelector_name();
					Pit pit = projectConfiguration.getPitfromPitName(pitName);
					if(pit != null) {
						for( Process p: processList){
							List<String> coefficients;
							if(applyProcessRestrictions){
								coefficients = processExprMap.get(p.getModel().getName());
								if(coefficients == null || coefficients.size() == 0) {
									continue;
								}
							} else {
								coefficients = new ArrayList<>();
								coefficients.add(processConstraintData.getCoefficient_name());
							}
							List<Block> blocks = new ArrayList<Block>();
							for(Block b: p.getBlocks()){
								if(b.getPitNo() == pit.getPitNumber()){
									blocks.add(b);
								}
							}
							eq += buildProcessConstraintVariables(p, coefficients, usetruckHourCoeffcient, blocks, i);
						}
						if(!applyProcessRestrictions) {
							List<String> coefficients = new ArrayList<>();
							coefficients.add(processConstraintData.getCoefficient_name());
							List<Block> blocks = new ArrayList<Block>();
							for(Block b: serviceInstanceData.getProcessBlocks()){
								if(pit.getPitNumber() != b.getPitNo()) continue;
								blocks.add(b);								
							}
							eq += buildStockpileConstraintVariables(coefficients,usetruckHourCoeffcient, blocks, i);
							blocks = new ArrayList<Block>();
							for(Block b: serviceInstanceData.getWasteBlocks()){
								if(pit.getPitNumber() != b.getPitNo()) continue;
								blocks.add(b);			
							}
							eq += buildWasteConstraintVariables(coefficients, usetruckHourCoeffcient, blocks, i);
						}
						
					}
					
					
					
				} else if(selectorType == ProcessConstraintData.SELECTION_PIT_GROUP) {
					PitGroup pg = projectConfiguration.getPitGroupfromName(processConstraintData.getSelector_name());
					Set pitNumbers = getPitsFromPitGroup(pg);
					for( Process p: processList){
						List<String> coefficients;
						if(applyProcessRestrictions){
							coefficients = processExprMap.get(p.getModel().getName());
							if(coefficients == null || coefficients.size() == 0) {
								continue;
							}
						} else {
							coefficients = new ArrayList<>();
							coefficients.add(processConstraintData.getCoefficient_name());
						}
						List<Block> blocks = new ArrayList<Block>();
						for(Block b: p.getBlocks()){
							if(pitNumbers.contains(b.getPitNo())){
								blocks.add(b);
							}
						}
						eq += buildProcessConstraintVariables(p, coefficients, usetruckHourCoeffcient, blocks, i);
					}
					if(!applyProcessRestrictions) {
						List<String> coefficients = new ArrayList<>();
						coefficients.add(processConstraintData.getCoefficient_name());
						List<Block> blocks = new ArrayList<Block>();
						for(Block b: serviceInstanceData.getProcessBlocks()){
							if(!pitNumbers.contains(b.getPitNo())) continue;
							blocks.add(b);								
						}
						eq += buildStockpileConstraintVariables(coefficients, usetruckHourCoeffcient, blocks, i);
						blocks = new ArrayList<Block>();
						for(Block b: serviceInstanceData.getWasteBlocks()){
							if(!pitNumbers.contains(b.getPitNo())) continue;
							blocks.add(b);			
						}
						eq += buildWasteConstraintVariables(coefficients, usetruckHourCoeffcient, blocks, i);
					}
					
				} else {
					for( Process p: processList){
						List<String> coefficients;
						if(applyProcessRestrictions){
							coefficients = processExprMap.get(p.getModel().getName());
							if(coefficients == null || coefficients.size() == 0) {
								continue;
							}
						} else {
							coefficients = new ArrayList<>();
							coefficients.add(processConstraintData.getCoefficient_name());
						}
						eq += buildProcessConstraintVariables(p, coefficients, usetruckHourCoeffcient, p.getBlocks(), i);
					}
					if(!applyProcessRestrictions) {
						List<String> coefficients = new ArrayList<>();
						coefficients.add(processConstraintData.getCoefficient_name());
						List<Block> blocks = new ArrayList<Block>();
						blocks.addAll(serviceInstanceData.getProcessBlocks());
						eq += buildStockpileConstraintVariables(coefficients, usetruckHourCoeffcient, blocks, i);
						blocks = new ArrayList<Block>();
						blocks.addAll(serviceInstanceData.getWasteBlocks());
						eq += buildWasteConstraintVariables(coefficients, usetruckHourCoeffcient, blocks, i);
					}
				}
				
				if(eq.length() > 0) {
					eq = eq.substring(1);
					if(processConstraintData.isMax()){
						eq = eq + " <= " +processConstraintData.getConstraintData().get(startYear+i -1);
					} else {
						eq = eq + " >= " +processConstraintData.getConstraintData().get(startYear+i -1);
					}
					write(eq);
				}
			}
			//
		}
		
	}

	private String buildProcessConstraintVariables(Process p, List<String> coefficients, boolean useTruckHour, List<Block> blocks, int period) {
		
		String eq = "";
		int processNumber = p.getProcessNo();
		for(Block block: blocks){			
			String variable = "p"+block.getPitNo()+"x"+block.getBlockNo()+"p"+processNumber+"t"+period;
			BigDecimal coefficientRatio =  new BigDecimal(0);
			if(useTruckHour) {
				coefficientRatio = getTruckHourRatio(block, p.getModel().getName());
			} else {
				for(String coefficient: coefficients){
					String expressionName = coefficient.replaceAll("\\s+","_");
					coefficientRatio = coefficientRatio.add(block.getComputedField(expressionName));					
				}
			}
	
			if(coefficientRatio.doubleValue() == 0) continue;
					
			eq +=  "+ "+ formatDecimalValue(coefficientRatio)+ variable;
		}			
		return eq;
	}
	
	private String buildStockpileConstraintVariables(List<String> coefficients, boolean useTruckHour, List<Block> blocks, int period) {
		
		String eq = "";
		for(Block block: blocks){
			Stockpile sp = this.serviceInstanceData.getPitStockpileMapping().get(block.getPitNo());
			if(sp == null) continue;
			String variable ="p"+block.getPitNo()+"x"+block.getBlockNo()+"s"+sp.getStockpileNumber()+"t"+period;
			BigDecimal coefficientRatio = new BigDecimal(0);
			if(useTruckHour) {
				coefficientRatio = getTruckHourRatio(block, sp.getName());
			} else {
				for(String coefficient: coefficients){
					String expressionName = coefficient.replaceAll("\\s+","_");
					coefficientRatio = coefficientRatio.add(block.getComputedField(expressionName));					
				}
			}
			
			if(coefficientRatio.doubleValue() == 0) continue;
			
			eq +=  "+ "+formatDecimalValue(coefficientRatio)+ variable;
		}			
		return eq;
	}

	private String buildWasteConstraintVariables(List<String> coefficients, boolean useTruckHour, List<Block> blocks, int period) {
	
		String eq = "";
		for(Block block: blocks){
			BigDecimal coefficientRatio = new BigDecimal(0);
			if(!useTruckHour) {
				for(String coefficient: coefficients){
					String expressionName = coefficient.replaceAll("\\s+","_");
					coefficientRatio = coefficientRatio.add(block.getComputedField(expressionName));					
				}
			}
			
			List<Dump> dumps = this.serviceInstanceData.getPitDumpMapping().get(block.getPitNo());
			if(dumps == null) continue;
			for(Dump dump: dumps){
				if(useTruckHour) {
					coefficientRatio = getTruckHourRatio(block, dump.getName());
				}
				if(coefficientRatio.doubleValue() == 0) continue;
				eq += "+ "+formatDecimalValue(coefficientRatio)+"p"+block.getPitNo()+"x"+block.getBlockNo()+"w"+dump.getDumpNumber()+"t"+period;
			}
		}			
		return eq;
	}
	
	private BigDecimal getTruckHourRatio(Block b, String contextName){
		BigDecimal th_ratio = new BigDecimal(0);
		int payload = serviceInstanceData.getBlockPayloadMapping().get(b.getId());
		if(payload > 0) {
			Integer ct = serviceInstanceData.getCycleTimeDataMapping().get(b.getPitNo()+":"+b.getBenchNo()+":"+contextName);
			if(ct != null) {
				double th_ratio_val =  (double)ct /( payload* 60);
				th_ratio = new BigDecimal(th_ratio_val);
			}
		}
		
		return th_ratio;
		
	}
}
