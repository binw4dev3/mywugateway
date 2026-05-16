package com.wu.gw.model.das;

import java.util.ArrayList;

public class MexicoCityStateRecord extends AbstractDASRecord {

	public final static String STATE_NAME = "State Name";
	public final static String STATE_NAME_FIELD = "STATE_NAME";
	public final static String STATE_CODE = "State Code";
	public final static String STATE_CODE_FIELD = "STATE_CODE";
	public final static String CITY = "City";
	public final static String CITY_FIELD = "CITY";
	
	private static ArrayList<String> ColomnHeader = new ArrayList<String>();
	
	static {
		ColomnHeader.add(STATE_CODE);
		ColomnHeader.add(STATE_NAME);
		ColomnHeader.add(CITY);
	}

	public MexicoCityStateRecord() {
	}
	
	@Override
	public ArrayList<String> getColomnHeader() {
		return ColomnHeader;
	}
	
	public String toString() {
		return getColumnData().get(STATE_NAME) + "\t\t\t\t" + getColumnData().get(STATE_CODE) + "\t" + getColumnData().get(CITY);
	}
}
