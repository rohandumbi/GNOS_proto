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
		if(created) return obj;
		throw new Exception();
	}
	
	
	public Scenario update(JsonObject jsonObject, String id) throws Exception {		
		String name = jsonObject.get("name").getAsString();
		int startYear = jsonObject.get("start_year").getAsInt();
		int timePeriod = jsonObject.get("time_period").getAsInt();
		float discount = jsonObject.get("discount").getAsFloat();
		Scenario obj = new Scenario();
		obj.setId(Integer.parseInt(id));
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
			Scenario obj = new Scenario();
			obj.setId(Integer.parseInt(id));
			dao.delete(obj);
			return true;
		}	
	}
}
