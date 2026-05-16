package com.wu.console;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.wu.config.ApplicationContextProvider;
import com.wu.config.WUGWConfig;
import com.wu.web.model.Bank;
import com.wu.web.model.Branch;
import com.wu.web.service.BankService;
import com.wu.web.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;

import com.wu.WUGWRuntime;
import com.wu.excel.ExcelException;
import com.wu.excel.impl.APNTemplateExcelDelegator;
import com.wu.excel.impl.ExcelFileWriter;
import com.wu.gw.model.APNTemplate;
import com.wu.gw.model.APNTemplateDataEntry;
import com.wu.gw.model.WUTransaction;
import com.wu.gw.model.das.AbstractDASRecord;
import com.wu.gw.model.das.CascadeListRecord;
import com.wu.gw.model.das.CountriesCurrenciesRecord;
import com.wu.gw.model.das.DASQueryResult;
import com.wu.gw.model.das.DeliveryOptionTemplateRecord;
import com.wu.gw.model.das.DeliveryServicesRecord;
import com.wu.gw.service.GetCascadeList;
import com.wu.gw.service.GetCountriesCurrencies;
import com.wu.gw.service.GetDeliveryOptionTemplate;
import com.wu.gw.service.GetDeliveryServices;
import com.wu.gw.util.UtilFunctions;
import com.wu.xmlhandler.GWMessageAssembleUtils;
import com.wu.xmlhandler.XMLAssemblerHandler;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
//@ConfigurationProperties(prefix = "APNDynamicRequest")
//@PropertySource(value = "properties/ServiceConsolerBanner.properties")
@Scope("prototype")
public class APNDynamicRequest extends AbstractServiceRequest {

	//@Autowired
	BankService bankService;
	//@Autowired
	BranchService branchService;

	private Map<String, File> dasAISMap;
	private Map<String, File> dasBISMap;

	private XMLAssemblerHandler xmlHandlerAIS;
	private XMLAssemblerHandler xmlHandlerBIS;

	private String env;
	private String serviceURL;

	private String countryCode = null;
	private String productCode = null;
	private String naid = null;
	private ArrayList<APNTemplate> templateList = null;
	
	private String prefix = "----";

/*	public APNDynamicRequest() {
		super();
	}*/

	public APNDynamicRequest(BankService bankService, BranchService branchService) {
		super();
		this.bankService = bankService;
		this.branchService = branchService;

		this.setRequestID(WUGWConfig.getProps().getProperty("APNDynamicRequest.serialNum"));
		this.setDescription(WUGWConfig.getProps().getProperty("APNDynamicRequest.description"));

		String requestID1 = this.getRequestID();
		System.out.println("requestID1 = " + requestID1);
	}

	@Override
	public boolean serve(WUGWRuntime wugwRuntime) {
		// Scanner scan = new Scanner(System.in);
		// UtilFunctions.logger.info("\nCountry code: ");
		System.out.print("\nEnvironment (PI / PROD): ");
		this.env = UtilFunctions.scan.nextLine().toUpperCase();
		System.out.print("\nCountry code: ");
		this.countryCode = UtilFunctions.scan.nextLine();
		// UtilFunctions.logger.info("\nNAID of Partner: ");
		System.out.print("\nNAID of Partner: ");
		this.naid = UtilFunctions.scan.nextLine();
		// UtilFunctions.logger.info("\nProduct code [D2C/D2B/SVC]: ");
		System.out.print("\nProduct code [D2C/D2B/SVC]: ");
		this.productCode = UtilFunctions.scan.nextLine();
		// scan.close();

		if (templateList != null) {
			templateList.clear();
		} else {
			templateList = new ArrayList<APNTemplate>();
		}

		return super.serve(wugwRuntime);
	}

	@Override
	protected boolean doService() {
		if(env.equalsIgnoreCase("PI")) {
			dasAISMap = wugwRuntime.getDasAISMapPI();
			dasBISMap = wugwRuntime.getDasBISMapPI();
			
		} else if(env.equalsIgnoreCase("PROD")) {
			Map<String, File> map = wugwRuntime.getDasAISMapProd();
			dasAISMap = wugwRuntime.getDasAISMapProd();
			dasBISMap = wugwRuntime.getDasBISMapProd();
		} else {
			System.out.print("\nInvalid running environment : " + env);
			return true;
		}

		xmlHandlerAIS = wugwRuntime.getAISAssemblerHandler();
		xmlHandlerBIS = wugwRuntime.getBISAssemblerHandler();

		serviceURL = wugwRuntime.getProperty(env + ".domain");

		this.execute();
		return true;
	}

	private String getDASXMLStr(String key, WUTransaction txnData) {
		File dasFile = null;
		String dasXMLStr = "";
		if (dasAISMap.containsKey(key)) {
			dasFile = dasAISMap.get(key);
			try {
				dasXMLStr = UtilFunctions.readFileToString(dasFile);
				dasXMLStr = GWMessageAssembleUtils.assembleDASRequestMsg(xmlHandlerAIS, dasXMLStr, txnData);
			} catch (IOException ioe) {
				UtilFunctions.loggingException(ioe);
			}
		} else if (dasBISMap.containsKey(key)) {
			dasFile = dasBISMap.get(key);
			try {
				dasXMLStr = UtilFunctions.readFileToString(dasFile);
				dasXMLStr = GWMessageAssembleUtils.assembleDASRequestMsg(xmlHandlerBIS, dasXMLStr, txnData);
			} catch (IOException ioe) {
				UtilFunctions.loggingException(ioe);
			}
		} else {
			UtilFunctions.logger.info("DAS xml sample does not exist : " + key);
		}

		return dasXMLStr;
	}

	private void execute() {
		// Get Country Currency List ---------------------------------
		WUTransaction ccData = ApplicationContextProvider.getBean("countriesCurrencies", WUTransaction.class);

		GetCountriesCurrencies gccService = ApplicationContextProvider.getBean("getCountriesCurrencies", GetCountriesCurrencies.class);
		String key = ("GetCountriesCurrencies" + "-" + this.countryCode + "-" + this.naid).toUpperCase();
		String dasXMLStr = this.getDASXMLStr(key, ccData);

		if (dasXMLStr.equals(""))
			return;

		boolean dataMore = false;
		do {
			String dasReplyStr = UtilFunctions.requestGatewayService(serviceURL, dasXMLStr);
			dataMore = gccService.execute(dasReplyStr, ccData);
			dasXMLStr = GWMessageAssembleUtils.assembleDASRequestMsg(xmlHandlerAIS, dasXMLStr, ccData);
		} while (dataMore);

		// Get Delivery Service List --------------------------------

		DASQueryResult dasQueryDataCC = ccData.getDasQueryData();
		for (AbstractDASRecord record : dasQueryDataCC.getRecordSet()) {
			String ctrCode = record.getColumnData().get(CountriesCurrenciesRecord.COUNTRY_CODE);
			String curCode = record.getColumnData().get(CountriesCurrenciesRecord.CURRENCY_CODE);
			
			System.out.println(ctrCode + " " + curCode);

			// Only for Bangladesh testing purpose (BD BDT)
			//if(!ctrCode.equalsIgnoreCase("BD")) continue;
			
			String destCtrCur = ctrCode + " " + curCode;

			WUTransaction svcData = ApplicationContextProvider.getBean("deliveryServices", WUTransaction.class);
			svcData.getDasFilters().put("queryfilter3", destCtrCur);
			svcData.getDasFilters().put("queryfilter4", productCode);

			GetDeliveryServices gdService = ApplicationContextProvider.getBean("getDeliveryServices", GetDeliveryServices.class);
			key = ("GetDeliveryServices" + "-" + this.countryCode + "-" + this.naid).toUpperCase();
			dasXMLStr = this.getDASXMLStr(key, svcData);

			if (dasXMLStr.equals(""))
				return;

			dataMore = false;
			do {
				String dasReplyStr = UtilFunctions.requestGatewayService(serviceURL, dasXMLStr);
				dataMore = gdService.execute(dasReplyStr, svcData);
				dasXMLStr = GWMessageAssembleUtils.assembleDASRequestMsg(xmlHandlerAIS, dasXMLStr, svcData);
			} while (dataMore);

			// Get Service Option Template List ---------------------

			DASQueryResult dasQueryDataSVC = svcData.getDasQueryData();
			for (AbstractDASRecord recordSVC : dasQueryDataSVC.getRecordSet()) {

				APNTemplate apnTemplate = new APNTemplate();
				apnTemplate.setCorridor(destCtrCur);

				UtilFunctions.d2bLogger.info(destCtrCur);
				UtilFunctions.d2bLogger.info("\t->\t" + recordSVC.toString());

				String templt = recordSVC.getColumnData().get(DeliveryServicesRecord.TEMPLT);
				String route = recordSVC.getColumnData().get(DeliveryServicesRecord.ROUTE);
				String svcdesp = recordSVC.getColumnData().get(DeliveryServicesRecord.DESCRIPTION);

				apnTemplate.setTemplateID(templt);
				apnTemplate.setServiceDesp(svcdesp);
				apnTemplate.setRoute(route);
				apnTemplate.setServiceCode(recordSVC.getColumnData().get(DeliveryServicesRecord.SVC_CODE));

				WUTransaction dotData = ApplicationContextProvider.getBean("deliveryOptionTemplate", WUTransaction.class);
				dotData.getDasFilters().put("queryfilter2", destCtrCur);
				dotData.getDasFilters().put("queryfilter3", templt);

				GetDeliveryOptionTemplate dotService = ApplicationContextProvider.getBean("getDeliveryOptionTemplate",
						GetDeliveryOptionTemplate.class);
				key = ("GetDeliveryOptionTemplate" + "-" + this.countryCode + "-" + this.naid).toUpperCase();
				dasXMLStr = this.getDASXMLStr(key, dotData);

				if (dasXMLStr.equals(""))
					return;

				dataMore = false;
				do {
					String dasReplyStr = UtilFunctions.requestGatewayService(serviceURL, dasXMLStr);
					dataMore = dotService.execute(dasReplyStr, dotData);
					dasXMLStr = GWMessageAssembleUtils.assembleDASRequestMsg(xmlHandlerAIS, dasXMLStr, dotData);
				} while (dataMore);

				DASQueryResult dasQueryDataDOT = dotData.getDasQueryData();

				/**
				 * Retrieve all the required data fields info (feature, dropdown list) from the result of DAS GetDeliveryOptionTemplate
				 */
				int comboNum = 0;
				ArrayList<String> combo1List = new ArrayList<String>();
				HashMap<String, String> combo1 = new HashMap<String, String>();
				HashMap<String, HashMap<String, AbstractDASRecord>> comboStore = new HashMap<String, HashMap<String, AbstractDASRecord>>();
				APNTemplateDataEntry apnField = null;
				HashMap<String, AbstractDASRecord> combo2 = null;
				for (AbstractDASRecord recordDOT : dasQueryDataDOT.getRecordSet()) {
					String tIndex = recordDOT.getColumnData().get(DeliveryOptionTemplateRecord.T_INDEX);
					String desp = recordDOT.getColumnData().get(DeliveryOptionTemplateRecord.DESCRIPTION);
					desp.replaceAll("&amp;", "&");
					String[] tLineData = desp.split(";");

					/**
					 * Retrieve the data options for a detected Type-I dropdown list.
					 * Normally the data options will be listed right after the appearance of the Type-I dropdown list field
					 * 
					 * The data options will be stored in the object of HashMap<String, String> combo1
					 * 
					 * e.g.
					 * 
					 *	<GETDELIVERYOPTIONTEMPLATE>
	                        <PRODUCT>UNIVERSAL ADDRESS DLV          H2H</PRODUCT>
	                        <CATEGORY>BANEPT</CATEGORY>
	                        <T_INDEX>005</T_INDEX>
	                        <DESCRIPTION>Receiver's Mobile Country Code; 20; 1478; ; 0; COMBO; NL; ; ; ; 1;</DESCRIPTION>
	                     </GETDELIVERYOPTIONTEMPLATE>
	                     <GETDELIVERYOPTIONTEMPLATE>
	                        <PRODUCT>UNIVERSAL ADDRESS DLV          H2H</PRODUCT>
	                        <CATEGORY>BANEPT</CATEGORY>
	                        <T_INDEX>006</T_INDEX>
	                        <DESCRIPTION>1; 0; 880; Bangladesh (+880);</DESCRIPTION>
	                     </GETDELIVERYOPTIONTEMPLATE>
					 * 
					 */
					if (comboNum > 0) {
						combo1List.add(desp);
						combo1.put(tLineData[2], tLineData[3]);
						comboNum--;
						if (comboNum == 0) {
							combo1List.stream().forEach((cValue) -> {
								UtilFunctions.d2bLogger.info(prefix + cValue);
							});

							for (Entry<String, String> entry : combo1.entrySet()) {
								apnField.addDropdownOption(prefix + entry.getKey().trim() + " - " + entry.getValue().trim());
							}

							combo1List.clear();
							combo1.clear();
						}

						continue;
					}

					UtilFunctions.d2bLogger.info("\t" + recordDOT.toString());

					/**
					 * index "000" is used to define the meta info of the template
					 * 
					 * e.g.
					 * 
					 *	<GETDELIVERYOPTIONTEMPLATE>
	                        <PRODUCT>DLVSVCS_UNIVERSAL              H2H</PRODUCT>
	                        <CATEGORY>4020</CATEGORY>
	                        <T_INDEX>000</T_INDEX>
	                        <DESCRIPTION>0; ; 0; ; ;EPT ; 0;</DESCRIPTION>
	                     </GETDELIVERYOPTIONTEMPLATE>
	                     <GETDELIVERYOPTIONTEMPLATE>
	                        <PRODUCT>UNIVERSAL ADDRESS DLV          H2H</PRODUCT>
	                        <CATEGORY>BANEPT</CATEGORY>
	                        <T_INDEX>000</T_INDEX>
	                        <DESCRIPTION>7; EARTHPORT BD/BDT D2B; ; ; 7; ; 7; 12:00:00Z,1;</DESCRIPTION>
	                     </GETDELIVERYOPTIONTEMPLATE>

					 */
					if (!tIndex.equals("000") && tLineData.length >= 6) {

						// Return APN field object if exists, otherwise create a new object
						apnField = apnTemplate.addDataField(tLineData[0].trim());
						apnField.setMaxLength(tLineData[1].trim());
						apnField.setFieldCHIPID(tLineData[2].trim());
						
						/*
						 * Required or Optional
						 */
						String fRequired = tLineData[3].trim();
						if (fRequired.contains("required")) {
							apnField.setRequired("Required");
						} else {
							apnField.setRequired("Optional");
						}
						
						/*
						 * Type of data field
						 */
						String fType = tLineData[5].trim();
						// Type-I dropdown list field
						if (fType.equalsIgnoreCase("COMBO")) {
							apnField.setFieldType("DropDown List");
						} 
						// Type-II dropdown list field
						else if (fType.equalsIgnoreCase("COMBO2")) {
							apnField.setFieldType("DropDown Cascade List");
						}
						// free text field
						else {
							apnField.setFieldType(fType);
							apnField.addDropdownOption("");
						}

						/*
						 * UtilFunctions.d2bLogger.info("\t" + apnField.getFieldName() + "\t" +
						 * apnField.getFieldType() + " - " + apnField.getMaxLength() + "\t" +
						 * apnField.getRequired());
						 */

						/**
						 * Retrieve the data options of the dropdown list : 
						 * 1. for Type-I list, the data options will be retrieved from the new records in the for-loop
						 * 2. for Type-II list, the data options will be retrieved by calling GetCascadeList API
						 */
						if (fType.equals("COMBO") && tLineData.length == 11 && tLineData[6].trim().equals("NL")) {
							comboNum = Integer.parseInt(tLineData[10].trim());
							apnField.setNumOfDropdownOptions(comboNum);
						} else if (fType.equals("COMBO2") && tLineData.length == 11
								&& tLineData[6].trim().equals("NL")) {
							String comboType = tLineData[10].trim();
							String[] comboTypeArray = comboType.split(",");
							String comboName = comboTypeArray[0].trim();
							String comboLevelStr = comboTypeArray[1].trim();
							int comboLevel = Integer.parseInt(comboLevelStr);
							
							apnField.setCascadeListLevel(String.valueOf(comboLevelStr));
							apnField.setIsCascadeList(true);
							apnField.setCascadeListName(comboName);

							WUTransaction clData = ApplicationContextProvider.getBean("cascadeList", WUTransaction.class);
							clData.getDasFilters().put("queryfilter2", comboName);
							combo2 = buildCascadeList(comboStore, comboName, clData, 1, comboLevel, prefix);

							/**
							 * record bank name and bank branch in database - only for BD BDT
							 */
							/*if(comboName.equalsIgnoreCase("CMBBAN") && comboLevel == 3) {
								for (String recordKey : combo2.keySet()) {
									CascadeListRecord clRecord = (CascadeListRecord) combo2.get(recordKey);
									String bankcode = clRecord.getColumnData().get(CascadeListRecord.LIST_VALUE);
									String bankname = clRecord.getColumnData().get(CascadeListRecord.LIST_TEXT);
									Bank bank = new Bank(bankcode, bankname);
									//boolean addedBank = bankService.addBank(bank);
									//System.out.println("new bank record added in database : " + bank);

									HashMap<String, AbstractDASRecord> locationCombo = clRecord.getSubCombo();
									if(locationCombo != null) {
										for (String locKey : locationCombo.keySet()) {
											CascadeListRecord LocRecord = (CascadeListRecord) locationCombo.get(locKey);
											String locationcode = LocRecord.getColumnData().get(CascadeListRecord.LIST_VALUE);
											String locationname = LocRecord.getColumnData().get(CascadeListRecord.LIST_TEXT);

											HashMap<String, AbstractDASRecord> branchCombo = LocRecord.getSubCombo();
											if(branchCombo != null) {
												for (String branchKey : branchCombo.keySet()) {
													CascadeListRecord branchRecord = (CascadeListRecord) branchCombo.get(branchKey);
													String branchcode = LocRecord.getColumnData().get(CascadeListRecord.LIST_VALUE);
													String branchname = LocRecord.getColumnData().get(CascadeListRecord.LIST_TEXT);
													Branch branch = new Branch(bankcode, branchcode, branchname, locationcode, locationname);
													//boolean addedBranch = branchService.addBranch(branch);
													//System.out.println("new branch record added in database : " + branch);
												}
											}
										}
									}
								}
							}*/

							//printCascadeList(prefix, combo2, apnTemplate, apnField, comboLevel);
							//combo2 = new HashMap<String, AbstractDASRecord>();
						}
					}
				}
				
				//Set<Map.Entry<String,HashMap<String,AbstractDASRecord>>> entrySetSorted = comboStore.entrySet().stream().sorted().collect(Collectors.toSet());
				for(Map.Entry<String,HashMap<String,AbstractDASRecord>> entry : comboStore.entrySet()) {
					String entryKey = entry.getKey();
					if(!entryKey.contains("####")) {
						/*HashMap<String,AbstractDASRecord> comboMap = entry.getValue();
						Comparator<Map.Entry<String,AbstractDASRecord>> comparator = new Comparator<Map.Entry<String,AbstractDASRecord>>() {

							@Override
							public int compare(Map.Entry<String, AbstractDASRecord> o1,
									Map.Entry<String, AbstractDASRecord> o2) {
								String t1 = o1.getValue().getColumnData().get(CascadeListRecord.LIST_TEXT);
								String t2 = o2.getValue().getColumnData().get(CascadeListRecord.LIST_TEXT);
								
								return t1.compareToIgnoreCase(t2);
							}
						};*/
						
						//comboMap.entrySet().stream().sorted(Comparator.comparing((Map.Entry<String,AbstractDASRecord>)e -> {}, null));

						ArrayList<String> dropDownOptionList = new ArrayList<String>();
						printCascadeList2(prefix, combo2, dropDownOptionList);
						
						apnTemplate.getDropdownListMap().put(entryKey, dropDownOptionList);
					}
				}

				templateList.add(apnTemplate);
			}
		}

		APNTemplateExcelDelegator apnTemplateExcelDelegator = new APNTemplateExcelDelegator(templateList);

		String dateStr = UtilFunctions.getCurrentDate();
		String filePath = wugwRuntime.getProperty("DAS.output.folder") + "\\" + "D2BDynamicTemplate" + "_" + countryCode
				+ "_" + naid + "_" + env + "_" + dateStr + ".xls";

		try {
			ExcelFileWriter.writeToExcel(filePath, apnTemplateExcelDelegator);
		} catch (ExcelException e) {
			UtilFunctions.loggingException(e);
		} catch (IOException e) {
			UtilFunctions.loggingException(e);
		}
	}

	private HashMap<String, AbstractDASRecord> callCascadeListService(WUTransaction clData) {
		GetCascadeList clService = ApplicationContextProvider.getBean("getCascadeList", GetCascadeList.class);
		String key = ("GetCascadeList" + "-" + this.countryCode + "-" + this.naid).toUpperCase();
		String dasXMLStr = this.getDASXMLStr(key, clData);
		HashMap<String, AbstractDASRecord> combo2 = new HashMap<String, AbstractDASRecord>();
		
		if (dasXMLStr.equals(""))
			return combo2;

		clData.getDasQueryData().clear();

		boolean dataMore = false;
		do {
			String dasReplyStr = UtilFunctions.requestGatewayService(serviceURL, dasXMLStr);
			dataMore = clService.execute(dasReplyStr, clData);
			dasXMLStr = GWMessageAssembleUtils.assembleDASRequestMsg(xmlHandlerAIS, dasXMLStr, clData);
		} while (dataMore);

		if (clData.getDasFilters().containsKey("queryfilter6")) {
			clData.getDasFilters().remove("queryfilter6");
		}

		DASQueryResult d2bClData = clData.getDasQueryData();
		for (AbstractDASRecord clRecord : d2bClData.getRecordSet()) {
			// UtilFunctions.logger.info(prefix + clRecord);
			combo2.put(clRecord.getColumnData().get(CascadeListRecord.LIST_TEXT), clRecord);
		}
		
		return combo2;
	}

	/**
	 * 
	 */
	private HashMap<String, AbstractDASRecord> buildCascadeList(HashMap<String, HashMap<String, AbstractDASRecord>> comboStore, 
			String comboKey, WUTransaction clData, int currLevel, int tarLevel, String prefix) {
		
		HashMap<String, AbstractDASRecord> combo2 = null;
		if(comboStore.containsKey(comboKey)) {
			combo2 = comboStore.get(comboKey);
		} else {
			combo2 = callCascadeListService(clData);
			comboStore.put(comboKey, combo2);
		}

		if (currLevel < tarLevel) {
			for (String listText : combo2.keySet()) {
				CascadeListRecord clRecord = (CascadeListRecord) combo2.get(listText);
				//String lText = clRecord.getColumnData().get(CascadeListRecord.LIST_TEXT);

				clData.getDasFilters().put("queryfilter" + String.valueOf(currLevel + 2), listText);
				String subComboKey = comboKey + "####" + listText;
				HashMap<String, AbstractDASRecord> subCombo2 = buildCascadeList(comboStore, subComboKey, clData, currLevel + 1, tarLevel, prefix + prefix);
				clRecord.setSubCombo(subCombo2);
				clData.getDasFilters().remove("queryfilter" + String.valueOf(currLevel + 2));
			}
		}
		
		return combo2;
	}
	
	private void printCascadeList2(String prefix, HashMap<String, AbstractDASRecord> combo, ArrayList<String> dropDownOptionList) {
		for (String key : combo.keySet()) {
			CascadeListRecord clRecord = (CascadeListRecord) combo.get(key);
			dropDownOptionList.add(prefix + clRecord);

			if (clRecord.getSubCombo() != null && clRecord.getSubCombo().size() > 0) {
				printCascadeList2(prefix + prefix, clRecord.getSubCombo(), dropDownOptionList);
			}
		}
	}

	private void printCascadeList(String prefix, HashMap<String, AbstractDASRecord> combo,
			APNTemplate apnTemplate, APNTemplateDataEntry apnField, int comboLevel) {
		for (String key : combo.keySet()) {
			int comboL = comboLevel;
			CascadeListRecord clRecord = (CascadeListRecord) combo.get(key);
			UtilFunctions.d2bLogger.info(prefix + clRecord);

			// if(!apnTemplate.getCorridor().equals("BD BDT")) {
			 	apnField.addDropdownOption(prefix + clRecord);
			// }
			//apnField.addDropdownOption(clRecord.getColumnData().get(CascadeListRecord.LIST_VALUE),
										//clRecord.getColumnData().get(CascadeListRecord.LIST_TEXT));

			if (clRecord.getSubCombo() != null && clRecord.getSubCombo().size() > 0 && (comboL--) > 0) {
				printCascadeList(prefix + prefix, clRecord.getSubCombo(), apnTemplate, apnField, comboL);
			}
		}
	}
}
