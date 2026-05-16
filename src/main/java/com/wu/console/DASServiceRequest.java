package com.wu.console;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

import com.wu.WUGWRuntime;
import com.wu.excel.impl.DASExcelDelegator;
import com.wu.excel.impl.ExcelFileWriter;
import com.wu.gw.model.WUTransaction;
import com.wu.gw.model.das.AbstractDASRecord;
import com.wu.gw.model.das.DASQueryResult;
import com.wu.gw.service.DASQueryService;
import com.wu.gw.util.UtilFunctions;
import com.wu.xmlhandler.GWMessageAssembleUtils;
import com.wu.xmlhandler.XMLAssemblerHandler;

public class DASServiceRequest extends AbstractServiceRequest {

	private Map<String, File> dasAISMap;
	private Map<String, File> dasBISMap;
	
	private XMLAssemblerHandler xmlHandlerAIS;
	private XMLAssemblerHandler xmlHandlerBIS;

	private WUTransaction txnData = null;
	private DASQueryService dasService = null;
	private String dasName = null;
	private String countryCode = null;
	private String naid = null;
	private String env = null;
	private String serviceURL = null;

	public DASServiceRequest() {
		super();
	}

	@Override
	public boolean serve(WUGWRuntime wugwRuntime) {
		// Scanner scan = new Scanner(System.in);
		System.out.print("\nEnvironment (PI / PROD): ");
		this.env = UtilFunctions.scan.nextLine().toUpperCase();
		System.out.print("\nCountry code: ");
		this.countryCode = UtilFunctions.scan.nextLine();
		System.out.print("\nNAID of Partner: ");
		this.naid = UtilFunctions.scan.nextLine();
		// scan.close();

		return super.serve(wugwRuntime);
	}

	@Override
	protected boolean doService() {
		if(env.equalsIgnoreCase("PI")) {
			dasAISMap = wugwRuntime.getDasAISMapPI();
			dasBISMap = wugwRuntime.getDasBISMapPI();
			
		} else if(env.equalsIgnoreCase("PROD")) {
			dasAISMap = wugwRuntime.getDasAISMapProd();
			dasBISMap = wugwRuntime.getDasBISMapProd();
		} else {
			System.out.print("\nInvalid running environment : " + env);
			return true;
		}
		
		serviceURL = wugwRuntime.getProperty(env + ".domain");
		
		xmlHandlerAIS = wugwRuntime.getAISAssemblerHandler();
		xmlHandlerBIS = wugwRuntime.getBISAssemblerHandler();

		this.execute();
		return true;
	}

	private void execute() {
		String key = (this.dasName + "-" + this.countryCode + "-" + this.naid).toUpperCase();

		File dasFile = null;
		String dasXMLStr = "";
		XMLAssemblerHandler xmlHandler = null;
		if (dasAISMap.containsKey(key)) {
			dasFile = dasAISMap.get(key);
			try {
				dasXMLStr = UtilFunctions.readFileToString(dasFile);
				xmlHandler = xmlHandlerAIS;
			} catch (IOException ioe) {
				UtilFunctions.loggingException(ioe);
			}
		} else if (dasBISMap.containsKey(key)) {
			dasFile = dasBISMap.get(key);
			try {
				dasXMLStr = UtilFunctions.readFileToString(dasFile);
				xmlHandler = xmlHandlerAIS;
			} catch (IOException ioe) {
				UtilFunctions.loggingException(ioe);
			}
		} else {
			UtilFunctions.logger.info("DAS xml sample does not exist : " + key);
			return;
		}

		boolean dataMore = false;
		do {
			dasXMLStr = GWMessageAssembleUtils.assembleDASRequestMsg(xmlHandler, dasXMLStr, txnData);
			String dasReplyStr = UtilFunctions.requestGatewayService(serviceURL, dasXMLStr);
			dataMore = dasService.execute(dasReplyStr, txnData);
		} while (dataMore);

		// --------------------------------------------------------------
		try {
			DASQueryResult dasQueryData = txnData.getDasQueryData();
			ArrayList<AbstractDASRecord> dasRecords = dasQueryData.getRecordSet();
			if (dasRecords == null || dasRecords.size() == 0) {
				UtilFunctions.logger.info("No DAS data retrieved from " + this.dasName);
			} else {
				dasQueryData.setDataHeader(dasQueryData.getRecordSet().get(0).getColomnHeader());
			}

			DASExcelDelegator dasDelegator = new DASExcelDelegator(dasQueryData);

			String dateStr = UtilFunctions.getCurrentDate();
			String filePath = wugwRuntime.getProperty("DAS.output.folder") + "\\" + dasName + "_"
					+ countryCode + "_" + naid + "_" + env + "_" + dateStr + ".xls";

			ExcelFileWriter.writeToExcel(filePath, dasDelegator);
		} catch (IOException ioe) {
			UtilFunctions.loggingException(ioe);
		}
	}

	public WUTransaction getTxnData() {
		return txnData;
	}

	public void setTxnData(WUTransaction txnData) {
		this.txnData = txnData;
	}

	public DASQueryService getDasService() {
		return dasService;
	}

	public void setDasService(DASQueryService dasService) {
		this.dasService = dasService;
	}

	public String getDasName() {
		return dasName;
	}

	public void setDasName(String dasName) {
		this.dasName = dasName;
	}
}
