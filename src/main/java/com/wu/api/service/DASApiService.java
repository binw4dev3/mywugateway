package com.wu.api.service;

import com.wu.WUGWRuntime;
import com.wu.config.ApplicationContextProvider;
import com.wu.gw.model.WUTransaction;
import com.wu.gw.model.das.DASQueryResult;
import com.wu.gw.service.DASQueryService;
import com.wu.gw.service.GetCountriesCurrencies;
import com.wu.gw.service.GetMexicoCityState;
import com.wu.gw.service.GetUSStateList;
import com.wu.gw.util.UtilFunctions;
import com.wu.xmlhandler.GWMessageAssembleUtils;
import com.wu.xmlhandler.XMLAssemblerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Reusable service for single-step DAS query operations.
 * Extracts the core execution logic from DASServiceRequest / console layer,
 * taking typed parameters and returning structured data instead of writing Excel files.
 */
@Service
public class DASApiService {

    @Autowired
    private WUGWRuntime wugwRuntime;

    @Autowired
    private GetCountriesCurrencies getCountriesCurrencies;

    @Autowired
    private GetUSStateList getUSStateList;

    @Autowired
    private GetMexicoCityState getMexicoCityState;

    // -----------------------------------------------------------------------
    // Public entry points for each DAS operation
    // -----------------------------------------------------------------------

    public DASQueryResult queryCountriesCurrencies(String env, String countryCode, String naid) {
        WUTransaction txnData = ApplicationContextProvider.getBean("countriesCurrencies", WUTransaction.class);
        return executeQuery(env, countryCode, naid, txnData, getCountriesCurrencies);
    }

    public DASQueryResult queryUSStates(String env, String countryCode, String naid) {
        WUTransaction txnData = ApplicationContextProvider.getBean("usStateInfo", WUTransaction.class);
        return executeQuery(env, countryCode, naid, txnData, getUSStateList);
    }

    public DASQueryResult queryMexicoCityState(String env, String countryCode, String naid) {
        WUTransaction txnData = ApplicationContextProvider.getBean("mexicoCityState", WUTransaction.class);
        return executeQuery(env, countryCode, naid, txnData, getMexicoCityState);
    }

    // -----------------------------------------------------------------------
    // Core DAS execution loop — shared across all single-step DAS queries
    // -----------------------------------------------------------------------

    /**
     * Resolves the DAS XML template by (dasName, countryCode, naid) key,
     * assembles the SOAP request, calls the WU gateway, and paginates until
     * DATA_MORE = N. Returns the fully populated DASQueryResult.
     */
    public DASQueryResult executeQuery(String env, String countryCode, String naid,
                                       WUTransaction txnData, DASQueryService dasService) {
        Map<String, File> dasAISMap;
        Map<String, File> dasBISMap;
        String serviceURL;

        String envUpper = env.toUpperCase();
        if ("PI".equals(envUpper)) {
            dasAISMap = wugwRuntime.getDasAISMapPI();
            dasBISMap = wugwRuntime.getDasBISMapPI();
        } else if ("PROD".equals(envUpper)) {
            dasAISMap = wugwRuntime.getDasAISMapProd();
            dasBISMap = wugwRuntime.getDasBISMapProd();
        } else {
            throw new IllegalArgumentException("Invalid environment: " + env + ". Must be PI or PROD.");
        }
        serviceURL = wugwRuntime.getProperty(envUpper + ".domain");

        String key = (txnData.getDasName() + "-" + countryCode + "-" + naid).toUpperCase();

        File dasFile;
        XMLAssemblerHandler xmlHandler;

        if (dasAISMap.containsKey(key)) {
            dasFile = dasAISMap.get(key);
            xmlHandler = wugwRuntime.getAISAssemblerHandler();
        } else if (dasBISMap.containsKey(key)) {
            dasFile = dasBISMap.get(key);
            xmlHandler = wugwRuntime.getBISAssemblerHandler();
        } else {
            throw new IllegalArgumentException("No DAS XML template found for key: " + key);
        }

        String dasXMLStr;
        try {
            dasXMLStr = UtilFunctions.readFileToString(dasFile);
            dasXMLStr = GWMessageAssembleUtils.assembleDASRequestMsg(xmlHandler, dasXMLStr, txnData);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DAS XML template: " + e.getMessage(), e);
        }

        boolean dataMore;
        do {
            String reply = UtilFunctions.requestGatewayService(serviceURL, dasXMLStr);
            dataMore = dasService.execute(reply, txnData);
            if (dataMore) {
                dasXMLStr = GWMessageAssembleUtils.assembleDASRequestMsg(xmlHandler, dasXMLStr, txnData);
            }
        } while (dataMore);

        return txnData.getDasQueryData();
    }
}
