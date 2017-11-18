package com.org.gnos.services.controller;

import java.util.List;
import java.util.Random;

import com.google.gson.JsonObject;
import com.org.gnos.db.dao.BenchConstraintDAO;
import com.org.gnos.db.dao.CapexDAO;
import com.org.gnos.db.dao.DumpDependencyDAO;
import com.org.gnos.db.dao.FixedCostDAO;
import com.org.gnos.db.dao.GradeConstraintDAO;
import com.org.gnos.db.dao.OpexDAO;
import com.org.gnos.db.dao.PitDependencyDAO;
import com.org.gnos.db.dao.ProcessConstraintDAO;
import com.org.gnos.db.dao.ScenarioDAO;
import com.org.gnos.db.model.CapexData;
import com.org.gnos.db.model.DumpDependencyData;
import com.org.gnos.db.model.FixedOpexCost;
import com.org.gnos.db.model.GradeConstraintData;
import com.org.gnos.db.model.OpexData;
import com.org.gnos.db.model.PitBenchConstraintData;
import com.org.gnos.db.model.PitDependencyData;
import com.org.gnos.db.model.ProcessConstraintData;
import com.org.gnos.db.model.Scenario;

public class ScenarioController {

	ScenarioDAO dao;
	
	public ScenarioController() {
		dao = new ScenarioDAO();
	}
	public List<Scenario> getAll(String projectIdStr) {
		int projectId = Integer.parseInt(projectIdStr);
		return dao.getAll(projectId);
	}
	
	public Scenario create(JsonObject jsonObject, String pid) throws Exception {
		String name = jsonObject.get("name").getAsString();
		int startYear = jsonObject.get("startYear").getAsInt();
		int timePeriod = jsonObject.get("timePeriod").getAsInt();
		float discount = jsonObject.get("discount").getAsFloat();
		Scenario obj = new Scenario();
		obj.setName(name);
		obj.setStartYear(startYear);
		obj.setTimePeriod(timePeriod);
		obj.setDiscount(discount);
		boolean created = dao.create(obj, Integer.parseInt(pid));
		if(created) {
			// Add fixed Cost entries here 
			FixedCostDAO fcdao = new FixedCostDAO();
			for (int i= 0; i < 5; i++) {
				FixedOpexCost foc = new FixedOpexCost();
				foc.setCostType(i);
				foc.setSelectionType(-1);
				foc.setDefault(true);
				foc.setInUse(true);
				fcdao.create(foc, obj.getId());
				fcdao.addYears(foc.getId(),  obj.getStartYear(),  obj.getStartYear()+obj.getTimePeriod());
			}
			return obj;
		}
		throw new Exception();
	}
	
	
	public Scenario update(JsonObject jsonObject, String id) throws Exception {		
		String name = jsonObject.get("name").getAsString();
		int startYear = jsonObject.get("startYear").getAsInt();
		int timePeriod = jsonObject.get("timePeriod").getAsInt();
		float discount = jsonObject.get("discount").getAsFloat();
		
		Scenario obj = dao.get(Integer.parseInt(id));
		if(obj == null) {
			throw new Exception("Nothing to update");
		} else {
			if(timePeriod > obj.getTimePeriod()) {
				int nstartYear = obj.getStartYear() + obj.getTimePeriod();
				int nendYear =  obj.getStartYear() + timePeriod -1;
				createEntries(obj.getId(), nstartYear, nendYear);
			} else {
				int endYear =  obj.getStartYear() + timePeriod -1;
				removeEntries(obj.getId(), endYear);
			}
		}
		
		obj.setName(name);
		obj.setStartYear(startYear);
		obj.setTimePeriod(timePeriod);
		obj.setDiscount(discount);
		boolean created = dao.update(obj);
		if(created) return obj;
		throw new Exception();
	}
	

	public Scenario copy(String pid, String sid) throws Exception{
		if((sid == null) || (sid.isEmpty())){
			throw new Exception("Please select a scenario");
		}else{
			int scenarioId = Integer.parseInt(sid);
			int random = new Random().nextInt(1000);
			Scenario obj = dao.get(scenarioId);
			obj.setId(-1);
			obj.setName(obj.getName()+"-Copy-"+random);
			boolean created = dao.create(obj, Integer.parseInt(pid));
			if(!created) {
				throw new Exception("Could not copy scenario. Please contact your administrator.");
			}
			// Copy all constraints
			//Opex
			OpexDAO opexDao = new OpexDAO();
			List<OpexData> opexDataList = opexDao.getAll(scenarioId);
			for(OpexData od: opexDataList) {
				od.setId(-1);
				opexDao.create(od, obj.getId());
			}
			//FixedCost 
			FixedCostDAO fixedCostDao = new FixedCostDAO();
			List<FixedOpexCost> fixedCostList = fixedCostDao.getAll(scenarioId);
			for(FixedOpexCost foc : fixedCostList) {
				foc.setId(-1);
				fixedCostDao.create(foc, obj.getId());
			}
			//Process Constraints 
			ProcessConstraintDAO processConstraintDao = new ProcessConstraintDAO();
			List<ProcessConstraintData> processConstraintList = processConstraintDao.getAll(scenarioId);
			for(ProcessConstraintData pcd: processConstraintList) {
				pcd.setId(-1);
				processConstraintDao.create(pcd, obj.getId());
			}
			//Bench Cobstraints
			BenchConstraintDAO benchConstraintDao = new BenchConstraintDAO();
			List<PitBenchConstraintData> benchConstraintList = benchConstraintDao.getAll(scenarioId);
			for(PitBenchConstraintData pbcd: benchConstraintList) {
				pbcd.setId(-1);
				benchConstraintDao.create(pbcd, obj.getId());
			}			
			//Grade Cobstraints
			GradeConstraintDAO gradeConstraintDao = new GradeConstraintDAO();
			List<GradeConstraintData> gradeConstraintList = gradeConstraintDao.getAll(scenarioId);
			for(GradeConstraintData gcd: gradeConstraintList) {
				gcd.setId(-1);
				gradeConstraintDao.create(gcd, obj.getId());
			}
			//Pit dependency
			PitDependencyDAO pitDependencyDao = new PitDependencyDAO();
			List<PitDependencyData> pitDependencyList = pitDependencyDao.getAll(scenarioId);
			for(PitDependencyData pdd: pitDependencyList) {
				pdd.setId(-1);
				pitDependencyDao.create(pdd, obj.getId());
			}			
			//Dump dependency
			DumpDependencyDAO dumpDependencyDao = new DumpDependencyDAO();
			List<DumpDependencyData> dumpDependencyList = dumpDependencyDao.getAll(scenarioId);
			for(DumpDependencyData ddd: dumpDependencyList) {
				ddd.setId(-1);
				dumpDependencyDao.create(ddd, obj.getId());
			}
			//Capex
			CapexDAO capexDao = new CapexDAO();
			List<CapexData> CapexDataList = capexDao.getAll(scenarioId);
			for(CapexData cd: CapexDataList) {
				cd.setId(-1);
				capexDao.create(cd, obj.getId());
			}
			return obj;
		}	
	}
	
	public boolean delete(String id) {
		if((id == null) || (id.isEmpty())){
			return false;
		}else{
			int scenarioId = Integer.parseInt(id);
			Scenario obj = new Scenario();
			obj.setId(scenarioId);
			dao.delete(obj);
			// Delete all realted config
			//Opex
			OpexDAO opexDao = new OpexDAO();
			opexDao.delete(scenarioId);
			//FixedCost 
			FixedCostDAO fixedCostDao = new FixedCostDAO();
			fixedCostDao.delete(scenarioId);
			//Process Constraints 
			ProcessConstraintDAO processConstraintDao = new ProcessConstraintDAO();
			processConstraintDao.delete(scenarioId);
			//Bench Cobstraints
			BenchConstraintDAO benchConstraintDao = new BenchConstraintDAO();
			benchConstraintDao.delete(scenarioId);		
			//Grade Cobstraints
			GradeConstraintDAO gradeConstraintDao = new GradeConstraintDAO();
			gradeConstraintDao.delete(scenarioId);
			//Pit dependency
			PitDependencyDAO pitDependencyDao = new PitDependencyDAO();
			pitDependencyDao.delete(scenarioId);		
			//Dump dependency
			DumpDependencyDAO dumpDependencyDao = new DumpDependencyDAO();
			dumpDependencyDao.delete(scenarioId);
			//Capex
			CapexDAO capexDao = new CapexDAO();
			capexDao.delete(scenarioId);
			return true;
		}	
	}
	
	private void removeEntries(int scenarioId, int endYear) {
		//Opex
		OpexDAO opexDao = new OpexDAO();
		List<OpexData> opexDataList = opexDao.getAll(scenarioId);
		for(OpexData od: opexDataList) {
			opexDao.deleteYears(od.getId(), endYear);
		}
		//FixedCost 
		FixedCostDAO fixedCostDao = new FixedCostDAO();
		List<FixedOpexCost> fixedCostList = fixedCostDao.getAll(scenarioId);
		for(FixedOpexCost foc : fixedCostList) {
			fixedCostDao.deleteYears(foc.getId(), endYear);
		}
		//Process Constraints 
		ProcessConstraintDAO processConstraintDao = new ProcessConstraintDAO();
		List<ProcessConstraintData> processConstraintList = processConstraintDao.getAll(scenarioId);
		for(ProcessConstraintData pcd: processConstraintList) {
			processConstraintDao.deleteYears(pcd.getId(), endYear);
		}
		//Bench Cobstraints
		BenchConstraintDAO benchConstraintDao = new BenchConstraintDAO();
		List<PitBenchConstraintData> benchConstraintList = benchConstraintDao.getAll(scenarioId);
		for(PitBenchConstraintData pbcd: benchConstraintList) {
			benchConstraintDao.deleteYears(pbcd.getId(), endYear);
		}			
		//Grade Cobstraints
		GradeConstraintDAO gradeConstraintDao = new GradeConstraintDAO();
		List<GradeConstraintData> gradeConstraintList = gradeConstraintDao.getAll(scenarioId);
		for(GradeConstraintData gcd: gradeConstraintList) {
			gradeConstraintDao.deleteYears(gcd.getId(), endYear);
		}

		
	}
	private void createEntries(int scenarioId, int startYear, int endYear) {
		//Opex
		OpexDAO opexDao = new OpexDAO();
		List<OpexData> opexDataList = opexDao.getAll(scenarioId);
		for(OpexData od: opexDataList) {
			opexDao.addYears(od.getId(), startYear, endYear);
		}
		//FixedCost 
		FixedCostDAO fixedCostDao = new FixedCostDAO();
		List<FixedOpexCost> fixedCostList = fixedCostDao.getAll(scenarioId);
		for(FixedOpexCost foc : fixedCostList) {
			fixedCostDao.addYears(foc.getId(), startYear, endYear);
		}
		//Process Constraints 
		ProcessConstraintDAO processConstraintDao = new ProcessConstraintDAO();
		List<ProcessConstraintData> processConstraintList = processConstraintDao.getAll(scenarioId);
		for(ProcessConstraintData pcd: processConstraintList) {
			processConstraintDao.addYears(pcd.getId(), startYear, endYear);
		}
		//Bench Cobstraints
		BenchConstraintDAO benchConstraintDao = new BenchConstraintDAO();
		List<PitBenchConstraintData> benchConstraintList = benchConstraintDao.getAll(scenarioId);
		for(PitBenchConstraintData pbcd: benchConstraintList) {
			benchConstraintDao.addYears(pbcd.getId(), startYear, endYear);
		}			
		//Grade Cobstraints
		GradeConstraintDAO gradeConstraintDao = new GradeConstraintDAO();
		List<GradeConstraintData> gradeConstraintList = gradeConstraintDao.getAll(scenarioId);
		for(GradeConstraintData gcd: gradeConstraintList) {
			gradeConstraintDao.addYears(gcd.getId(), startYear, endYear);
		}
	}
}
