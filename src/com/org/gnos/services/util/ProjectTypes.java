package com.org.gnos.services.util;

public interface ProjectTypes {

	final int PROJECT_IND = 1;
	final int SCENARIO_IND = 2;
	final int PROJECT_DATA_IND = 3;
	final int PROJECT_CYCLETIME_DATA_IND = 4;
	
	final int FIELD_IND = 11;
	final int REQ_FIELD_IND = 12;
	final int MODEL_IND = 13;
	final int EXPRESSION_IND = 14;
	final int PROCESS_IND = 15;
	final int PRODUCT_IND = 16;
	final int PRODUCT_JOIN_IND = 17;
	final int PROCESS_JOIN_IND = 18;
	final int PIT_GROUP_IND = 19;
	final int DUMP_IND = 20;
	final int STOCKPILE_IND = 21;
	final int TRUCKPARAM_PAYLOAD = 22;
	final int TRUCKPARAM_CYCLE_TIME = 23;
	final int CYCLE_FIXED_TIME = 24;
	final int CYCLE_TIME_FIELD_MAPPING = 25;
	
	final int SCN_OPEX = 201;
	final int SCN_FIXED_COST = 202;
	final int SCN_PROCESS_CONSTRAINT = 203;
	final int SCN_BENCH_CONSTRAINT = 204;
	final int SCN_GRADE_CONSTRAINT = 205;
	final int SCN_PIT_DEPENDENCY = 206;
	final int SCN_DUMP_DEPENDENCY = 207;
	final int SCN_CAPEX = 208;
}
