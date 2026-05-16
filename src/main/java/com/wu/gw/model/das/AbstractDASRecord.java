package com.wu.gw.model.das;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractDASRecord {

	private HashMap<String, String> hashMap = new HashMap<String, String>();

	public ArrayList<String> getColomnHeader() {
		return null;
	}
	
	public void addColumnData(String column, String data) {
		hashMap.put(column, data);
	}
	
	public HashMap<String, String> getColumnData() {
		return hashMap;
	}
}
