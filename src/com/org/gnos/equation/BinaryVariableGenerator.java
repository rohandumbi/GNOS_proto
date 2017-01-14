package com.org.gnos.equation;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Bench;
import com.org.gnos.core.Pit;
import com.org.gnos.db.model.CapexData;
import com.org.gnos.db.model.CapexInstance;

public class BinaryVariableGenerator extends EquationGenerator{

	public BinaryVariableGenerator(EquationContext data) {
		super(data);
	}
	
	@Override
	public void generate() {
		
		int bufferSize = 8 * 1024;
		try {
			output = new BufferedOutputStream(new FileOutputStream("binaryVariable.txt"), bufferSize);
			bytesWritten = 0;
			buildBinaryVariables();
			buildCapexBinaryVariables();
			output.flush();
			output.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public void buildBinaryVariables() {
		
		int timePeriod = context.getTimePeriod();
		Map<Integer, Pit> pits = context.getPits();
		Set<Integer> pitNos = pits.keySet();
		for(Integer pitNo:pitNos){
			Pit pit = pits.get(pitNo);
			Set<Bench> benches = pit.getBenches();
			for(Bench b: benches){
				for(int i=1; i<= timePeriod;i++){
					String eq = "p"+pitNo+"b"+b.getBenchNo()+"t"+i+" ";
					write(eq);
				}
			}
		}
		
	}
	
	public void buildCapexBinaryVariables() {
		int timeperiod = context.getTimePeriod();
		List<CapexData> capexDataList = context.getScenarioConfig().getCapexDataList();
		int capexCount = 0;
		for(CapexData cd: capexDataList) {
			capexCount++;
			List<CapexInstance> capexInstanceList = cd.getListOfCapexInstances();
			for (int j = 1; j <= capexInstanceList.size(); j++) {
				for(int i= 1; i <= timeperiod ; i++){
					String cv = " c"+capexCount+"i"+j+"t"+i;
					write(cv);
				}
			}
		}
	}

	@Override
	protected void write(String s) {

		try {
			byte[] bytes = s.getBytes();
			if(bytes.length + bytesWritten > BYTES_PER_LINE){
				output.write("\r\n".getBytes());
				output.flush();
				bytesWritten = 0;
			}
			output.write(bytes);
			bytesWritten = bytesWritten + bytes.length;
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}	
}
