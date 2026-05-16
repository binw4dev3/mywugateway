package com.wu.gw.service;

import com.wu.gw.model.WUTransaction;
import com.wu.gw.model.das.DASQueryResult;
import com.wu.gw.model.das.MexicoCityStateRecord;
import com.wu.gw.util.UtilFunctions;

public class GetMexicoCityState implements DASQueryService {

	public GetMexicoCityState() {
	}
	
	@Override
	public boolean execute(String dasXMLStr, WUTransaction txnData) {
		DASQueryResult dasQueryData = txnData.getDasQueryData();
		if (dasQueryData == null) {
			dasQueryData = new DASQueryResult();
			dasQueryData.setDasName(txnData.getDasName());
			txnData.setDasQueryData(dasQueryData);
		}
		
		boolean dataMore = false;
		if (!dasXMLStr.startsWith("error - ")) {
			String dataMoreStr = UtilFunctions.retrieveXMLValue(dasXMLStr, "DATA_MORE");
			dataMore = dataMoreStr.equals("Y");
			String recordSet = UtilFunctions.retrieveXMLSubElement(dasXMLStr, "RECORDSET");

			String[] records = recordSet.split("(?<=</" + txnData.getDasName().toUpperCase() + ">)");
			for (int i = 0, n = records.length; i < n; i++) {
				String record = records[i];
				MexicoCityStateRecord mxInfo = new MexicoCityStateRecord();
				mxInfo.addColumnData(MexicoCityStateRecord.STATE_CODE, UtilFunctions.retrieveXMLValue(record, MexicoCityStateRecord.STATE_CODE_FIELD));
				mxInfo.addColumnData(MexicoCityStateRecord.STATE_NAME, UtilFunctions.retrieveXMLValue(record, MexicoCityStateRecord.STATE_NAME_FIELD));
				mxInfo.addColumnData(MexicoCityStateRecord.CITY, UtilFunctions.retrieveXMLValue(record, MexicoCityStateRecord.CITY_FIELD));

				dasQueryData.getRecordSet().add(mxInfo);

				UtilFunctions.logger.info(mxInfo.toString());
				
				if (dataMore && i == (n - 1)) {
					txnData.getDasFilters().put("queryfilter2", mxInfo.getColumnData().get(MexicoCityStateRecord.STATE_NAME));
					txnData.getDasFilters().put("queryfilter3", mxInfo.getColumnData().get(MexicoCityStateRecord.CITY));
				}
			}
		} else {
			UtilFunctions.logger.info("AIS DAS reply - " + dasXMLStr);
		}

		return dataMore;
	}

}
