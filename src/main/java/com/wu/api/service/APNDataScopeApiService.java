package com.wu.api.service;

import com.wu.WUGWRuntime;
import com.wu.config.ApplicationContextProvider;
import com.wu.gw.model.APNTemplate;
import com.wu.gw.model.APNTemplateDataEntry;
import com.wu.gw.model.WUTransaction;
import com.wu.gw.model.das.*;
import com.wu.gw.service.GetCountriesCurrencies;
import com.wu.gw.service.GetDeliveryOptionTemplate;
import com.wu.gw.service.GetDeliveryServices;
import com.wu.gw.util.UtilFunctions;
import com.wu.xmlhandler.GWMessageAssembleUtils;
import com.wu.xmlhandler.XMLAssemblerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * API service for APN Data Scope retrieval.
 * Mirrors APNDataScopeRequest.execute() but returns List<APNTemplate>
 * instead of writing to an Excel file.
 *
 * The data scope report includes field-level metadata (name, type,
 * max length, required flag, cascade list name) without fetching the
 * full cascade list data — making it faster than the full dynamic-template call.
 */
@Service
public class APNDataScopeApiService {

    @Autowired
    private WUGWRuntime wugwRuntime;

    @Autowired
    private GetCountriesCurrencies getCountriesCurrencies;

    @Autowired
    private GetDeliveryServices getDeliveryServices;

    @Autowired
    private GetDeliveryOptionTemplate getDeliveryOptionTemplate;

    public List<APNTemplate> execute(String env, String countryCode, String naid, String productCode) {
        Map<String, File> dasAISMap;
        Map<String, File> dasBISMap;

        String envUpper = env.toUpperCase();
        if ("PI".equals(envUpper)) {
            dasAISMap = wugwRuntime.getDasAISMapPI();
            dasBISMap = wugwRuntime.getDasBISMapPI();
        } else if ("PROD".equals(envUpper)) {
            dasAISMap = wugwRuntime.getDasAISMapProd();
            dasBISMap = wugwRuntime.getDasBISMapProd();
        } else {
            throw new IllegalArgumentException("Invalid environment: " + env);
        }

        String serviceURL = wugwRuntime.getProperty(envUpper + ".domain");
        XMLAssemblerHandler xmlHandlerAIS = wugwRuntime.getAISAssemblerHandler();
        XMLAssemblerHandler xmlHandlerBIS = wugwRuntime.getBISAssemblerHandler();

        List<APNTemplate> templateList = new ArrayList<>();

        // Step 1 — Countries/Currencies
        WUTransaction ccData = ApplicationContextProvider.getBean("countriesCurrencies", WUTransaction.class);
        String key = ("GetCountriesCurrencies-" + countryCode + "-" + naid).toUpperCase();
        String dasXMLStr = getDASXMLStr(key, ccData, dasAISMap, dasBISMap, xmlHandlerAIS, xmlHandlerBIS);
        if (dasXMLStr.isEmpty()) return templateList;

        boolean dataMore;
        do {
            String reply = UtilFunctions.requestGatewayService(serviceURL, dasXMLStr);
            dataMore = getCountriesCurrencies.execute(reply, ccData);
            dasXMLStr = GWMessageAssembleUtils.assembleDASRequestMsg(xmlHandlerAIS, dasXMLStr, ccData);
        } while (dataMore);

        // Step 2 — Delivery Services per corridor
        for (AbstractDASRecord ccRecord : ccData.getDasQueryData().getRecordSet()) {
            String ctrCode = ccRecord.getColumnData().get(CountriesCurrenciesRecord.COUNTRY_CODE);
            String curCode = ccRecord.getColumnData().get(CountriesCurrenciesRecord.CURRENCY_CODE);
            String destCtrCur = ctrCode + " " + curCode;

            WUTransaction svcData = ApplicationContextProvider.getBean("deliveryServices", WUTransaction.class);
            svcData.getDasFilters().put("queryfilter3", destCtrCur);
            svcData.getDasFilters().put("queryfilter4", productCode);

            key = ("GetDeliveryServices-" + countryCode + "-" + naid).toUpperCase();
            dasXMLStr = getDASXMLStr(key, svcData, dasAISMap, dasBISMap, xmlHandlerAIS, xmlHandlerBIS);
            if (dasXMLStr.isEmpty()) return templateList;

            dataMore = false;
            do {
                String reply = UtilFunctions.requestGatewayService(serviceURL, dasXMLStr);
                dataMore = getDeliveryServices.execute(reply, svcData);
                dasXMLStr = GWMessageAssembleUtils.assembleDASRequestMsg(xmlHandlerAIS, dasXMLStr, svcData);
            } while (dataMore);

            // Step 3 — Option Template per delivery service
            for (AbstractDASRecord svcRecord : svcData.getDasQueryData().getRecordSet()) {
                APNTemplate apnTemplate = new APNTemplate();
                apnTemplate.setCorridor(destCtrCur);

                String templt = svcRecord.getColumnData().get(DeliveryServicesRecord.TEMPLT);
                String route = svcRecord.getColumnData().get(DeliveryServicesRecord.ROUTE);
                String svcdesp = svcRecord.getColumnData().get(DeliveryServicesRecord.DESCRIPTION);

                apnTemplate.setTemplateID(templt);
                apnTemplate.setServiceDesp(svcdesp);
                apnTemplate.setRoute(route);
                apnTemplate.setServiceCode(svcRecord.getColumnData().get(DeliveryServicesRecord.SVC_CODE));

                WUTransaction dotData = ApplicationContextProvider.getBean("deliveryOptionTemplate", WUTransaction.class);
                dotData.getDasFilters().put("queryfilter2", destCtrCur);
                dotData.getDasFilters().put("queryfilter3", templt);

                key = ("GetDeliveryOptionTemplate-" + countryCode + "-" + naid).toUpperCase();
                dasXMLStr = getDASXMLStr(key, dotData, dasAISMap, dasBISMap, xmlHandlerAIS, xmlHandlerBIS);
                if (dasXMLStr.isEmpty()) return templateList;

                dataMore = false;
                do {
                    String reply = UtilFunctions.requestGatewayService(serviceURL, dasXMLStr);
                    dataMore = getDeliveryOptionTemplate.execute(reply, dotData);
                    dasXMLStr = GWMessageAssembleUtils.assembleDASRequestMsg(xmlHandlerAIS, dasXMLStr, dotData);
                } while (dataMore);

                // Parse fields — lighter than full dynamic (no cascade list fetching)
                int comboNum = 0;
                APNTemplateDataEntry apnField = null;

                for (AbstractDASRecord dotRecord : dotData.getDasQueryData().getRecordSet()) {
                    String tIndex = dotRecord.getColumnData().get(DeliveryOptionTemplateRecord.T_INDEX);
                    String desp = dotRecord.getColumnData().get(DeliveryOptionTemplateRecord.DESCRIPTION);
                    desp = desp.replace("&amp;", "&");
                    String[] tLineData = desp.split(";");

                    if (comboNum > 0) {
                        comboNum--;
                        continue;
                    }

                    if (!"000".equals(tIndex) && tLineData.length >= 6) {
                        apnField = apnTemplate.addDataField(tLineData[0].trim());
                        apnField.setMaxLength(tLineData[1].trim());
                        apnField.setFieldCHIPID(tLineData[2].trim());

                        String fRequired = tLineData[3].trim();
                        apnField.setRequired(fRequired.contains("required") ? "Required" : "Optional");

                        String fType = tLineData[5].trim();
                        if ("COMBO".equalsIgnoreCase(fType)) {
                            apnField.setFieldType("DropDown List");
                            if (tLineData.length == 11 && "NL".equals(tLineData[6].trim())) {
                                comboNum = Integer.parseInt(tLineData[10].trim());
                            }
                        } else if ("COMBO2".equalsIgnoreCase(fType)) {
                            apnField.setFieldType("DropDown Cascade List");
                            if (tLineData.length == 11 && "NL".equals(tLineData[6].trim())) {
                                String[] comboTypeArray = tLineData[10].trim().split(",");
                                apnField.setCascadeListLevel(comboTypeArray[1].trim());
                                apnField.setIsCascadeList(true);
                                apnField.setCascadeListName(comboTypeArray[0].trim());
                            }
                        } else {
                            apnField.setFieldType(fType);
                            apnField.addDropdownOption("");
                        }
                    }
                }

                templateList.add(apnTemplate);
            }
        }

        return templateList;
    }

    // -----------------------------------------------------------------------

    private String getDASXMLStr(String key, WUTransaction txnData,
                                 Map<String, File> dasAISMap, Map<String, File> dasBISMap,
                                 XMLAssemblerHandler xmlHandlerAIS, XMLAssemblerHandler xmlHandlerBIS) {
        try {
            if (dasAISMap.containsKey(key)) {
                String xml = UtilFunctions.readFileToString(dasAISMap.get(key));
                return GWMessageAssembleUtils.assembleDASRequestMsg(xmlHandlerAIS, xml, txnData);
            } else if (dasBISMap.containsKey(key)) {
                String xml = UtilFunctions.readFileToString(dasBISMap.get(key));
                return GWMessageAssembleUtils.assembleDASRequestMsg(xmlHandlerBIS, xml, txnData);
            }
        } catch (IOException ioe) {
            UtilFunctions.loggingException(ioe);
        }
        UtilFunctions.logger.info("DAS XML template not found: " + key);
        return "";
    }
}
