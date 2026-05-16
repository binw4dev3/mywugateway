package com.wu.gw.model;

import java.util.HashMap;

public class APNCascadeDropDownList {

	private String cascadeListName;
	
	private APNTemplateDataEntry dataEntry;

	private HashMap<String, APNCascadeDropDownList> subCascadeDropDownList;
	
	public String getCascadeListName() {
		return cascadeListName;
	}

	public void setCascadeListName(String cascadeListName) {
		this.cascadeListName = cascadeListName;
	}

	public APNTemplateDataEntry getDataEntry() {
		return dataEntry;
	}

	public void setDataEntry(APNTemplateDataEntry dataEntry) {
		this.dataEntry = dataEntry;
	}

	public HashMap<String, APNCascadeDropDownList> getSubCascadeDropDownList() {
		return subCascadeDropDownList;
	}

	public void setSubCascadeDropDownList(HashMap<String, APNCascadeDropDownList> subCascadeDropDownList) {
		this.subCascadeDropDownList = subCascadeDropDownList;
	}
}
