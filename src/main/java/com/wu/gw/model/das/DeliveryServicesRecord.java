package com.wu.gw.model.das;

import java.util.ArrayList;

public class DeliveryServicesRecord extends AbstractDASRecord {

	public final static String DESTINATION_CODE = "Destination Code";
	public final static String DESTINATION_FIELD = "DESTINATION_CODE";
	public final static String SVC_CODE = "Service Code";
	public final static String SVC_CODE_FIELD = "SVC_CODE";
	public final static String SVC_NAME = "Service Name";
	public final static String SVC_NAME_FIELD = "SVC_NAME";
	public final static String ROUTE = "Routing Number";
	public final static String ROUTE_FIELD = "ROUTE";
	public final static String BANNER = "Banner";
	public final static String BANNER_FIELD = "BANNER";
	public final static String TEMPLT = "Template ID";
	public final static String TEMPLT_FIELD = "TEMPLT";
	public final static String DESCRIPTION = "Description";
	public final static String DESCRIPTION_FIELD = "DESCRIPTION";
	
	private static ArrayList<String> ColomnHeader = new ArrayList<String>();
	
	static {
		ColomnHeader.add(DESTINATION_CODE);
		ColomnHeader.add(SVC_CODE);
		ColomnHeader.add(SVC_NAME);
		ColomnHeader.add(ROUTE);
		ColomnHeader.add(BANNER);
		ColomnHeader.add(TEMPLT);
		ColomnHeader.add(DESCRIPTION);
	}
	
	public String toString() {
		return getColumnData().get(DESTINATION_CODE) + "\t" + getColumnData().get(SVC_CODE) + "\t" + getColumnData().get(SVC_NAME) + "\t" + 
				getColumnData().get(ROUTE) + "\t" + getColumnData().get(TEMPLT);
	}
}
