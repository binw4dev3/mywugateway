package com.wu.console;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wu.WUGWRuntime;
import com.wu.excel.impl.ExcelFileReader;
import com.wu.excel.impl.ExcelFileUpdator;
import com.wu.excel.impl.FeeInquiryExcelDelegator;
import com.wu.gw.model.ExcelDataEntry;
import com.wu.gw.util.UtilFunctions;
import com.wu.xmlhandler.GWMessageAssembleUtils;
import com.wu.xmlhandler.XMLAssemblerHandler;

public class PricingTableRequest extends AbstractServiceRequest {

	private Map<String, File> fiBISMapProd;
	private Map<String, File> fiAISMapProd;
	
	private Map<String, File> fiBISMapPI;
	private Map<String, File> fiAISMapPI;

	private XMLAssemblerHandler xmlHandlerAIS;
	private XMLAssemblerHandler xmlHandlerBIS;
	
	private FeeInquiryExcelDelegator delegator = new FeeInquiryExcelDelegator();

	public PricingTableRequest() {
		super();
	}

	@Override
	public boolean doService() {
		fiBISMapProd = wugwRuntime.getPricingBISMapProd();
		fiAISMapProd = wugwRuntime.getPricingAISMapProd();
		
		fiBISMapPI = wugwRuntime.getPricingBISMapPI();
		fiAISMapPI = wugwRuntime.getPricingAISMapPI();
		
		xmlHandlerAIS = wugwRuntime.getAISAssemblerHandler();
		xmlHandlerBIS = wugwRuntime.getBISAssemblerHandler();
		
		String inputFolderPath = wugwRuntime.getProperty("Pricing.input.folder");
		String outputFolderPath = wugwRuntime.getProperty("Pricing.output.folder");

		HashMap<String, File> dataFileMap = UtilFunctions.scanFiles(new File(inputFolderPath));

		dataFileMap.forEach((fName, dataFile) -> {
			try {
				execute(dataFile, fName);
				String destFilePath = outputFolderPath + "/" + UtilFunctions.getCurrentDate() + " " + dataFile.getName();
				UtilFunctions.copyFile(dataFile.getPath(), destFilePath);
			} catch (IOException e) {
				UtilFunctions.loggingException(e);
			}
		});

		return true;
	}
	
	public boolean runServiceStandalone(WUGWRuntime wugwRuntime) {
		
		this.wugwRuntime = wugwRuntime;
		
		fiBISMapProd = wugwRuntime.getPricingBISMapProd();
		fiAISMapProd = wugwRuntime.getPricingAISMapProd();
		
		xmlHandlerAIS = wugwRuntime.getAISAssemblerHandler();
		xmlHandlerBIS = wugwRuntime.getBISAssemblerHandler();
		
		String inputFolderPath = wugwRuntime.getProperty("Pricing.input.folder");
		String outputFolderPath = "C:/Users/303172/OneDrive - Western Union/[PricingTable]";

		HashMap<String, File> dataFileMap = UtilFunctions.scanFiles(new File(inputFolderPath));

		dataFileMap.forEach((fName, dataFile) -> {
			try {
				execute(dataFile, fName);
				String destFilePath = outputFolderPath + "/" + UtilFunctions.getCurrentDate() + " " + dataFile.getName();
				//String destFilePath = outputFolderPath + "/" + dataFile.getName();
				UtilFunctions.moveFile(dataFile.getPath(), destFilePath);
			} catch (IOException e) {
				UtilFunctions.loggingException(e);
			}
		});

		return true;
	}

	private void execute(File dataFile, String fileName) {
		try {
			String[] keys = fileName.split("-");
			String env = keys[1].trim();
			String naid = keys[2].trim();
			String serviceURL = wugwRuntime.getProperty(env + ".domain");
			List<Object> dataSet = ExcelFileReader.readFromExcel(dataFile.getPath(), delegator);
			for (Object sObject : dataSet) {
				ExcelDataEntry sData = (ExcelDataEntry) sObject;
				if (sData != null) {
					if(env.equalsIgnoreCase("PI")) {
						this.inquireFeeCharge(sData, serviceURL, fiAISMapPI, fiBISMapPI, naid);
					} else if (env.equalsIgnoreCase("PROD")) {
						this.inquireFeeCharge(sData, serviceURL, fiAISMapProd, fiBISMapProd, naid);
					}
				}
			}
			
			ExcelFileUpdator.updateToExcel(dataFile.getPath(), delegator, dataSet);

		} catch (Exception e) {

			UtilFunctions.loggingException(e);
		}
	}

	private void inquireFeeCharge(ExcelDataEntry sData, String serviceURL, Map<String, File> fiAISMap,
			Map<String, File> fiBISMap, String naid) throws IOException {
		
		String sCountryCode = sData.getSendCountryCurrency().getIsoCode().getCountryCode();
		String sCurrencyCode = sData.getSendCountryCurrency().getIsoCode().getCurrencyCode();
		//String naid = sData.getNaid();
		String key = sCountryCode + "-" + naid;
		// 000 or 500 or 800
		String service = sData.getServiceType();

		if (sData.getApiType().equalsIgnoreCase("AIS") && fiAISMap.containsKey(key)) {
			File fiFile = fiAISMap.get(key);
			String fiXMLStr = UtilFunctions.readFileToString(fiFile);

			fiXMLStr = GWMessageAssembleUtils.assembleAISFIRequestMsgForPricing(xmlHandlerAIS, fiXMLStr, sData);
			String fiReplyStr = UtilFunctions.requestGatewayService(serviceURL, fiXMLStr);

			if (!fiReplyStr.startsWith("error - ")) {
				String fee = UtilFunctions.retrieveXMLValue(fiReplyStr, "charges");
				sData.setFee(UtilFunctions.convertToNatureAmount(fee) + " " + sCurrencyCode);

				String fxRate = UtilFunctions.retrieveXMLValue(fiReplyStr, "exchange_rate");
				sData.setFxRate(fxRate);

			} else {
				UtilFunctions.logger.info("AIS FI - " + fiReplyStr);
				sData.setFee("AIS FI " + fiReplyStr);
			}
		} else if (sData.getApiType().equalsIgnoreCase("BIS") && fiBISMap.containsKey(key)) {
			File fiFile = fiBISMap.get(key);
			String fiXMLStr = UtilFunctions.readFileToString(fiFile);

			fiXMLStr = GWMessageAssembleUtils.assembleBISFIRequestMsgForPricing(xmlHandlerBIS, fiXMLStr, sData);
			String fiReplyStr = UtilFunctions.requestGatewayService(serviceURL, fiXMLStr);

			if (!fiReplyStr.startsWith("error - ")) {
				/*String fee = UtilFunctions.retrieveXMLValue(fiReplyStr, "charges");
				sData.setFee(UtilFunctions.convertToNatureAmount(fee) + " " + sCurrencyCode);

				String fxRate = UtilFunctions.retrieveXMLValue(fiReplyStr, "exchange_rate");
				sData.setFxRate(fxRate);*/
				
				List<String> serviceOptionList = UtilFunctions.retrieveAllSubElements(fiReplyStr, "service_option");
				
				serviceOptionList.forEach((String serviceOption) -> {
					if(serviceOption != null && !serviceOption.equals("")) {
						String serviceType = UtilFunctions.retrieveXMLValue(serviceOption, "code");
						if(serviceType.equals(service)) {
							String fee = UtilFunctions.retrieveXMLValue(serviceOption, "charges");
							sData.setFee(UtilFunctions.convertToNatureAmount(fee) + " " + sCurrencyCode);

							String fxRate = UtilFunctions.retrieveXMLValue(serviceOption, "exchange_rate");
							sData.setFxRate(fxRate);
						}
					}
				});
			} else {
				UtilFunctions.logger.info("BIS FI - " + fiReplyStr);
				sData.setFee("BIS FI " + fiReplyStr);
			}
		} else {
			sData.setFee("The sending country/currency is not available : " + key);
		}
	}
}
