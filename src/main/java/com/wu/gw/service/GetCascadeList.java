package com.wu.gw.service;

import com.wu.gw.model.WUTransaction;
import com.wu.gw.model.das.CascadeListRecord;
import com.wu.gw.model.das.DASQueryResult;
import com.wu.gw.util.UtilFunctions;

public class GetCascadeList implements DASQueryService {

	public GetCascadeList() {
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

			if(recordSet == null || recordSet.equals("")) return false;
			
			String[] records = recordSet.split("(?<=</" + txnData.getDasName().toUpperCase() + ">)");
			for (int i = 0, n = records.length; i < n; i++) {
				String record = records[i];
				CascadeListRecord cl = new CascadeListRecord();
				cl.addColumnData(CascadeListRecord.LIST_VALUE,
						UtilFunctions.retrieveXMLValue(record, CascadeListRecord.LIST_VALUE_FIELD));
				cl.addColumnData(CascadeListRecord.LIST_TEXT,
						UtilFunctions.retrieveXMLValue(record, CascadeListRecord.LIST_TEXT_FIELD));

				dasQueryData.getRecordSet().add(cl);

				//UtilFunctions.logger.info(cl.toString());

				if (dataMore && i == (n - 1)) {
					txnData.getDasFilters().put("queryfilter6", cl.getColumnData().get(CascadeListRecord.LIST_TEXT));
				}
			}
		} else {
			UtilFunctions.logger.info("AIS DAS reply - " + dasXMLStr);
		}

		return dataMore;
	}
}
