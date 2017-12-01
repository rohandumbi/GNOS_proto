package com.org.gnos.services.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.db.DBManager;
import com.org.gnos.db.dao.BenchConstraintDAO;
import com.org.gnos.db.dao.CapexDAO;
import com.org.gnos.db.dao.CycleFixedTimeDAO;
import com.org.gnos.db.dao.CycleTimeFieldMappingDAO;
import com.org.gnos.db.dao.DumpDAO;
import com.org.gnos.db.dao.DumpDependencyDAO;
import com.org.gnos.db.dao.ExpressionDAO;
import com.org.gnos.db.dao.FieldDAO;
import com.org.gnos.db.dao.FixedCostDAO;
import com.org.gnos.db.dao.GradeConstraintDAO;
import com.org.gnos.db.dao.ModelDAO;
import com.org.gnos.db.dao.OpexDAO;
import com.org.gnos.db.dao.PitDependencyDAO;
import com.org.gnos.db.dao.PitGroupDAO;
import com.org.gnos.db.dao.ProcessConstraintDAO;
import com.org.gnos.db.dao.ProcessJoinDAO;
import com.org.gnos.db.dao.ProcessTreeDAO;
import com.org.gnos.db.dao.ProcessTreeStateDAO;
import com.org.gnos.db.dao.ProductDAO;
import com.org.gnos.db.dao.ProductJoinDAO;
import com.org.gnos.db.dao.ProjectDAO;
import com.org.gnos.db.dao.RequiredFieldDAO;
import com.org.gnos.db.dao.ScenarioDAO;
import com.org.gnos.db.dao.StockpileDAO;
import com.org.gnos.db.dao.TruckParameterCycleTimeDAO;
import com.org.gnos.db.dao.TruckParameterPayloadDAO;
import com.org.gnos.db.model.CapexData;
import com.org.gnos.db.model.CapexInstance;
import com.org.gnos.db.model.CycleTimeFieldMapping;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.DumpDependencyData;
import com.org.gnos.db.model.Expression;
import com.org.gnos.db.model.Field;
import com.org.gnos.db.model.FixedOpexCost;
import com.org.gnos.db.model.GradeConstraintData;
import com.org.gnos.db.model.Model;
import com.org.gnos.db.model.OpexData;
import com.org.gnos.db.model.PitBenchConstraintData;
import com.org.gnos.db.model.PitDependencyData;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.ProcessConstraintData;
import com.org.gnos.db.model.ProcessJoin;
import com.org.gnos.db.model.ProcessTreeNode;
import com.org.gnos.db.model.ProcessTreeNodeState;
import com.org.gnos.db.model.Product;
import com.org.gnos.db.model.ProductJoin;
import com.org.gnos.db.model.Project;
import com.org.gnos.db.model.RequiredField;
import com.org.gnos.db.model.Scenario;
import com.org.gnos.db.model.Stockpile;
import com.org.gnos.db.model.TruckParameterCycleTime;
import com.org.gnos.db.model.TruckParameterPayload;
import com.org.gnos.services.ExpressionProcessor;
import com.org.gnos.services.PitBenchProcessor;
import com.org.gnos.services.csv.CSVDataProcessor;
import com.org.gnos.services.csv.CycletTimeDataProcessor;

public class ProjectImportHelper implements ProjectTypes {
	
	private BufferedReader br = null;
	
	public void importProject(String fileName) {
		Map<Integer, List<String[]>> projectData = new HashMap<Integer, List<String[]>>();

		try {
			br = new BufferedReader(new FileReader(fileName));
			String line = null;
			
			while ((line = br.readLine()) != null) {
				String[] linedataArr = line.split("\\|");
				if(linedataArr.length > 1) {
					Integer ind = Integer.parseInt(linedataArr[0]);
					List<String[]> data = projectData.get(ind);
					if(data == null) {
						data = new ArrayList<String[]>();
						projectData.put(ind, data);
					}
					data.add(linedataArr);
				}
			}
			if(projectData.get(PROJECT_IND) == null) {
				throw new Exception("Uploaded data file is not valid");
			}
			processData(projectData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processData(Map<Integer, List<String[]>> projectData) throws Exception {
		Project project = createProject(projectData.get(PROJECT_IND).get(0));
		if(project.getId() == -1) {
			throw new Exception("Can not import project");
		}
		importProjectConfig(project.getId(), projectData);
		importCSVData(project.getId(), projectData.get(PROJECT_DATA_IND));
		importCycleTImeData(project.getId(), projectData.get(PROJECT_CYCLETIME_DATA_IND));
	}

	private Project createProject(String[] list) {
		//int random = new Random().nextInt(1000);
		Project project = new Project();
		project.setName(list[1]);
		project.setDesc(list[2]);
		for(int i = 3; i<list.length; i++) {
			project.addFile(list[i]);
		}
		new ProjectDAO().create(project);
		return project;
	}
	
	private void importProjectConfig(int projectId, Map<Integer, List<String[]>> projectData) {
		
		// store fields
		List<String[]> fields = projectData.get(FIELD_IND);
		Map<String, Integer> fieldOldNewIdMapping = new HashMap<String, Integer>();
		for(int i=0; i < fields.size(); i++) {
			String[] fieldValues = fields.get(i);
			Field field = new Field();
			field.setName(fieldValues[2]);
			field.setDataType(Short.parseShort(fieldValues[3]));
			if(field.getDataType() == Field.TYPE_GRADE) {
				field.setWeightedUnit(fieldValues[4]);
			}
			new FieldDAO().create(field, projectId);
			fieldOldNewIdMapping.put(fieldValues[1], field.getId());
		}
		
		// store required fields
		List<String[]> requiredfields = projectData.get(REQ_FIELD_IND);
		for(int i=0; i < requiredfields.size(); i++) {
			String[] requiredfieldValues = requiredfields.get(i);
			RequiredField requiredfield = new RequiredField();
			requiredfield.setFieldName(requiredfieldValues[1]);
			requiredfield.setMappedFieldname(requiredfieldValues[2]);
			new RequiredFieldDAO().create(requiredfield, projectId);
		}
		
		// store expressions
		
		List<String[]> expressions = projectData.get(EXPRESSION_IND);
		Map<String, Integer> expressionOldNewIdMapping = new HashMap<String, Integer>();
		for(int i=0; i < expressions.size(); i++) {
			String[] expressionValues = expressions.get(i);
			Expression expression = new Expression();
			expression.setName(expressionValues[1]);
			expression.setGrade(Boolean.parseBoolean(expressionValues[2]));
			expression.setComplex(Boolean.parseBoolean(expressionValues[3]));
			expression.setExprvalue(expressionValues[4]);
			expression.setFilter(expressionValues[5]);
			expression.setWeightedFieldType(Short.parseShort(expressionValues[7]));
			if(expression.isGrade()) {
				expression.setWeightedField(expressionValues[6]);
			}
			new ExpressionDAO().create(expression, projectId);
			expressionOldNewIdMapping.put(expressionValues[8], expression.getId());
		}
		
		// store models
		
		List<String[]> models = projectData.get(MODEL_IND);
		Map<String, Integer> modelOldNewIdMapping = new HashMap<String, Integer>();
		for(int i= 0; i < models.size(); i++) {
			String[] modelValues = models.get(i);
			Model model = new Model();
			model.setName(modelValues[1]);
			model.setUnitType(Short.parseShort(modelValues[2]));
			if(model.getUnitType() == Model.UNIT_FIELD) {
				model.setFieldId(fieldOldNewIdMapping.get(modelValues[3]));
			} else if (model.getUnitType() == Model.UNIT_EXPRESSION) {
				model.setExpressionId(expressionOldNewIdMapping.get(modelValues[4]));
			}
			model.setCondition(modelValues[5]);
			new ModelDAO().create(model, projectId);
			modelOldNewIdMapping.put(modelValues[6], model.getId());
		}
		
		// Store processes
		List<String[]> processes = projectData.get(PROCESS_IND);
		for(int i= 0; i < processes.size(); i++) {
			String[] processValues = processes.get(i);
			int modelId = modelOldNewIdMapping.get(processValues[1]);
			int parentModelId = Integer.parseInt(processValues[2]);
			if(parentModelId != -1) {
				parentModelId = modelOldNewIdMapping.get(""+parentModelId);
			}
			ProcessTreeNode processTreeNode = new ProcessTreeNode();
			processTreeNode.setModelId(modelId);
			processTreeNode.setParentModelId(parentModelId);
			new ProcessTreeDAO().create(processTreeNode, projectId);
		}
		
		// Store processes
		List<String[]> processTreeStates = projectData.get(PROCESS_TREE_STATE_IND);
		for(int i= 0; i < processTreeStates.size(); i++) {
			String[] processTreeStateValues = processTreeStates.get(i);
			String nodeName = processTreeStateValues[1];
			float xLoc = Float.parseFloat(processTreeStateValues[2]);
			float yLoc = Float.parseFloat(processTreeStateValues[3]);
			
			ProcessTreeNodeState processTreeNodeState = new ProcessTreeNodeState();
			processTreeNodeState.setNodeName(nodeName);
			processTreeNodeState.setxLoc(xLoc);
			processTreeNodeState.setyLoc(yLoc);
			new ProcessTreeStateDAO().create(processTreeNodeState, projectId);
		}
		
		// store process joins
		List<String[]> processeJoins = projectData.get(PROCESS_JOIN_IND);
		for(int i= 0; processeJoins !=null && i < processeJoins.size(); i++) {
			String[] processJoinValues = processeJoins.get(i);
			ProcessJoin processJoin = new ProcessJoin();
			processJoin.setName(processJoinValues[1]);
			for(int j=2; j<processJoinValues.length; j++ ) {
				processJoin.addProcess(modelOldNewIdMapping.get(processJoinValues[j]));
			}
			new ProcessJoinDAO().create(processJoin, projectId);
		}
		// store products 
		List<String[]> products = projectData.get(PRODUCT_IND);
		for(int i= 0; products!= null && i < products.size(); i++) {
			String[] productvalues = products.get(i);
			short childUnitType = Short.parseShort(productvalues[4]);
			Product product = new Product();
			product.setName(productvalues[1]);
			product.setModelId(modelOldNewIdMapping.get(productvalues[2]));
			product.setBaseProduct(productvalues[3]);
			if(childUnitType == Product.UNIT_FIELD) {
				product.getFieldIdList().add(fieldOldNewIdMapping.get(productvalues[5]));
			} else if (childUnitType == Product.UNIT_EXPRESSION) {
				product.getExpressionIdList().add(expressionOldNewIdMapping.get(productvalues[5]));
			}
			new ProductDAO().create(product, projectId);
		}
		
		// store product joins
		List<String[]> procductJoins = projectData.get(PRODUCT_JOIN_IND);
		for(int i= 0; procductJoins != null && i < procductJoins.size(); i++) {
			String[] productJoinValues = procductJoins.get(i);
			ProductJoin productJoin = new ProductJoin();
			productJoin.setName(productJoinValues[1]);
			if(productJoinValues.length > 2) {
				String[] childProductList = productJoinValues[2].split(",");
						
				for(int j=0; j< childProductList.length; j++) {
					productJoin.getProductList().add(childProductList[j]);
				}
			}
			if(productJoinValues.length > 3) {
				String[] childProductJoinList = productJoinValues[3].split(",");
				for(int j=0; j< childProductJoinList.length; j++) {
					productJoin.getProductJoinList().add(childProductJoinList[j]);
				}
			}			

			new ProductJoinDAO().create(productJoin, projectId);
		}
		
		// Store pit group 
		
		List<String[]> pitGroups = projectData.get(PIT_GROUP_IND);
		for(int i= 0; i < pitGroups.size(); i++) {
			String[] pitGroupValues = pitGroups.get(i);
			PitGroup pitGroup = new PitGroup();
			pitGroup.setName(pitGroupValues[1]);
			
			if(pitGroupValues.length > 2) {
				String[] childPitList = pitGroupValues[2].split(",");			
				for(int j=0; j< childPitList.length; j++) {
					pitGroup.addPit(childPitList[j]);
				}
			}

			if(pitGroupValues.length > 3) {
				String[] childPitGroupList = pitGroupValues[3].split(",");
				for(int j=0; j< childPitGroupList.length; j++) {
					pitGroup.addPitGroup(childPitGroupList[j]);
				}
			}
			
			new PitGroupDAO().create(pitGroup, projectId);
		}
		
		// store dumps
		List<String[]> dumps = projectData.get(DUMP_IND);
		for(int i= 0; i < dumps.size(); i++) {
			String[] dumpValues = dumps.get(i);
			Dump dump = new Dump();
			dump.setType(Integer.parseInt(dumpValues[1]));
			dump.setName(dumpValues[2]);
			dump.setMappedTo(dumpValues[3]);
			dump.setMappingType(Integer.parseInt(dumpValues[4]));
			dump.setHasCapacity(Boolean.parseBoolean(dumpValues[5]));
			dump.setCondition(dumpValues[6]);
			dump.setCapacity(Integer.parseInt(dumpValues[7]));
			
			new DumpDAO().create(dump, projectId);
		}
		// store stockpiles
		List<String[]> stockpiles = projectData.get(STOCKPILE_IND);
		for(int i= 0; i < stockpiles.size(); i++) {
			String[] values = stockpiles.get(i);
			
			Stockpile sp = new Stockpile();
			sp.setType(Integer.parseInt(values[1]));
			sp.setName(values[2]);
			sp.setMappedTo(values[3]);
			sp.setMappingType(Integer.parseInt(values[4]));
			sp.setHasCapacity(Boolean.parseBoolean(values[5]));
			sp.setCondition(values[6]);
			sp.setCapacity(Integer.parseInt(values[7]));
			sp.setReclaim(Boolean.parseBoolean(values[8]));
			
			new StockpileDAO().create(sp, projectId);
		}
		List<String[]> truckPparamPayloads = projectData.get(TRUCKPARAM_PAYLOAD);
		for(int i = 0; truckPparamPayloads!= null && i< truckPparamPayloads.size(); i++) {
			String[] values = truckPparamPayloads.get(i);
			TruckParameterPayload obj = new TruckParameterPayload();
			obj.setMaterialName(values[1]);
			obj.setPayload(Integer.parseInt(values[2]));
 
			new TruckParameterPayloadDAO().create(obj, projectId);
	 
		}
		
		List<String[]> truckPparamCycleTimes = projectData.get(TRUCKPARAM_CYCLE_TIME);
		for(int i = 0; truckPparamCycleTimes!= null && i< truckPparamCycleTimes.size(); i++) {
			String[] values = truckPparamCycleTimes.get(i);
			TruckParameterCycleTime obj = new TruckParameterCycleTime();
			obj.setStockPileName(values[1]);
			for(int j= 2; j< values.length; j++) {
				String[] processData = values[j].split(",");
				obj.getProcessData().put(processData[0], new BigDecimal(processData[1]));
			}
 
			new TruckParameterCycleTimeDAO().create(obj, projectId); 
		}
		List<String[]> fixedTimeRow = projectData.get(CYCLE_FIXED_TIME);
		if(fixedTimeRow != null && fixedTimeRow.size() > 0) {
			new CycleFixedTimeDAO().create(projectId, new BigDecimal(fixedTimeRow.get(0)[1]));
		}
		
		List<String[]> cycleTimeFieldMappings = projectData.get(CYCLE_TIME_FIELD_MAPPING);
		for(int i = 0; cycleTimeFieldMappings!= null && i< cycleTimeFieldMappings.size(); i++) {
			String[] values = cycleTimeFieldMappings.get(i);
			CycleTimeFieldMapping obj = new CycleTimeFieldMapping();
			obj.setFieldName(values[1]);
			obj.setMappingType(Short.parseShort(values[2]));
			obj.setMappedFieldName(values[3]);
			new CycleTimeFieldMappingDAO().create(obj, projectId);
	 
		}
		
		// store scenarios 
		 List<String[]> scenariosData = projectData.get(SCENARIO_IND);
		 Map<String, Map<Integer, List<String[]>>> scenarioDataMap = new HashMap<String, Map<Integer, List<String[]>>>();
		 for(int i = 0; i< scenariosData.size(); i++) {
			 String[] row = scenariosData.get(i);
			 Map<Integer, List<String[]>> scenarioData = scenarioDataMap.get(row[1]);
			 if(scenarioData == null) {
				 scenarioData = new HashMap<Integer, List<String[]>>();
				 scenarioDataMap.put(row[1], scenarioData);
			 }
			 List<String[]> data = scenarioData.get(Integer.parseInt(row[2]));
			 if(data == null) {
				 data = new ArrayList<String[]>();
				 scenarioData.put(Integer.parseInt(row[2]), data);
			 }
			 data.add(Arrays.copyOfRange(row, 3, row.length));	 
		 }
		 Set<String> scenarioIds = scenarioDataMap.keySet();
		 for(String scenarioId : scenarioIds) {
			 Map<Integer, List<String[]>>  scenarioConfigData = scenarioDataMap.get(scenarioId);
			 List<String[]> scenarioDataList = scenarioConfigData.get(SCENARIO_IND);
			 String[] scenarioData = scenarioDataList.get(0);
			 Scenario scenario = new Scenario();
			 scenario.setName(scenarioData[0]);
			 scenario.setDiscount(Float.parseFloat(scenarioData[1]));
			 scenario.setStartYear(Integer.parseInt(scenarioData[2]));
			 scenario.setTimePeriod(Integer.parseInt(scenarioData[3]));
			 new ScenarioDAO().create(scenario, projectId);
			 int newScenarioId = scenario.getId();
			 importScenarioData(newScenarioId, scenarioConfigData, fieldOldNewIdMapping, expressionOldNewIdMapping, modelOldNewIdMapping);
		 }
		
		
	}
	
	private void importScenarioData(int scenarioId,  Map<Integer, List<String[]>> scenarioConfigData, Map<String, Integer> fieldOldNewIdMapping,
			Map<String, Integer> expressionOldNewIdMapping, Map<String, Integer> modelOldNewIdMapping) {
		
		List<String[]> opexDataList = scenarioConfigData.get(SCN_OPEX);
		for(int i= 0; opexDataList!= null && i < opexDataList.size(); i++) {
			String[] values = opexDataList.get(i);
			OpexData od = new OpexData();
			if(Integer.parseInt(values[0]) != -1) {
				od.setModelId(modelOldNewIdMapping.get(values[0]));
			} else {
				od.setModelId(-1);
			}
			
			od.setProductJoinName(values[1]);
			od.setUnitType(Short.parseShort(values[2]));
			if(od.getUnitType() == OpexData.UNIT_FIELD) {
				Integer newFieldId = fieldOldNewIdMapping.get(values[3]);
				if(newFieldId != null) {
					od.setFieldId(newFieldId);
				}
				
			} else if(od.getUnitType() == OpexData.UNIT_EXPRESSION) {
				Integer newExpressionId = expressionOldNewIdMapping.get(values[4]);
				if(newExpressionId != null) {
					od.setExpressionId(newExpressionId);
				}
			}
			od.setInUse(Boolean.parseBoolean(values[5]));
			od.setRevenue(Boolean.parseBoolean(values[6]));
			for(int j= 7; j< values.length; j++) {
				String[] costData = values[j].split(",");
				od.addYear(Integer.parseInt(costData[0]), new BigDecimal(costData[1]));
			}
			new OpexDAO().create(od, scenarioId);
		}
		
		List<String[]> fixedCostList = scenarioConfigData.get(SCN_FIXED_COST);
		for(int i= 0; fixedCostList!= null && i < fixedCostList.size(); i++) {
			String[] values = fixedCostList.get(i);
			FixedOpexCost foc = new FixedOpexCost();
			foc.setCostType(Integer.parseInt(values[0]));
			foc.setSelectorName(values[1]);
			foc.setSelectionType(Integer.parseInt(values[2]));
			foc.setInUse(Boolean.parseBoolean(values[3]));
			foc.setDefault(Boolean.parseBoolean(values[4]));
			for(int j= 5; j< values.length; j++) {
				String[] costData = values[j].split(",");
				foc.addCostData(Integer.parseInt(costData[0]), new BigDecimal(costData[1]));
			}
			new FixedCostDAO().create(foc, scenarioId);
		}
		
		List<String[]> processConstraintList = scenarioConfigData.get(SCN_PROCESS_CONSTRAINT);
		for(int i= 0; processConstraintList!= null && i < processConstraintList.size(); i++) {
			String[] values = processConstraintList.get(i);
			ProcessConstraintData pcd = new ProcessConstraintData();
			pcd.setCoefficient_name(values[0]);
			pcd.setSelector_name(values[1]);
			pcd.setInUse(Boolean.parseBoolean(values[2]));
			pcd.setMax(Boolean.parseBoolean(values[3]));
			pcd.setCoefficientType(Integer.parseInt(values[4]));
			pcd.setSelectionType(Integer.parseInt(values[5]));
			for(int j= 6; j< values.length; j++) {
				String[] costData = values[j].split(",");
				pcd.addYear(Integer.parseInt(costData[0]), Float.parseFloat(costData[1]));
			}
			new ProcessConstraintDAO().create(pcd, scenarioId);
		}
		
		List<String[]> benchConstraintList = scenarioConfigData.get(SCN_BENCH_CONSTRAINT);
		for(int i= 0;benchConstraintList !=null && i < benchConstraintList.size(); i++) {
			String[] values = benchConstraintList.get(i);
			PitBenchConstraintData pbcd = new PitBenchConstraintData();
			pbcd.setInUse(Boolean.parseBoolean(values[0]));
			pbcd.setPitName(values[1]);
			for(int j= 2; j< values.length; j++) {
				String[] costData = values[j].split(",");
				pbcd.addYear(Integer.parseInt(costData[0]), Float.parseFloat(costData[1]));
			}
			new BenchConstraintDAO().create(pbcd, scenarioId);
		}
		
		List<String[]> gradeConstraintList = scenarioConfigData.get(SCN_GRADE_CONSTRAINT);
		for(int i= 0; gradeConstraintList !=null && i < gradeConstraintList.size(); i++) {
			String[] values = gradeConstraintList.get(i);
			GradeConstraintData gcd = new GradeConstraintData();
			gcd.setInUse(Boolean.parseBoolean(values[0]));
			gcd.setProductJoinName(values[1]);
			gcd.setSelectedGradeName(values[2]);
			gcd.setMax(Boolean.parseBoolean(values[3]));
			gcd.setSelectionType(Integer.parseInt(values[4]));
			gcd.setSelectorName(values[5]);
			for(int j= 6; j< values.length; j++) {
				String[] costData = values[j].split(",");
				gcd.addYear(Integer.parseInt(costData[0]), Float.parseFloat(costData[1]));
			}
			new GradeConstraintDAO().create(gcd, scenarioId);
		}
		
		List<String[]> pitDependencyList = scenarioConfigData.get(SCN_PIT_DEPENDENCY);
		for(int i= 0; pitDependencyList!=null && i < pitDependencyList.size(); i++) {
			String[] values = pitDependencyList.get(i);
			PitDependencyData pdd = new PitDependencyData();
			pdd.setInUse(Boolean.parseBoolean(values[0]));
			pdd.setFirstPitName(values[1]);
			pdd.setFirstPitAssociatedBench(values[2]);
			pdd.setDependentPitName(values[3]);
			pdd.setDependentPitAssociatedBench(values[4]);
			pdd.setMinLead(Integer.parseInt(values[5]));
			pdd.setMaxLead(Integer.parseInt(values[6]));
			
			new PitDependencyDAO().create(pdd, scenarioId);
			
		}
		
		List<String[]> dumpDependencyList = scenarioConfigData.get(SCN_DUMP_DEPENDENCY);
		for(int i= 0; dumpDependencyList !=null && i < dumpDependencyList.size(); i++) {
			String[] values = dumpDependencyList.get(i);
			DumpDependencyData ddd = new DumpDependencyData();
			ddd.setInUse(Boolean.parseBoolean(values[0]));
			ddd.setFirstPitName(values[1]);
			ddd.setFirstPitGroupName(values[2]);
			ddd.setFirstDumpName(values[3]);
			ddd.setDependentDumpName(values[4]);
			
			new DumpDependencyDAO().create(ddd, scenarioId);			
		}
		
		List<String[]> capexList = scenarioConfigData.get(SCN_CAPEX);
		for(int i= 0; capexList!=null && i < capexList.size(); i++) {
			String[] values = capexList.get(i);
			CapexData cd = new CapexData();
			cd.setName(values[0]);
			
			for(int j= 1; j< values.length; j++) {
				String[] capexInstanceData = values[j].split(",");
				CapexInstance ci = new CapexInstance();
				ci.setName(capexInstanceData[0]);
				ci.setGroupingName(capexInstanceData[1]);
				ci.setGroupingType(Integer.parseInt(capexInstanceData[2]));
				ci.setCapexAmount(Long.parseLong(capexInstanceData[3]));
				ci.setExpansionCapacity(Long.parseLong(capexInstanceData[4]));
				ci.setInUse(Boolean.parseBoolean(capexInstanceData[5]));
				cd.addCapexInstance(ci);
			}
			new CapexDAO().create(cd, scenarioId);			
		}
		
		
	}

	private void importCycleTImeData(int projectId, List<String[]> list) {
		String[] columns = list.get(0);
		columns = Arrays.copyOfRange(columns, 3, columns.length);
		
		List<String[]> data = new ArrayList<String[]>();
		
		for(int i= 1; i < list.size(); i++) {
			String[] row = list.get(i);
			data.add(Arrays.copyOfRange(row, 3, row.length));
		}
		
		CycletTimeDataProcessor processor = CycletTimeDataProcessor.getInstance();
		processor.setColumns(columns);
		processor.setData(data);
		processor.dumpToDB(projectId);
		List<Field> cycletimefields = new ArrayList<Field>();
		for(String column: processor.getColumns()){
			Field field = new Field(column);
			cycletimefields.add(field);
		}
		saveCycleTimeFields(projectId, cycletimefields);
	}

	private void saveCycleTimeFields(int projectId, List<Field> cycletimefields ) {
		String inset_sql = " insert into cycle_time_fields (project_id, name) values (?, ?) ";
		String delete_sql = " delete from cycle_time_fields where project_id = ? ";
		try (
				Connection connection = DBManager.getConnection();
	            PreparedStatement ps1 = connection.prepareStatement(inset_sql);
				PreparedStatement ps2 = connection.prepareStatement(delete_sql);
			){
			
			ps2.setInt(1, projectId);
			ps2.executeUpdate();
			for (Field field : cycletimefields) {
				ps1.setInt(1, projectId);
				ps1.setString(2, field.getName());
				ps1.executeUpdate();
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	private void importCSVData(int projectId, List<String[]> list) {
		String[] columns = list.get(0);
		columns = Arrays.copyOfRange(columns, 3, columns.length);
		
		List<String[]> data = new ArrayList<String[]>();
		
		for(int i= 1; i < list.size(); i++) {
			String[] row = list.get(i);
			data.add(Arrays.copyOfRange(row, 3, row.length));
		}
		try {
			createTable(projectId, columns);
			
			CSVDataProcessor processor = new CSVDataProcessor();
			processor.setColumns(columns);
			processor.setData(data);
			processor.dumpToDB(projectId);
			
			List<Expression> expressions = new ExpressionDAO().getAll(projectId);
			ExpressionProcessor eprocessor = new ExpressionProcessor();
			eprocessor.setExpressions(expressions);
			eprocessor.store(projectId);
			
			new PitBenchProcessor().updatePitBenchData(projectId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}


	private void dropTable(int projectId, Connection conn) throws SQLException {
		String  data_table_sql = "DROP TABLE IF EXISTS gnos_data_"+projectId+"; ";
		String  computed_data_table_sql = "DROP TABLE IF EXISTS gnos_computed_data_"+projectId+"; ";

		try (
				Statement stmt = conn.createStatement();
			)
		{
			stmt.executeUpdate(data_table_sql);
			stmt.executeUpdate(computed_data_table_sql);
		} 
		
	}
	
	private void createTable(int projectId, String[] headerColumns) throws SQLException {
		Connection conn = DBManager.getConnection();
		dropTable(projectId, conn);
		String  data_sql = "CREATE TABLE gnos_data_"+projectId+" (id INT NOT NULL AUTO_INCREMENT, ";
		
		for(int i =0; i< headerColumns.length; i++){
			String columnName = headerColumns[i].replaceAll("\\s+","_").toLowerCase();
			data_sql += columnName +" VARCHAR(50)";
			data_sql += ", ";
		}
		data_sql += " PRIMARY KEY ( id ) );";
		
		String  computed_data_sql = "CREATE TABLE gnos_computed_data_"+projectId+" (row_id INT NOT NULL, block_no INT, pit_no INT, bench_no INT, PRIMARY KEY ( row_id )) ";
		System.out.println("Sql =>"+data_sql);
		try (
				Statement stmt = conn.createStatement();
			)
		{			
			stmt.executeUpdate(data_sql);
			stmt.executeUpdate(computed_data_sql);
		} 
		
	}

}
