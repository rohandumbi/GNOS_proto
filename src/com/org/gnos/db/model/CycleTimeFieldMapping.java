package com.org.gnos.db.model;

public class CycleTimeFieldMapping {
	
	public static final short FIXED_FIELD_MAPPING = 1;
	public static final short PROCESS_FIELD_MAPPING = 2;
	public static final short DUMP_FIELD_MAPPING = 3;
	public static final short STOCKPILE_FIELD_MAPPING = 4;	
	
	private String fieldName;
	private short mappingType;
	private String mappedFieldName;
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public short getMappingType() {
		return mappingType;
	}
	public void setMappingType(short mappingType) {
		this.mappingType = mappingType;
	}
	public String getMappedFieldName() {
		return mappedFieldName;
	}
	public void setMappedFieldName(String mappedFieldName) {
		this.mappedFieldName = mappedFieldName;
	}
	@Override
	public String toString() {
		return fieldName + "|" + mappingType + "|" + mappedFieldName;
	}	
}
