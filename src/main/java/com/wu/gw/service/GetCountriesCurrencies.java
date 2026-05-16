package com.wu.gw.service;

import com.wu.gw.model.WUTransaction;
import com.wu.gw.model.das.CountriesCurrenciesRecord;
import com.wu.gw.model.das.DASQueryResult;
import com.wu.gw.util.UtilFunctions;


public class GetCountriesCurrencies implements DASQueryService {

	public GetCountriesCurrencies() {
	}

	@Override
	public boolean execute(String dasXMLStr, WUTransaction txnData) {
		DASQueryResult dasQueryData = txnData.getDasQueryData();
		if (dasQueryData == null) {
			dasQueryData = new DASQueryResult();
			dasQueryData.setDasName(txnData.getDasName());
			txnData.setDasQueryData(dasQueryData);
		}

		Boolean dataMore = false;
		if (!dasXMLStr.startsWith("error - ")) {
			String dataMoreStr = UtilFunctions.retrieveXMLValue(dasXMLStr, "DATA_MORE");
			dataMore = dataMoreStr.equals("Y");
			String recordSet = UtilFunctions.retrieveXMLSubElement(dasXMLStr, "RECORDSET");

			if(recordSet == null || recordSet.equals("")) return false;
			
			String[] records = recordSet.split("(?<=</" + txnData.getDasName().toUpperCase() + ">)");
			for (int i = 0, n = records.length; i < n; i++) {
				String record = records[i];

				CountriesCurrenciesRecord cc = new CountriesCurrenciesRecord();
				cc.addColumnData(CountriesCurrenciesRecord.COUNTRY_CODE,
						UtilFunctions.retrieveXMLValue(record, CountriesCurrenciesRecord.COUNTRY_CODE_FIELD));
				cc.addColumnData(CountriesCurrenciesRecord.COUNTRY_ID,
						UtilFunctions.retrieveXMLValue(record, CountriesCurrenciesRecord.COUNTRY_ID_FIELD));
				cc.addColumnData(CountriesCurrenciesRecord.COUNTRY_NAME,
						UtilFunctions.retrieveXMLValue(record, CountriesCurrenciesRecord.COUNTRY_NAME_FIELD));
				cc.addColumnData(CountriesCurrenciesRecord.CURRENCY_CODE,
						UtilFunctions.retrieveXMLValue(record, CountriesCurrenciesRecord.CURRENCY_CODE_FIELD));
				cc.addColumnData(CountriesCurrenciesRecord.CURRENCY_ID,
						UtilFunctions.retrieveXMLValue(record, CountriesCurrenciesRecord.CURRENCY_ID_FIELD));
				cc.addColumnData(CountriesCurrenciesRecord.CURRENCY_NAME,
						UtilFunctions.retrieveXMLValue(record, CountriesCurrenciesRecord.CURRENCY_NAME_FIELD));

				dasQueryData.getRecordSet().add(cc);

				//UtilFunctions.logger.info(cc.toString());

				if (dataMore && i == (n - 1)) {
					txnData.getDasFilters().put("queryfilter3", cc.getColumnData().get(CountriesCurrenciesRecord.COUNTRY_NAME));
					txnData.getDasFilters().put("queryfilter4", cc.getColumnData().get(CountriesCurrenciesRecord.CURRENCY_NAME));
				}
			}
		} else {
			UtilFunctions.logger.info("AIS DAS reply - " + dasXMLStr);
		}
		
		return dataMore;
	}
}