package com.wu.gw.service;

import com.wu.gw.model.WUTransaction;
import com.wu.gw.model.das.DASQueryResult;
import com.wu.gw.model.das.DeliveryServicesRecord;
import com.wu.gw.util.UtilFunctions;

public class GetDeliveryServices implements DASQueryService {

	public GetDeliveryServices() {
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
			//int recNum = Integer.valueOf(UtilFunctions.retrieveXMLValue(dasXMLStr, "DATA_NUM_RECS"));
			String recordSet = UtilFunctions.retrieveXMLSubElement(dasXMLStr, "RECORDSET");

			if(recordSet == null || recordSet.equals("")) return false;
			
			String[] records = recordSet.split("(?<=</" + txnData.getDasName().toUpperCase() + ">)");
			for (int i = 0, n = records.length; i < n; i++) {
				String record = records[i];
				DeliveryServicesRecord svc = new DeliveryServicesRecord();
				svc.addColumnData(DeliveryServicesRecord.DESTINATION_CODE,
						UtilFunctions.retrieveXMLValue(record, DeliveryServicesRecord.DESTINATION_FIELD));
				svc.addColumnData(DeliveryServicesRecord.SVC_CODE, UtilFunctions.retrieveXMLValue(record, DeliveryServicesRecord.SVC_CODE_FIELD));
				svc.addColumnData(DeliveryServicesRecord.SVC_NAME, UtilFunctions.retrieveXMLValue(record, DeliveryServicesRecord.SVC_NAME_FIELD));
				svc.addColumnData(DeliveryServicesRecord.ROUTE, UtilFunctions.retrieveXMLValue(record, DeliveryServicesRecord.ROUTE_FIELD));
				svc.addColumnData(DeliveryServicesRecord.BANNER, UtilFunctions.retrieveXMLValue(record, DeliveryServicesRecord.BANNER_FIELD));
				svc.addColumnData(DeliveryServicesRecord.TEMPLT, UtilFunctions.retrieveXMLValue(record, DeliveryServicesRecord.TEMPLT_FIELD));
				svc.addColumnData(DeliveryServicesRecord.DESCRIPTION, UtilFunctions.retrieveXMLValue(record, DeliveryServicesRecord.DESCRIPTION_FIELD));

				dasQueryData.getRecordSet().add(svc);

				//UtilFunctions.d2bLogger.info(svc.toString());
				
				/*
				 * if (dataMore && i == (n - 1)) { txnData.getDasFilters().put("queryfilter2",
				 * svc.getColumnData().get(USStateInfo.STATE_NAME)); }
				 */
			}
		} else {
			UtilFunctions.logger.info("AIS DAS reply - " + dasXMLStr);
		}

		return dataMore;
	}

}