package com.wu.config;

import java.util.HashMap;

import com.wu.console.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.wu.WUGWRuntime;
import com.wu.gw.model.WUTransaction;
import com.wu.gw.service.GetCascadeList;
import com.wu.gw.service.GetCountriesCurrencies;
import com.wu.gw.service.GetDeliveryOptionTemplate;
import com.wu.gw.service.GetDeliveryServices;
import com.wu.gw.service.GetMexicoCityState;
import com.wu.gw.service.GetUSStateList;
import com.wu.xmlhandler.XMLAssemblerHandler;
import org.springframework.beans.factory.annotation.Qualifier;

@Configuration
public class WUGWConfig {

    // --- DAS query services (singletons) ---

    @Bean
    @Scope("singleton")
    public GetCountriesCurrencies getCountriesCurrencies() {
        return new GetCountriesCurrencies();
    }

    @Bean
    @Scope("singleton")
    public GetUSStateList getUSStateList() {
        return new GetUSStateList();
    }

    @Bean
    @Scope("singleton")
    public GetMexicoCityState getMexicoCityState() {
        return new GetMexicoCityState();
    }

    @Bean
    @Scope("singleton")
    public GetDeliveryServices getDeliveryServices() {
        return new GetDeliveryServices();
    }

    @Bean
    @Scope("singleton")
    public GetDeliveryOptionTemplate getDeliveryOptionTemplate() {
        return new GetDeliveryOptionTemplate();
    }

    @Bean
    @Scope("singleton")
    public GetCascadeList getCascadeList() {
        return new GetCascadeList();
    }

    // --- WUTransaction prototypes (fresh instance per use) ---

    @Bean
    @Scope("prototype")
    public WUTransaction countriesCurrencies() {
        WUTransaction cc = new WUTransaction();
        cc.setDasName("GetCountriesCurrencies");
        HashMap<String, String> dasFilters = new HashMap<>();
        dasFilters.put("queryfilter3", "");
        dasFilters.put("queryfilter4", "");
        cc.setDasFilters(dasFilters);
        return cc;
    }

    @Bean
    @Scope("prototype")
    public WUTransaction usStateInfo() {
        WUTransaction cc = new WUTransaction();
        cc.setDasName("GetUSStateList");
        HashMap<String, String> dasFilters = new HashMap<>();
        dasFilters.put("queryfilter2", "");
        cc.setDasFilters(dasFilters);
        return cc;
    }

    @Bean
    @Scope("prototype")
    public WUTransaction mexicoCityState() {
        WUTransaction cc = new WUTransaction();
        cc.setDasName("GetMexicoCityState");
        HashMap<String, String> dasFilters = new HashMap<>();
        dasFilters.put("queryfilter2", "");
        dasFilters.put("queryfilter3", "");
        cc.setDasFilters(dasFilters);
        return cc;
    }

    @Bean
    @Scope("prototype")
    public WUTransaction deliveryServices() {
        WUTransaction cc = new WUTransaction();
        cc.setDasName("GetDeliveryServices");
        HashMap<String, String> dasFilters = new HashMap<>();
        dasFilters.put("queryfilter3", "");
        dasFilters.put("queryfilter4", "");
        cc.setDasFilters(dasFilters);
        return cc;
    }

    @Bean
    @Scope("prototype")
    public WUTransaction deliveryOptionTemplate() {
        WUTransaction cc = new WUTransaction();
        cc.setDasName("GetDeliveryOptionTemplate");
        HashMap<String, String> dasFilters = new HashMap<>();
        dasFilters.put("queryfilter2", "");
        dasFilters.put("queryfilter3", "");
        dasFilters.put("queryfilter4", "");
        cc.setDasFilters(dasFilters);
        return cc;
    }

    @Bean
    @Scope("prototype")
    public WUTransaction cascadeList() {
        WUTransaction cc = new WUTransaction();
        cc.setDasName("GetCascadeList");
        HashMap<String, String> dasFilters = new HashMap<>();
        dasFilters.put("queryfilter2", "");
        cc.setDasFilters(dasFilters);
        return cc;
    }

    // --- Request map (all request beans injected by Spring) ---

    @Bean
    @Scope("prototype")
    public HashMap<String, AbstractServiceRequest> requestMap(
            APNDynamicRequest apnDynamicRequest,
            GenerateMTCNRequest genMTCNRequest,
            CountryCurrencyListRequest countryCurrencyListRequest,
            USStateListRequest usStateListRequest,
            MexicoCityStateListRequest mexicoCityStateListRequest,
            PricingTableRequest pricingTableRequest,
            DeliveryServiceListRequest deliveryServiceListRequest,
            APNDataScopeRequest apnDataScopeRequest,
            ExitRequest exitRequest) {
        HashMap<String, AbstractServiceRequest> requestMap = new HashMap<>();
        requestMap.put(genMTCNRequest.getRequestID(), genMTCNRequest);
        requestMap.put(countryCurrencyListRequest.getRequestID(), countryCurrencyListRequest);
        requestMap.put(usStateListRequest.getRequestID(), usStateListRequest);
        requestMap.put(mexicoCityStateListRequest.getRequestID(), mexicoCityStateListRequest);
        requestMap.put(apnDynamicRequest.getRequestID(), apnDynamicRequest);
        requestMap.put(pricingTableRequest.getRequestID(), pricingTableRequest);
        requestMap.put(deliveryServiceListRequest.getRequestID(), deliveryServiceListRequest);
        requestMap.put(apnDataScopeRequest.getRequestID(), apnDataScopeRequest);
        requestMap.put(exitRequest.getRequestID(), exitRequest);
        return requestMap;
    }

    @Bean
    @Scope("singleton")
    public WUGWRuntime WUGWRuntime(
            @Qualifier("AISAssemblerHandler") XMLAssemblerHandler AISAssemblerHandler,
            @Qualifier("BISAssemblerHandler") XMLAssemblerHandler BISAssemblerHandler,
            HashMap<String, AbstractServiceRequest> requestMap) {
        WUGWRuntime runtime = new WUGWRuntime();
        runtime.setRequestMap(requestMap);
        runtime.setAISAssemblerHandler(AISAssemblerHandler);
        runtime.setBISAssemblerHandler(BISAssemblerHandler);
        return runtime;
    }
}
