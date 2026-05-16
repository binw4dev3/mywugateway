package com.wu.gw.service;

import com.wu.gw.model.WUTransaction;
import com.wu.gw.model.das.DASQueryResult;
import com.wu.gw.model.das.USStateInfoRecord;
import com.wu.gw.util.UtilFunctions;

public class GetUSStateList implements DASQueryService {

	public GetUSStateList() {
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
				USStateInfoRecord usInfo = new USStateInfoRecord();
				usInfo.addColumnData(USStateInfoRecord.STATE_CODE,
						UtilFunctions.retrieveXMLValue(record, USStateInfoRecord.STATE_CODE_FIELD));
				usInfo.addColumnData(USStateInfoRecord.STATE_NAME,
						UtilFunctions.retrieveXMLValue(record, USStateInfoRecord.STATE_NAME_FIELD));

				dasQueryData.getRecordSet().add(usInfo);

				UtilFunctions.logger.info(usInfo.toString());
				
				if (dataMore && i == (n - 1)) {
					txnData.getDasFilters().put("queryfilter2", usInfo.getColumnData().get(USStateInfoRecord.STATE_NAME));
				}
			}
		} else {
			UtilFunctions.logger.info("AIS DAS reply - " + dasXMLStr);
		}

		return dataMore;
	}
}
