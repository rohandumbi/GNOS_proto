package com.org.gnos.scheduler.equation.generator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Block;
import com.org.gnos.core.Pit;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Field;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Process;
import com.org.gnos.db.model.ProcessConstraintData;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.db.model.Product;
import com.org.gnos.db.model.ProductJoin;
import com.org.gnos.db.model.Stockpile;
import com.org.gnos.db.model.TruckParameterCycleTime;
import com.org.gnos.scheduler.equation.Constraint;
import com.org.gnos.scheduler.equation.ExecutionContext;
import com.org.gnos.scheduler.equation.SPBlock;
import com.org.gnos.scheduler.equation.SlidingWindowExecutionContext;

public class ProcessConstraintEquationGenerator extends EquationGenerator{

	private List<ProcessConstraintData> processConstraintDataList;

	public ProcessConstraintEquationGenerator(ExecutionContext data) {
		super(data);
	}
	
	@Override
	public void generate() {
		processConstraintDataList = context.getProcessConstraintDataList();
		try {			
			buildProcessConstraintVariables();

		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public void buildProcessConstraintVariables() {
		
		int timePeriodStart = context.getTimePeriodStart();
		int timePeriodEnd = context.getTimePeriodEnd();
		int startYear = context.getStartYear();
		List<Process> processList = context.getProcessList();
		for(ProcessConstraintData processConstraintData: processConstraintDataList) {
			if(!processConstraintData.isInUse()) continue;
			int selectorType = processConstraintData.getSelectionType();
			int coefficientType = processConstraintData.getCoefficientType();
			Map<String, List<Object>> processExprMap = new HashMap<String, List<Object>>();
			boolean applyProcessRestrictions = false;
			boolean usetruckHourCoeffcient = false;
			if(coefficientType == ProcessConstraintData.COEFFICIENT_PRODUCT) {
				Product p = context.getProductFromName(processConstraintData.getCoefficient_name());
				if(p != null){
					List<Object> coefficients = new ArrayList<Object>();
					for(Integer eid : p.getExpressionIdList()){
						Expression expression = context.getExpressionById(eid);
						coefficients.add(expression);
					}
					for(Integer fid : p.getFieldIdList()){
						Field field = context.getFieldById(fid);
						coefficients.add(field);
					}
					Model model = context.getModelById(p.getModelId());
					processExprMap.put(model.getName(), coefficients);
					applyProcessRestrictions = true;
				}
			} else if(coefficientType == ProcessConstraintData.COEFFICIENT_PRODUCT_JOIN) {
				ProductJoin pj = context.getProductJoinFromName(processConstraintData.getCoefficient_name());
				Set<String> productNames = getProductsFromProductJoin(pj);
				for(String productName: productNames){
					Product p = context.getProductFromName(productName);
					Model model = context.getModelById(p.getModelId());
					String processName = model.getName();
					List<Object> coefficients = processExprMap.get(processName);
					if(coefficients == null){
						coefficients = new ArrayList<Object>();
						processExprMap.put(processName, coefficients);
					}
					for(Integer eid : p.getExpressionIdList()){
						Expression expression = context.getExpressionById(eid);
						coefficients.add(expression);
					}
					for(Integer fid : p.getFieldIdList()){
						Field field = context.getFieldById(fid);
						coefficients.add(field);
					}
				}
				applyProcessRestrictions = true;
			} else if( coefficientType == ProcessConstraintData.COEFFICIENT_TRUCK_HOUR ) {
				usetruckHourCoeffcient = true;
			}
			
			for(int i=timePeriodStart; i<= timePeriodEnd; i++){
				Constraint constraint = new Constraint(Constraint.PROCESS_CONSTRAINT);
				if(selectorType == ProcessConstraintData.SELECTION_PROCESS_JOIN) {
					ProcessJoin processJoin = context.getProcessJoinByName(processConstraintData.getSelector_name());
					if(processJoin != null) {
						for(Integer modelId: processJoin.getChildProcessList()){
							Model model = context.getModelById(modelId);
							if(model == null) continue;
							for( Process p: processList){
								if(p.getModel().getName().equals(model.getName())){
									List<Object> coefficients;
									if(applyProcessRestrictions){
										coefficients = processExprMap.get(p.getModel().getName());
										if(coefficients == null || coefficients.size() == 0) {
											continue;
										}
									} else {
										coefficients = new ArrayList<Object>();
										if(coefficientType == ProcessConstraintData.COEFFICIENT_EXPRESSION) {
											coefficients.add(context.getExpressionByName(processConstraintData.getCoefficient_name()));
										} else if(coefficientType == ProcessConstraintData.COEFFICIENT_FIELD)  {
											coefficients.add(context.getFieldByName(processConstraintData.getCoefficient_name()));
										}
									}
									buildProcessConstraintVariables(p, coefficients, usetruckHourCoeffcient, p.getBlocks(), i, constraint);
									break;
								}
							}
						}
					}
				}else if(selectorType == ProcessConstraintData.SELECTION_PROCESS) {
					for( Process p: processList){
						if(p.getModel().getName().equals(processConstraintData.getSelector_name())){
							List<Object> coefficients;
							if(applyProcessRestrictions){
								coefficients = processExprMap.get(p.getModel().getName());
								if(coefficients == null || coefficients.size() == 0) {
									continue;
								}
							} else {
								coefficients = new ArrayList<>();
								if(coefficientType == ProcessConstraintData.COEFFICIENT_EXPRESSION) {
									coefficients.add(context.getExpressionByName(processConstraintData.getCoefficient_name()));
								} else if(coefficientType == ProcessConstraintData.COEFFICIENT_FIELD)  {
									coefficients.add(context.getFieldByName(processConstraintData.getCoefficient_name()));
								}
							}
							buildProcessConstraintVariables(p, coefficients, usetruckHourCoeffcient, p.getBlocks(), i, constraint);
							break;
						}
					}
				} else if(selectorType == ProcessConstraintData.SELECTION_PIT) {
					String pitName = processConstraintData.getSelector_name();
					Pit pit = context.getPitNameMap().get(pitName);
					if(pit != null) {
						for( Process p: processList){
							List<Object> coefficients;
							if(applyProcessRestrictions){
								coefficients = processExprMap.get(p.getModel().getName());
								if(coefficients == null || coefficients.size() == 0) {
									continue;
								}
							} else {
								coefficients = new ArrayList<>();
								if(coefficientType == ProcessConstraintData.COEFFICIENT_EXPRESSION) {
									coefficients.add(context.getExpressionByName(processConstraintData.getCoefficient_name()));
								} else if(coefficientType == ProcessConstraintData.COEFFICIENT_FIELD)  {
									coefficients.add(context.getFieldByName(processConstraintData.getCoefficient_name()));
								}
							}
							List<Block> blocks = new ArrayList<Block>();
							for(Block b: p.getBlocks()){
								if(b.getPitNo() == pit.getPitNo()){
									blocks.add(b);
								}
							}
							buildProcessConstraintVariables(p, coefficients, usetruckHourCoeffcient, blocks, i, constraint);
						}
						if(!applyProcessRestrictions) {
							List<Object> coefficients = new ArrayList<Object>();
							if(coefficientType == ProcessConstraintData.COEFFICIENT_EXPRESSION) {
								coefficients.add(context.getExpressionByName(processConstraintData.getCoefficient_name()));
							} else if(coefficientType == ProcessConstraintData.COEFFICIENT_FIELD)  {
								coefficients.add(context.getFieldByName(processConstraintData.getCoefficient_name()));
							}
							List<Block> blocks = new ArrayList<Block>();
							for(Block b: context.getProcessBlocks()){
								if(pit.getPitNo() != b.getPitNo()) continue;
								blocks.add(b);								
							}
							buildStockpileConstraintVariables(coefficients,usetruckHourCoeffcient, blocks, i, constraint);
							blocks = new ArrayList<Block>();
							for(Block b: context.getWasteBlocks()){
								if(pit.getPitNo() != b.getPitNo()) continue;
								blocks.add(b);			
							}
							buildWasteConstraintVariables(coefficients, usetruckHourCoeffcient, blocks, i, constraint);
						}
						
					}
					
					
					
				} else if(selectorType == ProcessConstraintData.SELECTION_PIT_GROUP) {
					PitGroup pg = context.getPitGroupfromName(processConstraintData.getSelector_name());
					Set<Integer> pitNumbers = getPitsFromPitGroup(pg);
					for( Process p: processList){
						List<Object> coefficients;
						if(applyProcessRestrictions){
							coefficients = processExprMap.get(p.getModel().getName());
							if(coefficients == null || coefficients.size() == 0) {
								continue;
							}
						} else {
							coefficients = new ArrayList<Object>();
							if(coefficientType == ProcessConstraintData.COEFFICIENT_EXPRESSION) {
								coefficients.add(context.getExpressionByName(processConstraintData.getCoefficient_name()));
							} else if(coefficientType == ProcessConstraintData.COEFFICIENT_FIELD)  {
								coefficients.add(context.getFieldByName(processConstraintData.getCoefficient_name()));
							}
						}
						List<Block> blocks = new ArrayList<Block>();
						for(Block b: p.getBlocks()){
							if(pitNumbers.contains(b.getPitNo())){
								blocks.add(b);
							}
						}
						buildProcessConstraintVariables(p, coefficients, usetruckHourCoeffcient, blocks, i, constraint);
					}
					if(!applyProcessRestrictions) {
						List<Object> coefficients = new ArrayList<Object>();
						if(coefficientType == ProcessConstraintData.COEFFICIENT_EXPRESSION) {
							coefficients.add(context.getExpressionByName(processConstraintData.getCoefficient_name()));
						} else if(coefficientType == ProcessConstraintData.COEFFICIENT_FIELD)  {
							coefficients.add(context.getFieldByName(processConstraintData.getCoefficient_name()));
						}
						List<Block> blocks = new ArrayList<Block>();
						for(Block b: context.getProcessBlocks()){
							if(!pitNumbers.contains(b.getPitNo())) continue;
							blocks.add(b);								
						}
						buildStockpileConstraintVariables(coefficients, usetruckHourCoeffcient, blocks, i, constraint);
						blocks = new ArrayList<Block>();
						for(Block b: context.getWasteBlocks()){
							if(!pitNumbers.contains(b.getPitNo())) continue;
							blocks.add(b);			
						}
						buildWasteConstraintVariables(coefficients, usetruckHourCoeffcient, blocks, i, constraint);
					}
					
				} else {
					for( Process p: processList){
						List<Object> coefficients;
						if(applyProcessRestrictions){
							coefficients = processExprMap.get(p.getModel().getName());
							if(coefficients == null || coefficients.size() == 0) {
								continue;
							}
						} else {
							coefficients = new ArrayList<Object>();
							if(coefficientType == ProcessConstraintData.COEFFICIENT_EXPRESSION) {
								coefficients.add(context.getExpressionByName(processConstraintData.getCoefficient_name()));
							} else if(coefficientType == ProcessConstraintData.COEFFICIENT_FIELD)  {
								coefficients.add(context.getFieldByName(processConstraintData.getCoefficient_name()));
							}
						}
						buildProcessConstraintVariables(p, coefficients, usetruckHourCoeffcient, p.getBlocks(), i, constraint);
					}
					if(!applyProcessRestrictions) {
						List<Object> coefficients = new ArrayList<Object>();
						if(coefficientType == ProcessConstraintData.COEFFICIENT_EXPRESSION) {
							coefficients.add(context.getExpressionByName(processConstraintData.getCoefficient_name()));
						} else if(coefficientType == ProcessConstraintData.COEFFICIENT_FIELD)  {
							coefficients.add(context.getFieldByName(processConstraintData.getCoefficient_name()));
						}
						List<Block> blocks = new ArrayList<Block>();
						blocks.addAll(context.getProcessBlocks());
						buildStockpileConstraintVariables(coefficients, usetruckHourCoeffcient, blocks, i, constraint);
						blocks = new ArrayList<Block>();
						blocks.addAll(context.getWasteBlocks());
						buildWasteConstraintVariables(coefficients, usetruckHourCoeffcient, blocks, i, constraint);
					}
				}
				
				if(constraint.getVariables().size() > 0) {
					if(processConstraintData.isMax()){
						constraint.setEqualityType(Constraint.LESS_EQUAL);
						BigDecimal value = context.getScaledValue(new BigDecimal(processConstraintData.getConstraintData().get(startYear+i -1)));
						if(value.doubleValue() == 0) {
							constraint.setIgnore(true);
						}
						constraint.setValue(value);
					} else {
						constraint.setEqualityType(Constraint.GREATER_EQUAL);
						constraint.setValue(context.getScaledValue(new BigDecimal(processConstraintData.getConstraintData().get(startYear+i -1))));
					}
				}
				context.getConstraints().add(constraint);
			}
			//
		}
		
	}

	private void buildProcessConstraintVariables(Process p, List<Object> coefficients, boolean useTruckHour, List<Block> blocks, int period, Constraint constraint) {
		
		int processNumber = p.getProcessNo();
		for(Block block: blocks){			
			String variable = "p"+block.getPitNo()+"x"+block.getBlockNo()+"p"+processNumber+"t"+period;
			BigDecimal coefficientRatio =  new BigDecimal(0);
			if(useTruckHour) {
				coefficientRatio = getTruckHourRatio(block, p.getModel().getName());
			} else {
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
					coefficientRatio = coefficientRatio.add(context.getUnitValueforBlock(block, unitId, unitType));					
				}
			}
	
			if(coefficientRatio.doubleValue() == 0) continue;
					
			constraint.addVariable(variable, coefficientRatio);
			if(context.isSpReclaimEnabled() && period > 1 && context.isGlobalMode()) {
				int stockpileNo = getStockpileNo(block);
				if(stockpileNo > 0) {
					constraint.addVariable("sp"+stockpileNo+"x"+block.getBlockNo()+"p"+processNumber+"t"+period, coefficientRatio);
				}			
			}
		}
		if(context.isSpReclaimEnabled() && period > 1 && !context.isGlobalMode()) {
			SlidingWindowExecutionContext swctx = (SlidingWindowExecutionContext) context;
			BigDecimal fixedTime = context.getFixedTime();
			Map<Integer, SPBlock> spBlockMapping = swctx.getSpBlockMapping();
			Set<Integer> spNos = spBlockMapping.keySet();			
			for(int spNo: spNos){
				Stockpile sp = swctx.getStockpileFromNo(spNo);
				SPBlock spb = spBlockMapping.get(spNo);
				if(spb == null) continue;
				Set<Process> processes = spb.getProcesses();
				for(Process process: processes){
					if(process.getProcessNo() == p.getProcessNo()) {
						BigDecimal coefficientRatio =  new BigDecimal(0);
						TruckParameterCycleTime cycleTime =  context.getTruckParamCycleTimeByStockpileName(sp.getName());
						if(useTruckHour) {
							int payload = spb.getPayload();
							if(payload > 0) {
								BigDecimal ct = new BigDecimal(0);
								if(cycleTime.getProcessData() != null){
									ct = cycleTime.getProcessData().get(p.getModel().getName()).add(fixedTime);
								} 
								if(ct != null) {
									double th_ratio_val =  ct.doubleValue() /( payload* 60);
									coefficientRatio = new BigDecimal(th_ratio_val);
								}
							}
						} else {
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
								coefficientRatio = coefficientRatio.add(swctx.getUnitValueforBlock(spb, unitId, unitType));					
							}
						}
						
						if(coefficientRatio.doubleValue() == 0) continue;
						
						constraint.addVariable("sp"+spNo+"x0p"+processNumber+"t"+period, coefficientRatio);
					}
				}
			}
		}
	}
	
	private void buildStockpileConstraintVariables(List<Object> coefficients, boolean useTruckHour, List<Block> blocks, int period, Constraint constraint) {
		
		for(Block block: blocks){
			Stockpile sp = getStockpile(block);
			if(sp == null) continue;
			String variable ="p"+block.getPitNo()+"x"+block.getBlockNo()+"s"+sp.getStockpileNumber()+"t"+period;
			BigDecimal coefficientRatio = new BigDecimal(0);
			if(useTruckHour) {
				coefficientRatio = getTruckHourRatio(block, sp.getName());
			} else {
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
					coefficientRatio = coefficientRatio.add(context.getUnitValueforBlock(block, unitId, unitType));					
				}
			}
			
			if(coefficientRatio.doubleValue() == 0) continue;
			constraint.addVariable(variable, coefficientRatio);
		}			
	}

	private void buildWasteConstraintVariables(List<Object> coefficients, boolean useTruckHour, List<Block> blocks, int period, Constraint constraint) {
	
		for(Block block: blocks){
			BigDecimal coefficientRatio = new BigDecimal(0);
			if(!useTruckHour) {
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
					coefficientRatio = coefficientRatio.add(context.getUnitValueforBlock(block, unitId, unitType));					
				}
			}
			
			List<Dump> dumps = getDump(block);
			if(dumps == null) continue;
			for(Dump dump: dumps){
				if(useTruckHour) {
					coefficientRatio = getTruckHourRatio(block, dump.getName());
				}
				if(coefficientRatio.doubleValue() == 0) continue;
				String variable = "p"+block.getPitNo()+"x"+block.getBlockNo()+"w"+dump.getDumpNumber()+"t"+period;
				constraint.addVariable(variable, coefficientRatio);
			}
		}			
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
	private Stockpile getStockpile(Block b){
		List<Stockpile> stockpiles = context.getStockpiles();
		
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
		List<Dump> alldumps = context.getDumps();
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
