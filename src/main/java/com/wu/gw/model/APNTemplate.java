package com.wu.gw.model;

import java.util.ArrayList;
import java.util.HashMap;

public class APNTemplate {

	// Country code + Currency code, e.g. US USD
	private String corridor;
	
	private String serviceDesp;
	
	private String templateID;
	
	private String status = "available";
	
	private String serviceCode;
	
	private String route;
	
	private HashMap<String, ArrayList<String>> dropdownListMap = new HashMap<String, ArrayList<String>>();
	
	private HashMap<String, APNTemplateDataEntry> dataFields = new HashMap<String, APNTemplateDataEntry>();

	public String getCorridor() {
		return corridor;
	}

	public void setCorridor(String corridor) {
		this.corridor = corridor;
	}

	public String getServiceDesp() {
		return serviceDesp;
	}

	public void setServiceDesp(String serviceDesp) {
		this.serviceDesp = serviceDesp;
	}

	public HashMap<String, APNTemplateDataEntry> getDataFields() {
		return dataFields;
	}

	public APNTemplateDataEntry addDataField(String fieldName) {
		if(dataFields.containsKey(fieldName)) {
			return dataFields.get(fieldName);
		}
		
		APNTemplateDataEntry newField = new APNTemplateDataEntry();
		newField.setFieldName(fieldName);
		this.dataFields.put(fieldName, newField);
		
		return newField;
	}

	public String getTemplateID() {
		return templateID;
	}

	public void setTemplateID(String templateID) {
		this.templateID = templateID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public HashMap<String, ArrayList<String>> getDropdownListMap() {
		return dropdownListMap;
	}
}
