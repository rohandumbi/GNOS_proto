package com.org.gnos.scheduler.equation.generator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Block;
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
import com.org.gnos.scheduler.equation.ExecutionContext;

public class ProcessConstraintEquationGenerator extends EquationGenerator{

	private List<ProcessConstraintData> processConstraintDataList;

	public ProcessConstraintEquationGenerator(ExecutionContext data) {
		super(data);
	}
	
	@Override
	public void generate() {
		processConstraintDataList = context.getScenarioConfig().getProcessConstraintDataList();
		try {			
			buildProcessConstraintVariables();
			output.flush();

		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public void buildProcessConstraintVariables() {
		
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		int startYear = context.getStartYear();
		List<Process> processList = context.getProjectConfig().getProcessList();
		for(ProcessConstraintData processConstraintData: processConstraintDataList) {
			if(!processConstraintData.isInUse()) continue;
			int selectorType = processConstraintData.getSelectionType();
			int coefficientType = processConstraintData.getCoefficientType();
			Map<String, List<String>> processExprMap = new HashMap<String, List<String>>();
			boolean applyProcessRestrictions = false;
			boolean usetruckHourCoeffcient = false;
			if(coefficientType == ProcessConstraintData.COEFFICIENT_PRODUCT) {
				Product p = context.getProjectConfig().getProductByName(processConstraintData.getCoefficient_name());
				if(p != null){
					List<String> coefficients = new ArrayList<String>();
					for(Expression e : p.getListOfExpressions()){
						coefficients.add(e.getName());
					}
					processExprMap.put(p.getAssociatedProcess().getName(), coefficients);
					applyProcessRestrictions = true;
				}
			} else if(coefficientType == ProcessConstraintData.COEFFICIENT_PRODUCT_JOIN) {
				ProductJoin pj = context.getProjectConfig().getProductJoinByName(processConstraintData.getCoefficient_name());
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
			
			for(int i=timePeriodStart; i<= timePeriodEnd; i++){
				String eq = "";
				if(selectorType == ProcessConstraintData.SELECTION_PROCESS_JOIN) {
					ProcessJoin processJoin = context.getProjectConfig().getProcessJoinByName(processConstraintData.getSelector_name());
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
					Pit pit = context.getProjectConfig().getPitfromPitName(pitName);
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
							for(Block b: context.getProcessBlocks()){
								if(pit.getPitNumber() != b.getPitNo()) continue;
								blocks.add(b);								
							}
							eq += buildStockpileConstraintVariables(coefficients,usetruckHourCoeffcient, blocks, i);
							blocks = new ArrayList<Block>();
							for(Block b: context.getWasteBlocks()){
								if(pit.getPitNumber() != b.getPitNo()) continue;
								blocks.add(b);			
							}
							eq += buildWasteConstraintVariables(coefficients, usetruckHourCoeffcient, blocks, i);
						}
						
					}
					
					
					
				} else if(selectorType == ProcessConstraintData.SELECTION_PIT_GROUP) {
					PitGroup pg = context.getProjectConfig().getPitGroupfromName(processConstraintData.getSelector_name());
					Set<Integer> pitNumbers = getPitsFromPitGroup(pg);
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
						for(Block b: context.getProcessBlocks()){
							if(!pitNumbers.contains(b.getPitNo())) continue;
							blocks.add(b);								
						}
						eq += buildStockpileConstraintVariables(coefficients, usetruckHourCoeffcient, blocks, i);
						blocks = new ArrayList<Block>();
						for(Block b: context.getWasteBlocks()){
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
						blocks.addAll(context.getProcessBlocks());
						eq += buildStockpileConstraintVariables(coefficients, usetruckHourCoeffcient, blocks, i);
						blocks = new ArrayList<Block>();
						blocks.addAll(context.getWasteBlocks());
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
					coefficientRatio = coefficientRatio.add(context.getExpressionValueforBlock(block, coefficient));					
				}
			}
	
			if(coefficientRatio.doubleValue() == 0) continue;
					
			eq +=  "+ "+ formatDecimalValue(coefficientRatio)+ variable;
			
			if(context.isSpReclaimEnabled() && period > 1) {
				int stockpileNo = getStockpileNo(block);
				if(stockpileNo > 0) {
					if(coefficientRatio.doubleValue() > 0) {
						eq +=   " + ";
					}
					eq +=  formatDecimalValue(coefficientRatio)+"sp"+stockpileNo+"x"+block.getBlockNo()+"p"+processNumber+"t"+period;
					
				}			
			}
		}			
		return eq;
	}
	
	private String buildStockpileConstraintVariables(List<String> coefficients, boolean useTruckHour, List<Block> blocks, int period) {
		
		String eq = "";
		for(Block block: blocks){
			Stockpile sp = getStockpile(block);
			if(sp == null) continue;
			String variable ="p"+block.getPitNo()+"x"+block.getBlockNo()+"s"+sp.getStockpileNumber()+"t"+period;
			BigDecimal coefficientRatio = new BigDecimal(0);
			if(useTruckHour) {
				coefficientRatio = getTruckHourRatio(block, sp.getName());
			} else {
				for(String coefficient: coefficients){
					coefficientRatio = coefficientRatio.add(context.getExpressionValueforBlock(block, coefficient));					
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
					coefficientRatio = coefficientRatio.add(context.getExpressionValueforBlock(block, coefficient));					
				}
			}
			
			List<Dump> dumps = getDump(block);
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
		int payload = context.getBlockPayloadMapping().get(b.getId());
		if(payload > 0) {
			BigDecimal ct = context.getCycleTimeDataMapping().get(b.getPitNo()+":"+b.getBenchNo()+":"+contextName);
			if(ct != null) {
				double th_ratio_val =  ct.doubleValue() /( payload* 60);
				th_ratio = new BigDecimal(th_ratio_val);
			}
		}
		
		return th_ratio;
		
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
	private Stockpile getStockpile(Block b){
		List<Stockpile> stockpiles = context.getProjectConfig().getStockPileList();
		
		for(Stockpile sp: stockpiles){
			Set<Block> blocks = sp.getBlocks();
			for(Block block: blocks){
				if(block.getBlockNo() == b.getBlockNo()){
					return sp;
				}
			}
		}
		
		return null;
	}
	
	private List<Dump> getDump(Block b){
		List<Dump> alldumps = context.getProjectConfig().getDumpList();
		List<Dump> dumps = new ArrayList<Dump>();
		for(Dump dump: alldumps){
			Set<Block> blocks = dump.getBlocks();
			for(Block block: blocks){
				if(block.getBlockNo() == b.getBlockNo()){
					dumps.add(dump);
					continue;
				}
			}
		}
		
		return dumps;
	}
}
