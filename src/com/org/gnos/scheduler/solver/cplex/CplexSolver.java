package com.org.gnos.scheduler.solver.cplex;

import ilog.concert.IloException;
import ilog.concert.IloLPMatrix;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import com.org.gnos.scheduler.solver.ISolver;

public class CplexSolver implements ISolver {

	public void solve(String fileName) {
		File file = new File("C:\\result.csv");
		
		
		
		try {
			PrintWriter output = new PrintWriter(file);
			IloCplex cplex = new IloCplex();
			cplex.tuneParam();
			cplex.setParam(IloCplex.Param.MIP.Tolerances.MIPGap, 0.1);
			//cplex.setParam(IloCplex.IntParam.BrDir,1); // 1=up branch first, 0= automatic, -1=down branch selected first 
			//cplex.setParam(IloCplex.Param.MIP.Cuts.MIRCut,2); // set to agressive cut
			//cplex.setParam(IloCplex.Param.Emphasis.MIP,1);
			//cplex.setParam(IloCplex.Param.MIP.Strategy.Probe,3); //-1 no probing, 0=default,1=moderate,2=agressive,3=very aggressive
			cplex.setParam(IloCplex.Param.Read.Scale,1); // decides how to scale the problem matrix -1=no scaling, 0=equilibrium (default), 1=agressive
			
			System.out.println("cplex version: " +cplex.getVersion());
			
			
			// variables
			IloNumVar[] _vars;
						
			cplex.importModel(fileName);
			
			IloLPMatrix lp = (IloLPMatrix)cplex.LPMatrixIterator().next();
		       
	       _vars=lp.getNumVars();
	         
			// solve
			
			if(cplex.solve()){
				System.out.println("Obj = "+cplex.getObjValue());
				for (int i=0; i<_vars.length; i++) {
					String ccc=String.valueOf(_vars[i]);
					if(ccc.indexOf("x")!=-1 && cplex.getValue(_vars[i])> 0){
						output.println(rect(ccc) + "," + cplex.getValue(_vars[i]));
					}
				}
				output.close();
			}
			
			else {
				System.out.println("Model not solved");
			}
			
		}
		catch (IloException exc) {
			exc.printStackTrace();
		} // catch cplex
		
		catch (IOException ex) {
			System.out.printf("error", ex);
		}
	} // public static void
	
	
	// process variables
	public static String rect(String x){
		String ret_str = null;
		String block,pit,process,period;
		int xpos=x.indexOf("x");
		int wpos=x.indexOf("w");
		int spos=x.indexOf("s");
		int pitpos=x.indexOf("p");
		int tpos=x.indexOf("t");
		
		if(xpos!=-1){  // all variables except binaries
			
			if(wpos!=-1){
				//it's a waste variable
				
				block=x.substring(xpos+1, wpos);
				pit=x.substring(1, xpos);
				process=x.substring(wpos,tpos);
				period=x.substring(tpos+1);
				ret_str=pit + "," + block + "," + process + "," + period;
				
			}
			
			else if(spos!=-1){
				//it's a stockpile variable
		
				block=x.substring(xpos+1, spos);
				pit=x.substring(1, xpos);
				process=x.substring(spos,tpos);
				period=x.substring(tpos+1);
				ret_str=pit + "," + block + "," + process + "," + period;
			}
			else {
				//it's a process variable - non waste and non stockpile 
				int prpos=x.indexOf("p", 1);  //look for 2nd "p", first p is at 0.
				
				block=x.substring(xpos+1, prpos);
				pit=x.substring(1, xpos);
				process=x.substring(prpos,tpos);
				period=x.substring(tpos+1);
				ret_str=pit + "," + block + "," + process + "," + period;
				
				
			}
			
		}
		
		return ret_str;
	}
}
