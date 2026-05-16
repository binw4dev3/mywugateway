package com.wu.gw.model.das;

import java.util.ArrayList;

public class DeliveryOptionTemplateRecord extends AbstractDASRecord {

	public final static String PRODUCT = "Product";
	public final static String PRODUCT_field = "PRODUCT";
	public final static String CATEGORY = "Category";
	public final static String CATEGORY_field = "CATEGORY";
	public final static String T_INDEX = "T Index";
	public final static String T_INDEX_field = "T_INDEX";
	public final static String DESCRIPTION = "Description";
	public final static String DESCRIPTION_field = "DESCRIPTION";
	
	private static ArrayList<String> ColomnHeader = new ArrayList<String>();
	
	static {
		ColomnHeader.add(PRODUCT);
		ColomnHeader.add(CATEGORY);
		ColomnHeader.add(T_INDEX);
		ColomnHeader.add(DESCRIPTION);
	}

	public DeliveryOptionTemplateRecord() {
	}
	
	public String toString() {
		return getColumnData().get(T_INDEX) + "\t" + getColumnData().get(DESCRIPTION);
	}
}
