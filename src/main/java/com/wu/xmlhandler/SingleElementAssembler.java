package com.wu.xmlhandler;

import java.util.List;

public class SingleElementAssembler extends AbstractXMLAssembler {

	public SingleElementAssembler() {
		
		super();
	}
	
	@Override
	public String assemble(String xmlMsg, String key, List<String> valueList) {
		
		if(xmlMsg == null || xmlMsg.equals("") || key == null || key.equals("") || valueList == null || valueList.size() == 0) {
			
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
			
		return xmlMsg.substring(0, sPosMsg) + sKey + valueList.get(0) + eKey + xmlMsg.substring(ePosMsg);
	}
}
