package com.org.gnos.equation;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.gnos.core.Bench;
import com.org.gnos.core.Pit;

public class BinaryVariableGenerator extends EquationGenerator{

	public BinaryVariableGenerator(InstanceData data) {
		super(data);
	}
	
	@Override
	public void generate() {
		
		int bufferSize = 8 * 1024;
		try {
			output = new BufferedOutputStream(new FileOutputStream("binaryVariable.txt"), bufferSize);
			bytesWritten = 0;
			buildBinaryVariables();
			output.flush();
			output.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public void buildBinaryVariables() {
		
		int timePeriod = scenarioConfigutration.getTimePeriod();
		Map<Integer, Pit> pits = serviceInstanceData.getPits();
		Set<Integer> pitNos = pits.keySet();
		for(Integer pitNo:pitNos){
			Pit pit = pits.get(pitNo);
			List<Bench> benches = pit.getBenches();
			for(Bench b: benches){
				for(int i=1; i<= timePeriod;i++){
					String eq = "p"+pitNo+"b"+b.getBenchNo()+"t"+i+" ";
					write(eq);
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
