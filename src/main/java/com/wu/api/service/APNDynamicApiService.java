package com.wu.api.service;

import com.wu.WUGWRuntime;
import com.wu.config.ApplicationContextProvider;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * API service for the APN Dynamic Template operation.
 * Mirrors APNDynamicRequest.execute() but returns List<APNTemplate>
 * instead of writing to Excel.
 */
@Service
public class APNDynamicApiService {

    @Autowired
    private WUGWRuntime wugwRuntime;

    @Autowired
    private GetCountriesCurrencies getCountriesCurrencies;

    @Autowired
    private GetDeliveryServices getDeliveryServices;

    @Autowired
    private GetDeliveryOptionTemplate getDeliveryOptionTemplate;

    @Autowired
    private GetCascadeList getCascadeList;

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

        // Step 1 — Get Countries/Currencies
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

        // Step 2 — For each corridor, get Delivery Services
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

            // Step 3 — For each delivery service, get Option Template
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

                // Step 4 — Parse option template fields (dropdowns, cascade lists, free text)
                int comboNum = 0;
                List<String> combo1List = new ArrayList<>();
                Map<String, String> combo1 = new LinkedHashMap<>();
                Map<String, Map<String, AbstractDASRecord>> comboStore = new HashMap<>();
                APNTemplateDataEntry apnField = null;

                for (AbstractDASRecord dotRecord : dotData.getDasQueryData().getRecordSet()) {
                    String tIndex = dotRecord.getColumnData().get(DeliveryOptionTemplateRecord.T_INDEX);
                    String desp = dotRecord.getColumnData().get(DeliveryOptionTemplateRecord.DESCRIPTION);
                    desp = desp.replace("&amp;", "&");
                    String[] tLineData = desp.split(";");

                    if (comboNum > 0) {
                        combo1List.add(desp);
                        if (tLineData.length >= 4) {
                            combo1.put(tLineData[2].trim(), tLineData[3].trim());
                        }
                        comboNum--;
                        if (comboNum == 0 && apnField != null) {
                            for (Map.Entry<String, String> entry : combo1.entrySet()) {
                                apnField.addDropdownOption(entry.getKey().trim() + " - " + entry.getValue().trim());
                            }
                            combo1List.clear();
                            combo1.clear();
                        }
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
                                apnField.setNumOfDropdownOptions(comboNum);
                            }
                        } else if ("COMBO2".equalsIgnoreCase(fType)) {
                            apnField.setFieldType("DropDown Cascade List");
                            if (tLineData.length == 11 && "NL".equals(tLineData[6].trim())) {
                                String[] comboTypeArray = tLineData[10].trim().split(",");
                                String comboName = comboTypeArray[0].trim();
                                String comboLevelStr = comboTypeArray[1].trim();
                                apnField.setCascadeListLevel(comboLevelStr);
                                apnField.setIsCascadeList(true);
                                apnField.setCascadeListName(comboName);

                                WUTransaction clData = ApplicationContextProvider.getBean("cascadeList", WUTransaction.class);
                                clData.getDasFilters().put("queryfilter2", comboName);
                                buildCascadeList(comboStore, comboName, clData, dasAISMap, dasBISMap,
                                        xmlHandlerAIS, serviceURL, 1, Integer.parseInt(comboLevelStr), "----");
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
    // Helpers
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

    private Map<String, AbstractDASRecord> buildCascadeList(
            Map<String, Map<String, AbstractDASRecord>> comboStore,
            String comboName, WUTransaction clData,
            Map<String, File> dasAISMap, Map<String, File> dasBISMap,
            XMLAssemblerHandler xmlHandlerAIS, String serviceURL,
            int currentLevel, int maxLevel, String prefix) {

        if (comboStore.containsKey(comboName)) {
            return comboStore.get(comboName);
        }

        String serviceURL2 = wugwRuntime.getProperty("PI.domain");
        XMLAssemblerHandler xmlHandler = wugwRuntime.getAISAssemblerHandler();

        Map<String, File> clAISMap = wugwRuntime.getDasAISMapPI();
        // Use the first available GetCascadeList key in the map
        String clKey = clAISMap.keySet().stream()
                .filter(k -> k.startsWith("GETCASCADELIST"))
                .findFirst().orElse(null);
        if (clKey == null) return new HashMap<>();

        try {
            String dasXMLStr = UtilFunctions.readFileToString(clAISMap.get(clKey));
            dasXMLStr = GWMessageAssembleUtils.assembleDASRequestMsg(xmlHandler, dasXMLStr, clData);
            boolean dataMore;
            do {
                String reply = UtilFunctions.requestGatewayService(serviceURL2, dasXMLStr);
                dataMore = getCascadeList.execute(reply, clData);
                dasXMLStr = GWMessageAssembleUtils.assembleDASRequestMsg(xmlHandler, dasXMLStr, clData);
            } while (dataMore);
        } catch (IOException e) {
            UtilFunctions.loggingException(e);
        }

        Map<String, AbstractDASRecord> result = new LinkedHashMap<>();
        comboStore.put(comboName, result);
        return result;
    }
}
