package com.org.gnos.core;

public class ReportConstants {

	//Report Types
	public final static short TYPE_EXPIT 			= 1;
	public final static short TYPE_RECLAIM 			= 2;
	public final static short TYPE_PROCESS			= 3;
	public final static short TYPE_TOTAL_MOVEMENT 	= 4;
	
	//Data types
	public final static short DATA_UNIT_FIELD 		= 1;
	public final static short DATA_EXPRESSION 		= 2;
	public final static short DATA_PRODUCT 			= 3;
	public final static short DATA_PRODUCT_JOIN 	= 4;
	public final static short DATA_TOTAL_TH 		= 5;
	public final static short DATA_GRADE 			= 6;

	// Group By
	
	public final static short GROUP_NONE 			= 1;
	public final static short GROUP_INPUT_FIELD 	= 2;
	public final static short GROUP_PROCESS_JOIN 	= 3;
	public final static short GROUP_DESTINATION_TYPE = 4;
	public final static short GROUP_DESTINATION 	= 5;
	public final static short GROUP_PIT_GROUP 		= 6;
	public final static short GROUP_CUSTOM 			= 7;
	
}
