package com.org.gnos.scheduler.equation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Block;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Field;
import com.org.gnos.db.model.Product;
import com.org.gnos.db.model.ProductJoin;
import com.org.gnos.db.model.TruckParameterCycleTime;
import com.org.gnos.scheduler.processor.CapexRecord;
import com.org.gnos.scheduler.processor.SlidingWindowModeDBStorageHelper;

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
	
	private Set<CapexRecord> capexCapacityUsedList = new HashSet<CapexRecord>();
	
	private List<Expression> gradeExpressions;
	//private Set<String> gradeFields;
	
	public SlidingWindowExecutionContext(int projectId, int scenarioId) {
		super(projectId, scenarioId);
		parseGradeExpression();
	}
	
	@Override
	public void reset() {
		super.reset();
		spblockVariableMapping = new HashMap<Integer, List<String>>();
	}
	
	public int getStartYear() {
		return getScenario().getStartYear();
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
/*	public BigDecimal getExpressionValueforBlock(SPBlock spb, Expression expr) {
		String expressionName = expr.getName().replaceAll("\\s+","_");			
		return spb.getComputedField(expressionName);		
	}
	
	public BigDecimal getExpressionValueforBlock(SPBlock spb, String exprName) {
		String expressionName = exprName.replaceAll("\\s+","_");			
		return spb.getComputedField(expressionName);		
	}
	*/

	public BigDecimal getUnitValueforBlock(SPBlock spb, int unitId, short unitType) {
		if(unitType == 1) { // 1- Field, 2 - Expression
			Field field = getFieldById(unitId);
			try {
				BigDecimal value = spb.getField(field.getName());
				return value;
			} catch(Exception e) {
				return null;
			}
		} else {
			Expression expression = getExpressionById(unitId);
			String expressionName = expression.getName().replaceAll("\\s+", "_");
			return spb.getComputedField(expressionName);
		}
	}
	
	public BigDecimal getUnitValueforBlock(SPBlock spb, String unitName, short unitType) {
		if(unitType == 1) { // 1- Field, 2 - Expression
			return spb.getField(unitName);
		} else {
			return spb.getComputedField(unitName);
		}
	}
	
	public BigDecimal getGradeValueforBlock(SPBlock spb, String unitName, short unitType) {
		if(unitType == 1) { // 1- Field, 2 - Expression
			return  spb.getField(unitName);
		} else {
			return spb.getComputedField(unitName);
		}
	}
	
	@Override
	public boolean isGlobalMode() {
		return false;
	}
	
	
	@Override
	public boolean hasRemainingTonnage(Block b) {
		
		return (getTonnesWtForBlock(b) > TONNAGE_TOLERANCE); //0.01 is the tolerance
	}
	
	public double getOriginalTonnesWtForBlock(Block b){
		return super.getTonnesWtForBlock(b);
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
			/*if(spb.reclaimedTonnesWt == spb.lasttonnesWt){
				spb.reset();
			}*/
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
	
	public void finalizeStockpiles(SlidingWindowModeDBStorageHelper storeHelper) {
		
		Set<Integer> spNos = spBlockMapping.keySet();
		for(int spNo: spNos){
			SPBlock spb = spBlockMapping.get(spNo);
			if(!(spb.getLasttonnesWt() == spb.getTonnesWt() && spb.getReclaimedTonnesWt() == 0)) {
				storeHelper.processStockpileInventory(spNo, spb);
			}			
			spb.setLasttonnesWt(spb.getTonnesWt());
			if(spb.reclaimedTonnesWt == spb.lasttonnesWt){
				spb.reset();
			}
			spb.setReclaimedTonnesWt(0);
		}
		
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
				Block b = getBlockByNumber(blockNo);
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
				/*// calculate grade fields
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
				}*/
				
				// Calculate fields 
				Map<String, BigDecimal> spblockFields = spb.getFields();
				
				List<Field> fields = getFields();
				Map<String, String> blockFields = b.getFields();
				for(Field field: fields) {
					if(field.getDataType() == Field.TYPE_TEXT || field.getDataType() == Field.TYPE_NUMERIC) continue;
					BigDecimal fieldVal = new BigDecimal(blockFields.get(field.getName()));
					fieldVal = fieldVal.multiply(new BigDecimal(ratio));
					BigDecimal existingVal = spblockFields.get(field.getName());
					if(existingVal != null) {
						fieldVal = fieldVal.add(existingVal);
					}
					spblockFields.put(field.getName(), fieldVal);				
				}
				
				spb.setComputedFields(spblockcomputedFields);
				spb.setFields(spblockFields);
				spb.getProcesses().addAll(b.getProcesses());
			}
			processGrades(spb, gradeExprs);
		}
		
		spTonnesWigthMapping = new HashMap<Integer,  Map<Integer, Double>>();
	}
	
	private void balanceStockpileBlock(SPBlock spb) {
		double spbratio = (spb.getLasttonnesWt() - spb.getReclaimedTonnesWt())/spb.getTonnesWt();
		double spbremainingratio = (spb.getLasttonnesWt() - spb.getReclaimedTonnesWt())/spb.getLasttonnesWt();
		Map<String, BigDecimal> spbcomputedFields = spb.getComputedFields();
		Map<String, BigDecimal> spbFields = spb.getFields();
		//Map<String, BigDecimal> gradeFields = spb.getGradeFields();
		Set<String> computedfieldNames = spbcomputedFields.keySet();
		//Set<String> gradefieldNames = gradeFields.keySet();
		Set<String> spbFieldNames = spbFields.keySet();
		for(String fieldName: computedfieldNames) {
			BigDecimal fieldVal = spbcomputedFields.get(fieldName);
			if(fieldVal != null) {
				fieldVal = fieldVal.multiply(new BigDecimal(spbratio));
				spbcomputedFields.put(fieldName, fieldVal);
			}		
		}
/*		for(String fieldName: gradefieldNames) {
			BigDecimal fieldVal = spbFields.get(fieldName);
			if(fieldVal != null) {
				fieldVal = fieldVal.multiply(new BigDecimal(spbremainingratio));
				spbcomputedFields.put(fieldName, fieldVal);
			}
			
		}*/
		for(String fieldName: spbFieldNames) {
			BigDecimal fieldVal = spbFields.get(fieldName);
			if(fieldVal != null) {
				fieldVal = fieldVal.multiply(new BigDecimal(spbremainingratio));
				spbFields.put(fieldName, fieldVal);
			}		
		}
	}
	
	public void finalizeCapex(List<CapexRecord> capexBinaryList) {
		if(capexBinaryList == null || capexBinaryList.size() == 0) return;
		for(CapexRecord cr: capexBinaryList) {
			if(cr.getValue() == 1) {
				capexCapacityUsedList.add(cr);
			}
			
		}
	}
	private void processGrades(SPBlock spb, Set<String> gradeExprs) {
		for(String exprName: gradeExprs) {
			Expression expr = getExpressionByName(exprName);
			String exprValue = expr.getExprvalue();
			if(expr.isComplex()) {
				String[] arr = exprValue.split("[+-/*]");
				String f1 = arr[0].trim();
				String f2 = arr[1].trim();
				BigDecimal value = new BigDecimal(0);
				if(exprValue.indexOf("/")!= -1){
					//System.out.println("Expression Name :"+exprName+" f1:"+spb.getGradeField(f1)+" f2: "+ spb.getGradeField(f2));
					if(spb.getField(f1).doubleValue() == 0 && spb.getField(f2).doubleValue() == 0 ) {
						value =  new BigDecimal(0);
					} else {
						value = new BigDecimal(spb.getField(f1).doubleValue()/spb.getField(f2).doubleValue());
					}
					
				} else if(exprValue.indexOf("*")!= -1){
					value = spb.getField(f1).multiply(spb.getField(f2));
				} else if(exprValue.indexOf("+")!= -1){
					value = spb.getField(f1).add(spb.getField(f2));
				} else if(exprValue.indexOf("1")!= -1){
					value = spb.getField(f1).subtract(spb.getField(f2));
				}
				spb.getComputedFields().put(exprName, value);
				//System.out.println("TestSP: exprName: "+exprName+" f1:"+spb.getGradeField(f1)+" f2:"+spb.getGradeField(f2)+" value:"+value);
			} else {
				spb.getComputedFields().put(exprName, spb.getField(exprValue.trim()));
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
		//gradeFields = new HashSet<String>();		
		for(Expression expression: getExpressions()) {
			if(expression.isGrade()){
				gradeExpressions.add(expression);
				/*if(expression.isComplex()) {
					gradeFields.addAll(parseExpressionValue(expression.getExprvalue()));
				} else {
					gradeFields.add(expression.getExprvalue());
				}*/
			}
		}
	}

	public BigDecimal getProductValueForBlock(SPBlock b, Product product) {
		BigDecimal value =  new BigDecimal(0);
		if(product == null) return value;
		for(Integer eid : product.getExpressionIdList()){
			value = value.add(getUnitValueforBlock(b, eid, Product.UNIT_EXPRESSION));
		}
		for(Integer fid : product.getFieldIdList()){
			value = value.add(getUnitValueforBlock(b, fid, Product.UNIT_FIELD));
		}
		return value;	
	}

	public BigDecimal getProductJoinValueForBlock(SPBlock b, ProductJoin productJoin) {
		BigDecimal value =  new BigDecimal(0);
		for(String productName :productJoin.getProductList()) {
			Product p = getProductFromName(productName);
			value = value.add(getProductValueForBlock(b, p));
		}		
		return value;
	}
	
	public BigDecimal getTruckHourRatio(SPBlock b, String originSP, String contextName){
		BigDecimal th_ratio = new BigDecimal(0);
		int payload = b.getPayload();
		if(payload > 0) {
			
			TruckParameterCycleTime cycleTime = getTruckParamCycleTimeByStockpileName(originSP);
			BigDecimal ct = new BigDecimal(0);
			if(cycleTime.getProcessData() != null){
				ct = cycleTime.getProcessData().get(contextName).add(getFixedTime());
			} 
			if(ct != null) {
				double th_ratio_val =  ct.doubleValue() /( payload* 60);
				th_ratio = new BigDecimal(th_ratio_val);
			}
		}
		
		return th_ratio;
		
	}

	public boolean isCapacityUsed(int capexNo, int instanceNo) {
		
		for(CapexRecord cr: capexCapacityUsedList) {
			if(cr.getCapexNo() == capexNo && cr.getInstanceNo() == instanceNo) {
				return true;
			}
		}
			
		
		return false;
	}
}
