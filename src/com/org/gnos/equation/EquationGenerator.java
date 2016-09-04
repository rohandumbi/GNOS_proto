package com.org.gnos.equation;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.core.ScenarioConfigutration;
import com.org.gnos.db.model.Dump;
import com.org.gnos.db.model.Pit;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Product;
import com.org.gnos.db.model.ProductJoin;
import com.org.gnos.db.model.Stockpile;

public abstract class EquationGenerator {
	
	static final int BYTES_PER_LINE = 256;
	
	protected BufferedOutputStream output;
	protected ProjectConfigutration projectConfiguration;
	protected ScenarioConfigutration scenarioConfigutration;
	protected InstanceData serviceInstanceData = null;

	
	protected int bytesWritten = 0;
	
	public EquationGenerator(InstanceData data) {
		this.serviceInstanceData = data;
		this.projectConfiguration = ProjectConfigutration.getInstance();
		this.scenarioConfigutration = ScenarioConfigutration.getInstance();
	}


	protected List<Product> getProductsFromProductJoin(ProductJoin pj) {
		List<Product> products = new ArrayList<Product>();
		if(pj == null) return products;
		products.addAll(pj.getlistChildProducts());
		if(pj.getListChildProductJoins().size() > 0){
			for(ProductJoin pji: pj.getListChildProductJoins()) {
				products.addAll(getProductsFromProductJoin(pji));
			}
		}
		return products;
	}
	
	protected Set<Integer> getPitsFromPitGroup(PitGroup pg) {
		Set<Integer> pitNumbers = new HashSet<Integer>();
		if(pg == null) return pitNumbers;
		for(Pit p: pg.getListChildPits()){
			pitNumbers.add(p.getPitNumber());
		}
		for(PitGroup pgi: pg.getListChildPitGroups()){
			pitNumbers.addAll(getPitsFromPitGroup(pgi));
		}
		
		return pitNumbers;
	}
	protected Set<String> getProcessListFromProductJoin(ProductJoin pj){
		Set<String> processes = new HashSet<String>();
		 for(Product childProduct: pj.getlistChildProducts()){
			 processes.add(childProduct.getAssociatedProcess().getName());
		 }
		 for(ProductJoin childJoin: pj.getListChildProductJoins()) {
			 processes.addAll(getProcessListFromProductJoin(childJoin));
		 }
		 return processes;
	}
	
	protected void write(String s) {

		try {
			s = s +"\r\n";
			byte[] bytes = s.getBytes();
			output.write(bytes);
			output.flush();			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}	
	
	// Abstract class
	abstract void generate();
}
