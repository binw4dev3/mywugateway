package com.wu.api.service;

import com.wu.WUGWRuntime;
import com.wu.config.ApplicationContextProvider;
import com.wu.gw.model.APNTemplate;
import com.wu.gw.model.ExcelDataEntry;
import com.wu.gw.model.WUTransaction;
import com.wu.gw.model.ais.CountryCurrencyInfo;
import com.wu.gw.model.ais.IsoCode;
import com.wu.gw.model.das.*;
import com.wu.gw.service.GetCountriesCurrencies;
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
 * API service for delivery service list discovery.
 * Mirrors DeliveryServiceListRequest.execute() but returns List<APNTemplate>
 * instead of writing to Excel. Each entry includes corridor, service code,
 * template ID, and fee-inquiry availability status.
 */
@Service
public class DeliveryServiceApiService {

    @Autowired
    private WUGWRuntime wugwRuntime;

    @Autowired
    private GetCountriesCurrencies getCountriesCurrencies;

    @Autowired
    private GetDeliveryServices getDeliveryServices;

    public List<APNTemplate> execute(String env, String countryCode, String naid) {
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

        // Step 2 — Delivery services per corridor, with fee-inquiry status check
        for (AbstractDASRecord ccRecord : ccData.getDasQueryData().getRecordSet()) {
            String ctrCode = ccRecord.getColumnData().get(CountriesCurrenciesRecord.COUNTRY_CODE);
            String curCode = ccRecord.getColumnData().get(CountriesCurrenciesRecord.CURRENCY_CODE);
            String destCtrCur = ctrCode + " " + curCode;

            WUTransaction svcData = ApplicationContextProvider.getBean("deliveryServices", WUTransaction.class);
            svcData.getDasFilters().put("queryfilter3", destCtrCur);
            svcData.getDasFilters().put("queryfilter4", "ALL");

            key = ("GetDeliveryServices-" + countryCode + "-" + naid).toUpperCase();
            dasXMLStr = getDASXMLStr(key, svcData, dasAISMap, dasBISMap, xmlHandlerAIS, xmlHandlerBIS);
            if (dasXMLStr.isEmpty()) return templateList;

            dataMore = false;
            do {
                String reply = UtilFunctions.requestGatewayService(serviceURL, dasXMLStr);
                dataMore = getDeliveryServices.execute(reply, svcData);
                dasXMLStr = GWMessageAssembleUtils.assembleDASRequestMsg(xmlHandlerAIS, dasXMLStr, svcData);
            } while (dataMore);

            for (AbstractDASRecord svcRecord : svcData.getDasQueryData().getRecordSet()) {
                APNTemplate apnTemplate = new APNTemplate();
                apnTemplate.setCorridor(destCtrCur);
                apnTemplate.setServiceCode(svcRecord.getColumnData().get(DeliveryServicesRecord.SVC_CODE));
                apnTemplate.setTemplateID(svcRecord.getColumnData().get(DeliveryServicesRecord.TEMPLT));
                apnTemplate.setServiceDesp(svcRecord.toString());

                // Fee-inquiry availability check
                String fiKey = ("FeeInquiry-" + countryCode + "-" + naid).toUpperCase();
                ExcelDataEntry sData = new ExcelDataEntry();
                sData.setTransactionType("WMN");
                sData.setAmount("10");
                sData.setServiceType(svcRecord.getColumnData().get(DeliveryServicesRecord.SVC_CODE));

                CountryCurrencyInfo ccInfo = new CountryCurrencyInfo();
                IsoCode isoCode = new IsoCode();
                isoCode.setCountryCode(ctrCode);
                isoCode.setCurrencyCode(curCode);
                ccInfo.setIsoCode(isoCode);
                sData.setReceiveCountryCurrency(ccInfo);

                String status = checkServiceAvailability(fiKey, sData, dasAISMap, dasBISMap,
                        xmlHandlerAIS, xmlHandlerBIS, serviceURL);
                apnTemplate.setStatus(status);

                templateList.add(apnTemplate);
            }
        }

        return templateList;
    }

    // -----------------------------------------------------------------------

    private String checkServiceAvailability(String key, ExcelDataEntry sData,
                                             Map<String, File> dasAISMap, Map<String, File> dasBISMap,
                                             XMLAssemblerHandler xmlHandlerAIS, XMLAssemblerHandler xmlHandlerBIS,
                                             String serviceURL) {
        try {
            if (dasAISMap.containsKey(key)) {
                String fiXMLStr = UtilFunctions.readFileToString(dasAISMap.get(key));
                fiXMLStr = GWMessageAssembleUtils.assembleAISFIRequestMsg(xmlHandlerAIS, fiXMLStr, sData);
                String reply = UtilFunctions.requestGatewayService(serviceURL, fiXMLStr);
                return reply.startsWith("error - ") ? reply.replaceFirst("error - ", "") : "Available";
            } else if (dasBISMap.containsKey(key)) {
                String fiXMLStr = UtilFunctions.readFileToString(dasBISMap.get(key));
                fiXMLStr = GWMessageAssembleUtils.assembleBISFIRequestMsg(xmlHandlerBIS, fiXMLStr, sData);
                String reply = UtilFunctions.requestGatewayService(serviceURL, fiXMLStr);
                return reply.startsWith("error - ") ? reply.replaceFirst("error - ", "") : "Available";
            }
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
        return "No FI template available for: " + key;
    }

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
