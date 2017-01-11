package com.org.gnos.equation;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.org.gnos.core.Bench;
import com.org.gnos.core.Block;
import com.org.gnos.core.Pit;

public class BenchProportionEquationGenerator extends EquationGenerator{

	private String tonnesWeightFieldName;
	private Map<Integer, List<String>> blockVariableMapping;
	final Pattern lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
	
	public BenchProportionEquationGenerator(EquationContext data) {
		super(data);
		this.tonnesWeightFieldName = context.getTonnesWeightAlisName();
		this.blockVariableMapping = context.getBlockVariableMapping();
	}
	
	@Override
	public void generate() {
		
		int bufferSize = 8 * 1024;
		try {
			output = new BufferedOutputStream(new FileOutputStream("benchProportions.txt"), bufferSize);
			bytesWritten = 0;
			buildBenchProportionEquations();
			output.flush();
			output.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public void buildBenchProportionEquations() {
		Map<Integer, Pit> pits = context.getPits();
		int timePeriod = context.getTimePeriod();
		Set<Integer> pitNos = pits.keySet();
		for(int pitNo: pitNos){
			Pit pit = pits.get(pitNo);
			Set<Bench> benches = pit.getBenches();
			for(Bench bench: benches){
				List<Block> blocks = bench.getBlocks();
				Block lastBlock = null;
				for(Block block: blocks) {
					if(lastBlock != null) {
						List<String> blockvariables1 = this.blockVariableMapping.get(lastBlock.getId());
						List<String> blockvariables2 = this.blockVariableMapping.get(block.getId());
						String tonnage1 = lastBlock.getField(this.tonnesWeightFieldName);
						String tonnage2 = block.getField(this.tonnesWeightFieldName);
						for(int i=1; i<= timePeriod; i++){
							StringBuilder sb = new StringBuilder("");
							for(String variable : blockvariables1) {
								if(variable.startsWith("sp")) continue;
								Matcher matcher = lastIntPattern.matcher(variable);
								if (matcher.find()) {
								      String yearStr = matcher.group(1);
								      int year = Integer.parseInt(yearStr);
								      if(year != i ) continue;
								      if(sb.length() > 0) {
											sb.append(" + ");
								      }
									  sb.append(tonnage2+variable);
								}								
							}
							for(String variable : blockvariables2) {
								if(variable.startsWith("sp")) continue;
								Matcher matcher = lastIntPattern.matcher(variable);
								if (matcher.find()) {
								      String yearStr = matcher.group(1);
								      int year = Integer.parseInt(yearStr);
								      if(year != i ) continue;
								      sb.append(" - "+tonnage1+variable);
								}
							}
							if(sb.length() > 0){
								sb.append(" = 0");
								write(sb.toString());
							}
						}
					}
					lastBlock = block;
				}
			}
		}
	}
	
	@Override
	protected String formatDecimalValue(BigDecimal bd) {
		return bd.stripTrailingZeros().toString();
	}
}
