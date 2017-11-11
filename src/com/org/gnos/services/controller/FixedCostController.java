package com.org.gnos.services.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.org.gnos.db.DBManager;
import com.org.gnos.db.dao.FixedCostDAO;
import com.org.gnos.db.dao.PitGroupDAO;
import com.org.gnos.db.model.FixedOpexCost;
import com.org.gnos.db.model.PitGroup;

public class FixedCostController {

	private FixedCostDAO dao;
	
	public FixedCostController() {
		dao = new FixedCostDAO();
	}
	
	public List<FixedOpexCost> getAll(String scenarioIdStr) {
		int scenarioId = Integer.parseInt(scenarioIdStr);
		return dao.getAll(scenarioId);
	}
	
	public FixedOpexCost create(JsonObject jsonObject, String scenarioIdStr) throws Exception {
		int costType = jsonObject.get("costType").getAsInt();
		boolean inUse = jsonObject.get("inUse").getAsBoolean();
		boolean isDefault = jsonObject.get("isDefault").getAsBoolean();
		JsonObject costDataObj = jsonObject.get("costData").getAsJsonObject();
		int selectionType = jsonObject.get("selectionType").getAsInt();
		String selectorName = jsonObject.get("selectorName").getAsString();

		boolean isDuplicate = checkDuplicate(Integer.parseInt(scenarioIdStr), costType, selectionType, selectorName);
		if(isDuplicate) {
			throw new Exception("Can not create as you have duplicate configuration");
		}
		FixedOpexCost obj = new FixedOpexCost();
		obj.setCostType(costType);
		obj.setSelectionType(selectionType);
		obj.setSelectorName(selectorName);
		obj.setInUse(inUse);
		obj.setDefault(isDefault);
		
		for (Entry<String, JsonElement> costData : costDataObj.entrySet()) {
			obj.addCostData(Integer.parseInt(costData.getKey()), costData.getValue().getAsBigDecimal());
		}
		boolean created = dao.create(obj, Integer.parseInt(scenarioIdStr));
		if(created) return obj;
		throw new Exception();
	}
	

	public FixedOpexCost update(JsonObject jsonObject, String scenarioIdStr) throws Exception {		
		int id = jsonObject.get("id").getAsInt();
		int costType = jsonObject.get("costType").getAsInt();
		boolean inUse = jsonObject.get("inUse").getAsBoolean();
		boolean isDefault = jsonObject.get("isDefault").getAsBoolean();
		int selectionType = jsonObject.get("selectionType").getAsInt();
		String selectorName = null;//since this value can be null for default cost types
		if(jsonObject.get("selectorName") != null){
			selectorName = jsonObject.get("selectorName").getAsString();
		}
		JsonObject costDataObj = jsonObject.get("costData").getAsJsonObject();
		
		boolean isDuplicate = checkDuplicate(Integer.parseInt(scenarioIdStr), costType, selectionType, selectorName);
		if(isDuplicate) {
			throw new Exception("Can not create as you have duplicate configuration");
		}
		
		FixedOpexCost obj = new FixedOpexCost();
		obj.setId(id);
		obj.setCostType(costType);
		obj.setSelectionType(selectionType);
		obj.setSelectorName(selectorName);
		obj.setInUse(inUse);
		obj.setDefault(isDefault);
		
		for (Entry<String, JsonElement> costData : costDataObj.entrySet()) {
			obj.addCostData(Integer.parseInt(costData.getKey()), costData.getValue().getAsBigDecimal());
		}
		boolean created = dao.update(obj, Integer.parseInt(scenarioIdStr));
		if(created) return obj;
		throw new Exception();
	}
	
	public boolean delete(String scenarioIdStr) {
		dao.delete(Integer.parseInt(scenarioIdStr));
		return true;	
	}
	
	
	private boolean checkDuplicate(int scenarioId, int costType, int selectionType, String selectorName) {
		boolean isDuplicate = false;
		List<FixedOpexCost> fixedCostList = dao.getAll(scenarioId);
		int projectId = getProjectIdFromScenarioId(scenarioId);
		List<PitGroup> pitGroupList = new PitGroupDAO().getAll(projectId);
		List<String> names = new ArrayList<String>();
		for(FixedOpexCost foc: fixedCostList) {
			if(!foc.isDefault() && foc.getCostType() == costType) {
				if(foc.getSelectionType() == FixedOpexCost.SELECTOR_PIT || foc.getSelectionType() == FixedOpexCost.SELECTOR_STOCKPILE) {
					names.add(foc.getSelectorName());
				} else if(foc.getSelectionType() == FixedOpexCost.SELECTOR_PIT_GROUP) {
					PitGroup pitgroup = getPitGroupfromName(foc.getSelectorName(), pitGroupList);
					names.addAll(flattenPitGroup(pitgroup, pitGroupList));
				} 
			}
		}
		if(selectionType == FixedOpexCost.SELECTOR_PIT || selectionType == FixedOpexCost.SELECTOR_STOCKPILE) {
			if(names.contains(selectorName)) {
				isDuplicate = true;
			} 			
		} else if(selectionType == FixedOpexCost.SELECTOR_PIT_GROUP) {
			PitGroup pitgroup = getPitGroupfromName(selectorName, pitGroupList);
			Set<String> pitNames = flattenPitGroup(pitgroup, pitGroupList);
			for(String pitName: pitNames) {
				if(names.contains(pitName)) {
					isDuplicate = true;
					break;
				} 
			}
		}
		return isDuplicate;
	}

	private int getProjectIdFromScenarioId(int scenarioId) {
		int projectId = -1;
		try (
				Connection connection = DBManager.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("select project_id from scenario where id = "+scenarioId);
				){
			while(resultSet.next()){
				projectId = resultSet.getInt("project_id");			
			}

		} catch(SQLException e){
			e.printStackTrace();
		}
		
		return projectId;
	}
	
	public PitGroup getPitGroupfromName(String name, List<PitGroup> pitGroups) {
		if (pitGroups == null || pitGroups.size() == 0 || name == null)
			return null;

		for (PitGroup pitGroup : pitGroups) {
			if (pitGroup.getName().equals(name)) {
				return pitGroup;
			}
		}
		return null;
	}
	
	public Set<String> flattenPitGroup(PitGroup pg, List<PitGroup> pitGroups) {
		Set<String> pits = new HashSet<String>();
		pits.addAll(pg.getListChildPits());
		for (String childGroup : pg.getListChildPitGroups()) {
			pits.addAll(flattenPitGroup(getPitGroupfromName(childGroup, pitGroups), pitGroups));
		}

		return pits;
	}

}
