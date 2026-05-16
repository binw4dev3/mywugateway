package com.wu.xmlhandler;

public class GWSenderNameAssembler extends GWNameAssembler {

	@Override
	protected String nameCXML(String value1) {
		
		return "<sender><name name_type=\"C\"><business_name>" + value1 + "</business_name></name>";
	}
	
	@Override
	protected String nameMXML(String value1, String value2, String value3) {
		
		return "<sender><name name_type=\"M\"><given_name>" + value1 + "</given_name><paternal_name>" + value2 + "</paternal_name><maternal_name>" + value3 + "</maternal_name></name>";
	}
	
	@Override
	protected String nameDXML(String value1, String value2, String value3) {

		if(value2 == null || value2.equals("") || value2.equals("null") || value2.equals("NULL")) {
			
			return "<sender><name name_type=\"D\"><first_name>" + value1 + "</first_name><last_name>" + value3 + "</last_name></name>";
		}
		
		return "<sender><name name_type=\"D\"><first_name>" + value1 + "</first_name><middle_name>" + value2 + "</middle_name><last_name>" + value3 + "</last_name></name>";
	}
}
