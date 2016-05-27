package models;

import java.util.ArrayList;

public class Word {
	private String valueType;
	private String dataType;
	private String className;
	private String value;

	private ArrayList<ObjectRelation> objectRelation;
	
	public String getValueType() {
		return valueType;
	}
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public ArrayList<ObjectRelation> getObjectRelation() {
		return objectRelation;
	}
	public void setObjectRelation(ArrayList<ObjectRelation> objectRelation) {
		this.objectRelation = objectRelation;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

}
