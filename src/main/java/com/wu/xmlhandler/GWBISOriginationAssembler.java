package com.wu.xmlhandler;

import java.util.List;

public class GWBISOriginationAssembler extends AbstractXMLAssembler  {

	public GWBISOriginationAssembler() {
		
		super();
	}
	
	@Override
	public String assemble(String xmlMsg, String key, List<String> valueList) {
		
		if(xmlMsg == null || xmlMsg.equals("") || key == null || key.equals("") || valueList == null || valueList.size() != 3) {
			
			return null;
		}
		
		int pos = key.indexOf("...");
		if(pos <= 0 || pos >= (key.length() - 3)) {
				
			return null;
		}
			
		String sKey = key.substring(0, pos);
		String eKey = key.substring(pos + 3);
			
		int sPosMsg = xmlMsg.indexOf(sKey);
		int ePosMsg = xmlMsg.indexOf(eKey, sPosMsg) + eKey.length();
		
		return xmlMsg.substring(0, sPosMsg) + sKey + "<principal_amount>" + valueList.get(0) + "</principal_amount><currency_iso_code>" + valueList.get(1) + "</currency_iso_code><country_iso_code>" + valueList.get(2) + eKey +xmlMsg.substring(ePosMsg);
	}
}
