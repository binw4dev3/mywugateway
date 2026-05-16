package com.wu.gw.model.das;

import java.util.ArrayList;

public class DASQueryResult {

	//public final static String GET_COUNTRIES_CURRENCIES = "GetCountriesCurrencies";
	
	private int recordNum = 0;
	
	private String dasName = null;
	
	private ArrayList<AbstractDASRecord> recordSet = new ArrayList<AbstractDASRecord>();
	
	private ArrayList<String> dataHeader = null;

	public void clear() {
		recordSet.clear();
		recordNum = 0;
	}
	
	public int getRecordNum() {
		return recordNum;
	}

	public void setRecordNum(int recordNum) {
		this.recordNum = recordNum;
	}

	public String getDasName() {
		return dasName;
	}

	public void setDasName(String dasName) {
		this.dasName = dasName;
	}

	public ArrayList<AbstractDASRecord> getRecordSet() {
		return recordSet;
	}

	public void setRecordSet(ArrayList<AbstractDASRecord> recordSet) {
		this.recordSet = recordSet;
	}

	public ArrayList<String> getDataHeader() {
		return dataHeader;
	}

	public void setDataHeader(ArrayList<String> dataHeader) {
		this.dataHeader = dataHeader;
	}
}
