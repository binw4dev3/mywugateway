package com.wu.gw.model;

import java.util.ArrayList;
import java.util.HashMap;

public class APNTemplateDataEntry {
	
	private String fieldName;
	
	private String fieldType;
	
	private String required;
	
	private String maxLength;
	
	private String fieldCHIPID;
	
	private String fieldUABID;
	
	private Boolean isCascadeList = false;
	
	private String cascadeListName = "";
	
	private String cascadeListLevel = "";
	
	private int numOfDropdownOptions = 0;
	
	//private HashMap<String, APNDataDropDownOption> dropdownOptions;
	private ArrayList<String> dropdownOptions = new ArrayList<String>();

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	
	
	public ArrayList<String> getDropdownOptions() {
		return dropdownOptions;
	}
	
	public void addDropdownOption(String option) {
		dropdownOptions.add(option);
	}

	/*
	public HashMap<String, APNDataDropDownOption> getDropdownOptions() {
		return dropdownOptions;
	}
	
	public void addDropdownOption(String key, String value) {
		if(dropdownOptions == null) {
			dropdownOptions = new HashMap<String, APNDataDropDownOption>();
		}
		
		APNDataDropDownOption option = new APNDataDropDownOption();
		option.setOptionValue(key);
		option.setOptionText(value);
		
		dropdownOptions.put(key, option);
	}
	*/
	
	public String getRequired() {
		return required;
	}

	public void setRequired(String required) {
		this.required = required;
	}

	public String getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}

	public String getFieldCHIPID() {
		return fieldCHIPID;
	}

	public void setFieldCHIPID(String fieldCHIPID) {
		this.fieldCHIPID = fieldCHIPID;
	}

	public String getFieldUABID() {
		return fieldUABID;
	}

	public void setFieldUABID(String fieldUABID) {
		this.fieldUABID = fieldUABID;
	}

	public int getNumOfDropdownOptions() {
		return numOfDropdownOptions;
	}

	public void setNumOfDropdownOptions(int numOfDropdownOptions) {
		this.numOfDropdownOptions = numOfDropdownOptions;
	}

	public Boolean isCascadeList() {
		return isCascadeList;
	}

	public void setIsCascadeList(Boolean isCascadeList) {
		this.isCascadeList = isCascadeList;
	}

	public String getCascadeListName() {
		return cascadeListName;
	}

	public void setCascadeListName(String cascadeListName) {
		this.cascadeListName = cascadeListName;
	}

	public String getCascadeListLevel() {
		return cascadeListLevel;
	}

	public void setCascadeListLevel(String cascadeListLevel) {
		this.cascadeListLevel = cascadeListLevel;
	}
}
