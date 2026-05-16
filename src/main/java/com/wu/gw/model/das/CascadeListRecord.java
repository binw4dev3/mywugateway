package com.wu.gw.model.das;

import java.util.ArrayList;
import java.util.HashMap;

public class CascadeListRecord extends AbstractDASRecord {

	public final static String LIST_VALUE = "List Value";
	public final static String LIST_VALUE_FIELD = "LIST_VALUE";
	public final static String LIST_TEXT = "List Text";
	public final static String LIST_TEXT_FIELD = "LIST_TEXT";
	
	private static ArrayList<String> ColomnHeader = new ArrayList<String>();
	
	static {
		ColomnHeader.add(LIST_VALUE);
		ColomnHeader.add(LIST_TEXT);
	}
	
	//private HashMap<String, AbstractDASRecord> subCombo = new HashMap<String, AbstractDASRecord>();
	private HashMap<String, AbstractDASRecord> subCombo;
	
	public CascadeListRecord() {
	}
	
	public HashMap<String, AbstractDASRecord> getSubCombo() {
		return subCombo;
	}

	public void setSubCombo(HashMap<String, AbstractDASRecord> subCombo) {
		this.subCombo = subCombo;
	}

	public String toString() {
		return getColumnData().get(LIST_VALUE) + " - " + getColumnData().get(LIST_TEXT);
	}
}
