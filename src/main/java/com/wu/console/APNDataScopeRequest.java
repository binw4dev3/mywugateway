package com.wu.console;

import com.wu.WUGWRuntime;
import com.wu.config.ApplicationContextProvider;
import com.wu.excel.ExcelException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.wu.excel.impl.APNTemplateExcelDelegator;
import com.wu.excel.impl.D2BDataScopeExcelDelegator;
import com.wu.excel.impl.ExcelFileWriter;
import com.wu.gw.model.APNTemplate;
import com.wu.gw.model.APNTemplateDataEntry;
import com.wu.gw.model.WUTransaction;
import com.wu.gw.model.das.*;
import com.wu.gw.service.GetCascadeList;
import com.wu.gw.service.GetCountriesCurrencies;
import com.wu.gw.service.GetDeliveryOptionTemplate;
import com.wu.gw.service.GetDeliveryServices;
import com.wu.gw.util.UtilFunctions;
import com.wu.xmlhandler.GWMessageAssembleUtils;
import com.wu.xmlhandler.XMLAssemblerHandler;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope("prototype")
public class APNDataScopeRequest extends AbstractServiceRequest {

    @Value("${APNDataScopeRequest.serialNum}")
    private String serialNum;

    @Value("${APNDataScopeRequest.description}")
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
    private String productCode = null;
    private String naid = null;
    private ArrayList<APNTemplate> templateList = null;

    private String prefix = "----";

    public APNDataScopeRequest() {
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

            //if(!ctrCode.equalsIgnoreCase("BD")) continue;

            String destCtrCur = ctrCode + " " + curCode;

            //if(!destCtrCur.equals("AL ALL")) continue;

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

                    if (comboNum > 0) {
                        comboNum--;
                        continue;
                    }

                    UtilFunctions.d2bLogger.info("\t" + recordDOT.toString());

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

                        if (fType.equals("COMBO") && tLineData.length == 11 && tLineData[6].trim().equals("NL")) {
                            comboNum = Integer.parseInt(tLineData[10].trim());
                        } else if (fType.equals("COMBO2") && tLineData.length == 11
                                && tLineData[6].trim().equals("NL")) {
                            String comboType = tLineData[10].trim();
                            String[] comboTypeArray = comboType.split(",");
                            String comboName = comboTypeArray[0].trim();
                            String comboLevelStr = comboTypeArray[1].trim();

                            apnField.setCascadeListLevel(comboLevelStr);
                            apnField.setIsCascadeList(true);
                            apnField.setCascadeListName(comboName);
                        }
                    }
                }

                templateList.add(apnTemplate);
            }
        }

        D2BDataScopeExcelDelegator d2BDataScopeExcelDelegator = new D2BDataScopeExcelDelegator(templateList);

        String dateStr = UtilFunctions.getCurrentDate();
        String filePath = wugwRuntime.getProperty("DAS.output.folder") + "\\" + "D2BDataScope" + "_" + countryCode
                + "_" + naid + "_" + env + "_" + dateStr + ".xls";

        try {
            ExcelFileWriter.writeToExcel(filePath, d2BDataScopeExcelDelegator);
        } catch (ExcelException e) {
            UtilFunctions.loggingException(e);
        } catch (IOException e) {
            UtilFunctions.loggingException(e);
        }
    }
}
