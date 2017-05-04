package com.org.gnos.scheduler.solver.cplex;

import ilog.concert.IloException;
import ilog.concert.IloLPMatrix;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.List;

import com.org.gnos.scheduler.processor.FileStorageHelper;
import com.org.gnos.scheduler.processor.IStorageHelper;
import com.org.gnos.scheduler.processor.Record;
import com.org.gnos.scheduler.solver.ISolver;

public class CplexSolver implements ISolver {
	
	private IStorageHelper helper;
	private IloCplex cplex1;

	public CplexSolver() {
		try {
			cplex1 = new IloCplex();
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void solve(String fileName, int timePeiod) {
         test();     
        try {
        	IloCplex cplex = new IloCplex();
			cplex.tuneParam();
			cplex.setParam(IloCplex.Param.MIP.Tolerances.MIPGap, 0.1);
			cplex.setParam(IloCplex.Param.Read.Scale,1); // decides how to scale the problem matrix -1=no scaling, 0=equilibrium (default), 1=aggressive
			   
			System.out.println("cplex version: " +cplex.getVersion());
			   
			   
			// variables
			IloNumVar[] _vars;                    
			System.out.println("Optimising " + fileName);
            cplex.importModel(fileName);
           
           
            IloLPMatrix lp = (IloLPMatrix)cplex.LPMatrixIterator().next();
  
            _vars=lp.getNumVars();

            // solve
			   
			if(cplex.solve()){
				System.out.println("Obj = "+cplex.getObjValue());
				cplex.writeSolution("temp_"+timePeiod+".sol");
				List<Record> records = new ArrayList<Record>();
				for (int i=0; i<_vars.length; i++) {
					String ccc=String.valueOf(_vars[i]);
					if(ccc.indexOf("x")!=-1 && cplex.getValue(_vars[i])> 0){
						Record record = parse(ccc, timePeiod);
						if(record == null) continue;
						record.setValue(cplex.getValue(_vars[i]));
						records.add(record);
					}
				} //for int
				helper.store(records);
			} //if solve
			else {
			    System.out.println("Model not solved");
			}
			   
			//cplex.exportModel("test.lp");
                       
        }
        catch (IloException exc) {
        	exc.printStackTrace();
        } // catch cplex
	} // public static void

	public static Record parse(String x, int timePeiod){
		
		Record rec = new Record();
        String block,pit,process;
        int period;
        int xpos=x.indexOf("x");
        int wpos=x.indexOf("w");
        int spos=x.indexOf("s",2); // stockpile block, look for "s" after position 1 as "s" at pos 1 = reclaim
        int rcpos=x.indexOf("s"); // stockpile reclaim
        int tpos=x.indexOf("t");
        int prpos=x.indexOf("p", 2);  //look for 2nd "p", first p is at 0.
        //int dpos=x.indexOf("d"); // dump reclaim variable
        
        period= Integer.parseInt(x.substring(tpos+1));
        
        if(timePeiod > 0 && timePeiod != period) {
        	return null;
        }
        	
       
        if(xpos!=-1){  // all variables except binaries
                       
            if(wpos!=-1){
                //it's a waste variable
                System.out.println("w: " +x);
                block=x.substring(xpos+1, wpos);
                pit=x.substring(1, xpos);
                process=x.substring(wpos+1,tpos);
                
                rec.setOriginType(Record.ORIGIN_PIT);
                rec.setPitNo(Integer.parseInt(pit));
                rec.setBlockNo(Integer.parseInt(block));
                rec.setDestinationType(Record.DESTINATION_WASTE);
                rec.setWasteNo(Integer.parseInt(process));
                rec.setTimePeriod(period);                                      
            }         
            else if(spos > 1){
                //it's a stockpile variable
                System.out.println("sp: " +x);
                block=x.substring(xpos+1, spos);
                pit=x.substring(1, xpos);
                process=x.substring(spos+1,tpos);
                
                rec.setOriginType(Record.ORIGIN_PIT);
                rec.setPitNo(Integer.parseInt(pit));
                rec.setBlockNo(Integer.parseInt(block));
                rec.setDestinationType(Record.DESTINATION_SP);
                rec.setDestSpNo(Integer.parseInt(process));
                rec.setTimePeriod(period);
            }
            else if(rcpos == 0){
                //it's a reclaim variable for global mode
                System.out.println("rc: " +x);
                //int prpos=x.indexOf("p", 2);  //look for 2nd "p", first p is at 1.
                block=x.substring(xpos+1, prpos);
                pit=x.substring(2, xpos);
                process=x.substring(prpos+1,tpos);
                
                rec.setOriginType(Record.ORIGIN_SP);
                rec.setOriginSpNo(Integer.parseInt(pit));
                rec.setBlockNo(Integer.parseInt(block));
                rec.setDestinationType(Record.DESTINATION_PROCESS);
                rec.setProcessNo(Integer.parseInt(process));
                rec.setTimePeriod(period);
            }
           /*
            else if(dpos == 0){
                //it's a dump reclaim variable for global mode
                System.out.println("dc: " +x);
                //int prpos=x.indexOf("p", 2);  //look for 2nd "p", first p is at 1.
                block=x.substring(xpos+1, wpos);
                pit=x.substring(1, xpos);
                process=x.substring(wpos,tpos);
                period=x.substring(tpos+1);
                ret_str=pit + "," + block + "," + process + "," + period;
            }
           
			*/
            else if(prpos != -1){
                //it's a process variable - non waste and non stockpile
                //int prpos=x.indexOf("p", 2);  //look for 2nd "p", first p is at 0.
                System.out.println("process: " +x);
                block=x.substring(xpos+1, prpos);
                pit=x.substring(1, xpos);
                process=x.substring(prpos+1,tpos);
               
                rec.setOriginType(Record.ORIGIN_PIT);
                rec.setPitNo(Integer.parseInt(pit));
                rec.setBlockNo(Integer.parseInt(block));
                rec.setDestinationType(Record.DESTINATION_PROCESS);
                rec.setProcessNo(Integer.parseInt(process));
                rec.setTimePeriod(period);
            }             
        }

        return rec;
	}
	
	public static void main(String[] args) {
		//new CplexSolver().solve("C:\\Users\\abandyopadhy\\Downloads\\gnos\\merged_maximixe\\merged_maximixe.lp", -1);
		CplexSolver solver = new CplexSolver();
		solver.setStorageHelper(new FileStorageHelper());
		solver.solve("gtp_dump_1.lp", -1);
	}

	@Override
	public void setStorageHelper(IStorageHelper helper) {
		this.helper = helper;		
	}
	public IloCplex getCplex() {
		return cplex1;
	}
	public void setCplex(IloCplex cplex) {
		this.cplex1 = cplex;
	}
}
