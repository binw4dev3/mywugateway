package com.wu.gw.model.das;

import java.util.ArrayList;

public class CountriesCurrenciesRecord extends AbstractDASRecord {

	public final static String COUNTRY_NAME = "Country Name";
	public final static String COUNTRY_NAME_FIELD = "COUNTRY_LONG";
	public final static String COUNTRY_ID = "Country ID";
	public final static String COUNTRY_ID_FIELD = "ISO_COUNTRY_NUM_CD";
	public final static String COUNTRY_CODE = "Country Code";
	public final static String COUNTRY_CODE_FIELD = "ISO_COUNTRY_CD";
	public final static String CURRENCY_NAME = "Currency Name";
	public final static String CURRENCY_NAME_FIELD = "CURRENCY_NAME";
	public final static String CURRENCY_ID = "Currency ID";
	public final static String CURRENCY_ID_FIELD = "ISO_CURRENCY_NUM_CD";
	public final static String CURRENCY_CODE = "Currency Code";
	public final static String CURRENCY_CODE_FIELD = "CURRENCY_CD";
			
	private static ArrayList<String> ColomnHeader = new ArrayList<String>();
	
	static {
		ColomnHeader.add(COUNTRY_NAME);
		ColomnHeader.add(COUNTRY_ID);
		ColomnHeader.add(COUNTRY_CODE);
		ColomnHeader.add(CURRENCY_NAME);
		ColomnHeader.add(CURRENCY_ID);
		ColomnHeader.add(CURRENCY_CODE);
	}
	
	public CountriesCurrenciesRecord() {
	}
	
	@Override
	public ArrayList<String> getColomnHeader() {
		return ColomnHeader;
	}
	
	public String toString() {
		return getColumnData().get(COUNTRY_NAME) + "\t\t\t\t" + getColumnData().get(COUNTRY_ID) + "\t" + getColumnData().get(COUNTRY_CODE) + "\t" + 
				getColumnData().get(CURRENCY_NAME) + "\t\t\t\t" + getColumnData().get(CURRENCY_ID) + "\t" + getColumnData().get(CURRENCY_CODE);
	}
}
