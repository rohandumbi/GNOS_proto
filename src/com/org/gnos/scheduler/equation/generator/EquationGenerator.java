package com.org.gnos.scheduler.equation.generator;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.org.gnos.core.GNOSConfig;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Product;
import com.org.gnos.db.model.ProductJoin;
import com.org.gnos.scheduler.equation.ExecutionContext;

public abstract class EquationGenerator {
	
	static final int BYTES_PER_LINE = 256;
	private static int DECIMAL_POINT = 6;
	
	protected BufferedOutputStream output;
	protected ExecutionContext context = null;

	
	protected int bytesWritten = 0;
	
	static {
		String dp_format = GNOSConfig.get("format.dp");
		if(dp_format!= null && dp_format.trim().length()>0){
			DECIMAL_POINT = Integer.parseInt(dp_format);
		}
		System.out.println("DP value is "+DECIMAL_POINT);
	}
	
	public EquationGenerator(ExecutionContext data) {
		this.context = data;
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
		for(Integer p: pg.getListChildPits()){
			pitNumbers.add(p);
		}
		for(Integer pgi: pg.getListChildPitGroups()){
			//pitNumbers.addAll(getPitsFromPitGroup(pgi)); // Arpan hack
			
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
	
	protected String formatDecimalValue(BigDecimal bd) {
		BigDecimal a =  bd.setScale(DECIMAL_POINT , BigDecimal.ROUND_HALF_EVEN);
		return a.stripTrailingZeros().toString();
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
	
	public BufferedOutputStream getOutput() {
		return output;
	}


	public void setOutput(BufferedOutputStream output) {
		this.output = output;
	}


	// Abstract class
	public abstract void generate();
}
