package com.wu.console;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import com.wu.config.ApplicationContextProvider;

import com.wu.WUGWRuntime;
import com.wu.excel.ExcelException;
import com.wu.excel.impl.DeliveryServiceListExcelDelegator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.wu.excel.impl.ExcelFileWriter;
import com.wu.gw.model.APNTemplate;
import com.wu.gw.model.ExcelDataEntry;
import com.wu.gw.model.WUTransaction;
import com.wu.gw.model.ais.CountryCurrencyInfo;
import com.wu.gw.model.ais.IsoCode;
import com.wu.gw.model.das.AbstractDASRecord;
import com.wu.gw.model.das.CountriesCurrenciesRecord;
import com.wu.gw.model.das.DASQueryResult;
import com.wu.gw.model.das.DeliveryServicesRecord;
import com.wu.gw.service.GetCountriesCurrencies;
import com.wu.gw.service.GetDeliveryServices;
import com.wu.gw.util.UtilFunctions;
import com.wu.xmlhandler.GWMessageAssembleUtils;
import com.wu.xmlhandler.XMLAssemblerHandler;

@Component
@Scope("prototype")
public class DeliveryServiceListRequest extends AbstractServiceRequest {

        @Value("${DeliveryServiceListRequest.serialNum}")
        private String serialNum;

        @Value("${DeliveryServiceListRequest.description}")
        private String descriptionValue;

        @PostConstruct
        private void init() {
                setRequestID(serialNum);
                setDescription(descriptionValue);
        }

        private Map<String, File> dasAISMap;
        private Map<String, File> dasBISMap;

        private XMLAssemblerHandler xmlHandlerAIS;
        private XMLAssemblerHandler xmlHandlerBIS;

        private String env;
        private String serviceURL;

        private String countryCode = null;
        private String naid = null;
        private ArrayList<APNTemplate> templateList = null;

        public DeliveryServiceListRequest() {
                super();
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
                if (env.equalsIgnoreCase("PI")) {
                        dasAISMap = wugwRuntime.getDasAISMapPI();
                        dasBISMap = wugwRuntime.getDasBISMapPI();

                } else if (env.equalsIgnoreCase("PROD")) {
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

        private String checkServiceAvailability(String key, ExcelDataEntry sData) {
                String status = "Yes";
                try {
                        if (dasAISMap.containsKey(key)) {
                                File fiFile = dasAISMap.get(key);
                                String fiXMLStr = UtilFunctions.readFileToString(fiFile);

                                fiXMLStr = GWMessageAssembleUtils.assembleAISFIRequestMsg(xmlHandlerAIS, fiXMLStr, sData);
                                String fiReplyStr = UtilFunctions.requestGatewayService(serviceURL, fiXMLStr);

                                if (fiReplyStr.startsWith("error - ")) {
                                        status = fiReplyStr.replaceFirst("error - ", "");
                                }
                        } else if (dasBISMap.containsKey(key)) {
                                File fiFile = dasBISMap.get(key);
                                String fiXMLStr = UtilFunctions.readFileToString(fiFile);

                                fiXMLStr = GWMessageAssembleUtils.assembleBISFIRequestMsg(xmlHandlerBIS, fiXMLStr, sData);
                                String fiReplyStr = UtilFunctions.requestGatewayService(serviceURL, fiXMLStr);

                                if (fiReplyStr.startsWith("error - ")) {
                                        status = fiReplyStr.replaceFirst("error - ", "");
                                }
                        } else {
                                sData.setFee("The sending country/currency is not available : " + key);
                        }
                } catch (IOException ioe) {
                        status= ioe.getMessage();
                }

                return status;
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
                        String destCtrCur = ctrCode + " " + curCode;

                        WUTransaction svcData = ApplicationContextProvider.getBean("deliveryServices", WUTransaction.class);
                        svcData.getDasFilters().put("queryfilter3", destCtrCur);
                        svcData.getDasFilters().put("queryfilter4", "ALL");

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

                        // Store delivery service info and check service availability by calling Fee Inquiry API
                        DASQueryResult dasQueryDataSVC = svcData.getDasQueryData();
                        for (AbstractDASRecord recordSVC : dasQueryDataSVC.getRecordSet()) {
                                APNTemplate apnTemplate = new APNTemplate();
                                apnTemplate.setCorridor(destCtrCur);
                                apnTemplate.setServiceCode(recordSVC.getColumnData().get(DeliveryServicesRecord.SVC_CODE));

                                UtilFunctions.d2bLogger.info(destCtrCur);
                                UtilFunctions.d2bLogger.info("\t->\t" + recordSVC.toString());

                                String templt = recordSVC.getColumnData().get(DeliveryServicesRecord.TEMPLT);

                                apnTemplate.setTemplateID(templt);
                                apnTemplate.setServiceDesp(recordSVC.toString());

                                key = ("FeeInquiry" + "-" + this.countryCode + "-" + this.naid).toUpperCase();
                                ExcelDataEntry sData = new ExcelDataEntry();
                                sData.setTransactionType("WMN");
                                sData.setAmount("10");
                                sData.setServiceType(recordSVC.getColumnData().get(DeliveryServicesRecord.SVC_CODE));
                                CountryCurrencyInfo ccInfo = new CountryCurrencyInfo();
                                IsoCode isoCode = new IsoCode();
                                isoCode.setCountryCode(ctrCode);
                                isoCode.setCurrencyCode(curCode);
                                ccInfo.setIsoCode(isoCode);
                                sData.setReceiveCountryCurrency(ccInfo);
                                String status = checkServiceAvailability(key, sData);
                                apnTemplate.setStatus(status);

                                templateList.add(apnTemplate);
                        }
                }

                DeliveryServiceListExcelDelegator serviceListExcelDelegator = new DeliveryServiceListExcelDelegator(
                                templateList);

                String dateStr = UtilFunctions.getCurrentDate();
                String filePath = wugwRuntime.getProperty("DAS.output.folder") + "\\" + "DeliveryServiceList" + "_"
                                + countryCode + "_" + naid + "_" + env + "_" + dateStr + ".xls";

                try {
                        ExcelFileWriter.writeToExcel(filePath, serviceListExcelDelegator);
                } catch (ExcelException e) {
                        UtilFunctions.loggingException(e);
                } catch (IOException e) {
                        UtilFunctions.loggingException(e);
                }
        }

}
