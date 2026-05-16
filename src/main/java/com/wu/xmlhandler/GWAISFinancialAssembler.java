package com.wu.xmlhandler;

import java.util.List;

public class GWAISFinancialAssembler extends AbstractXMLAssembler {

	protected static String TXNTYPE_WMN = "WMN";
	protected static String TXNTYPE_WMF = "WMF";
	
	public GWAISFinancialAssembler() {
		
		super();
	}
	
	@Override
	protected String generateXML(List<String> valueList) {
		
		if(valueList.size() < 2) {
			
			System.out.println("Invalid financials value parameters");
			
			return null;
		}
		
		String txnType = valueList.get(0);
		
		if(txnType.equals(TXNTYPE_WMN)) {
			
			return wmnXML(valueList.get(1));
			
		} else if(txnType.equals(TXNTYPE_WMF)) {
			
			return wmfXML(valueList.get(1));
		}
		
		return null;
	}
	
	protected String wmnXML(String amount) {
		
		return "<financials><originators_principal_amount>" + amount + "</originators_principal_amount></financials>";
	}
	
	protected String wmfXML(String amount) {
		
		return "<financials><destination_principal_amount>" + amount + "</destination_principal_amount></financials>";
	}
}
