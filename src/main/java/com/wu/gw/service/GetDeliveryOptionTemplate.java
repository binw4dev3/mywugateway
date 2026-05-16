package com.wu.gw.service;

import com.wu.gw.model.WUTransaction;
import com.wu.gw.model.das.DASQueryResult;
import com.wu.gw.model.das.DeliveryOptionTemplateRecord;
import com.wu.gw.util.UtilFunctions;

public class GetDeliveryOptionTemplate implements DASQueryService {

	public GetDeliveryOptionTemplate() {
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
			// int recNum = Integer.valueOf(UtilFunctions.retrieveXMLValue(dasXMLStr,
			// "DATA_NUM_RECS"));
			String recordSet = UtilFunctions.retrieveXMLSubElement(dasXMLStr, "RECORDSET");

			if(recordSet == null || recordSet.equals("")) return false;
			
			String[] records = recordSet.split("(?<=</" + txnData.getDasName().toUpperCase() + ">)");
			for (int i = 0, n = records.length; i < n; i++) {
				String record = records[i];
				DeliveryOptionTemplateRecord dot = new DeliveryOptionTemplateRecord();
				dot.addColumnData(DeliveryOptionTemplateRecord.CATEGORY,
						UtilFunctions.retrieveXMLValue(record, DeliveryOptionTemplateRecord.CATEGORY_field));
				dot.addColumnData(DeliveryOptionTemplateRecord.DESCRIPTION,
						UtilFunctions.retrieveXMLValue(record, DeliveryOptionTemplateRecord.DESCRIPTION_field));
				dot.addColumnData(DeliveryOptionTemplateRecord.T_INDEX,
						UtilFunctions.retrieveXMLValue(record, DeliveryOptionTemplateRecord.T_INDEX_field));
				dot.addColumnData(DeliveryOptionTemplateRecord.PRODUCT,
						UtilFunctions.retrieveXMLValue(record, DeliveryOptionTemplateRecord.PRODUCT_field));

				dasQueryData.getRecordSet().add(dot);

				//UtilFunctions.d2bLogger.info(dot.toString());

				if (dataMore && i == (n - 1)) {
					txnData.getDasFilters().put("queryfilter4", dot.getColumnData().get(DeliveryOptionTemplateRecord.T_INDEX));
				}
			}
		} else {
			UtilFunctions.logger.info("AIS DAS reply - " + dasXMLStr);
		}

		return dataMore;
	}
}
