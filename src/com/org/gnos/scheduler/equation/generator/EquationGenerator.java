package com.org.gnos.scheduler.equation.generator;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.org.gnos.core.GNOSConfig;
import com.org.gnos.core.Pit;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Product;
import com.org.gnos.db.model.ProductJoin;
import com.org.gnos.scheduler.equation.ExecutionContext;
import com.org.gnos.scheduler.solver.ISolver;

public abstract class EquationGenerator {
	
	static final int BYTES_PER_LINE = 256;
	private static int DECIMAL_POINT = 6;
	
	protected ISolver solver;
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


	protected Set<String> getProductsFromProductJoin(ProductJoin pj) {
		Set<String> products = new HashSet<String>();
		if(pj == null) return products;
		products.addAll(pj.getProductList());
		if(pj.getProductJoinList().size() > 0){
			for(String productJoinName : pj.getProductJoinList()) {
				ProductJoin pji = context.getProductJoinFromName(productJoinName);
				products.addAll(getProductsFromProductJoin(pji));
			}
		}
		return products;
	}

	protected Set<Integer> getPitsFromPitGroup(PitGroup pg) {
		Set<Integer> pitNumbers = new HashSet<Integer>();
		if(pg == null) return pitNumbers;
		for(String pitName: pg.getListChildPits()){
			Pit pit = context.getPitNameMap().get(pitName);
			pitNumbers.add(pit.getPitNo());
		}
		for(String pitGroupName: pg.getListChildPitGroups()){
			PitGroup pitGroup = context.getPitGroupfromName(pitGroupName);
			pitNumbers.addAll(getPitsFromPitGroup(pitGroup));
		}		
		return pitNumbers;
	}
	
	protected Set<Integer> getProcessListFromProductJoin(ProductJoin pj){
		Set<Integer> processes = new HashSet<Integer>();
		 for(String productName: pj.getProductList()){
			 Product childProduct = context.getProductFromName(productName);
			 processes.add(childProduct.getModelId());
		 }
		 for(String productJoinName: pj.getProductJoinList()) {
			 ProductJoin childJoin = context.getProductJoinFromName(productJoinName);
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

	public ISolver getSolver() {
		return solver;
	}

	public void setSolver(ISolver solver) {
		this.solver = solver;
	}

	public BufferedOutputStream getOutput() {
		return output;
	}

	public void setOutput(BufferedOutputStream output) {
		this.output = output;
	}


	// Abstract method generate
	public abstract void generate();
}
