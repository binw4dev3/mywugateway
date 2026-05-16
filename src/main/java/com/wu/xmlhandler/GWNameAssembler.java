package com.wu.xmlhandler;

import java.util.List;

public abstract class GWNameAssembler extends AbstractXMLAssembler {

	protected static String NAMETYPE_C = "C";
	protected static String NAMETYPE_M = "M";
	protected static String NAMETYPE_D = "D";
	
	public GWNameAssembler() {
		
		super();
	}
	
	@Override
	protected String generateXML(List<String> valueList) {
		
		if(valueList.size() < 2) {
			
			System.out.println("Invalid name value parameters");
			
			return null;
		}
		
		String nameType = valueList.get(0);
		
		if(nameType.equals(NAMETYPE_C)) {
			
			return nameCXML(valueList.get(1));
			
		}

		if(valueList.size() < 4) {
			
			System.out.println("Invalid name value parameters");
			
			return null;
		}
		
		if(nameType.equals(NAMETYPE_M)) {
			
			return nameMXML(valueList.get(1), valueList.get(2), valueList.get(3));
			
		} else if(nameType.equals(NAMETYPE_D)) {
			
			return nameDXML(valueList.get(1), valueList.get(2), valueList.get(3));
		}
		
		return null;
	}
	
	protected String nameCXML(String value1) {
		
		return null;
	}
	
	protected String nameMXML(String value1, String value2, String value3) {
		
		return null;
	}
	
	protected String nameDXML(String value1, String value2, String value3) {

		return null;
	}
}
