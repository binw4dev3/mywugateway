package com.wu.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.wu.console.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.wu.WUGWRuntime;
import com.wu.gw.model.WUTransaction;
import com.wu.gw.service.GetCascadeList;
import com.wu.gw.service.GetCountriesCurrencies;
import com.wu.gw.service.GetDeliveryOptionTemplate;
import com.wu.gw.service.GetDeliveryServices;
import com.wu.gw.service.GetMexicoCityState;
import com.wu.gw.service.GetUSStateList;
import com.wu.xmlhandler.XMLAssemblerHandler;

@Configuration
public class WUGWConfig {

    // --- CountryCurrencyList ---
    @Value("${CountryCurrencyListRequest.serialNum}")
    private String countryCurrencySerialNum;
    @Value("${CountryCurrencyListRequest.description}")
    private String countryCurrencyDescription;
    @Value("${CountryCurrencyListRequest.dasName}")
    private String countryCurrencyDasName;

    // --- USStateList ---
    @Value("${USStateListRequest.serialNum}")
    private String usStateSerialNum;
    @Value("${USStateListRequest.description}")
    private String usStateDescription;
    @Value("${USStateListRequest.dasName}")
    private String usStateDasName;

    // --- MexicoCityState ---
    @Value("${MexicoCityStateRequest.serialNum}")
    private String mexicoCityStateSerialNum;
    @Value("${MexicoCityStateRequest.description}")
    private String mexicoCityStateDescription;
    @Value("${MexicoCityStateRequest.dasName}")
    private String mexicoCityStateDasName;

    // --- GenMTCN ---
    @Value("${GenMTCNRequest.serialNum}")
    private String genMtcnSerialNum;

    // --- PricingTable ---
    @Value("${PricingTableRequest.serialNum}")
    private String pricingTableSerialNum;
    @Value("${PricingTableRequest.description}")
    private String pricingTableDescription;

    // --- DeliveryServiceList ---
    @Value("${DeliveryServiceListRequest.serialNum}")
    private String deliveryServiceSerialNum;
    @Value("${DeliveryServiceListRequest.description}")
    private String deliveryServiceDescription;

    // --- APNDataScope ---
    @Value("${APNDataScopeRequest.serialNum}")
    private String apnDataScopeSerialNum;
    @Value("${APNDataScopeRequest.description}")
    private String apnDataScopeDescription;

    // --- Exit ---
    @Value("${ExitRequest.serialNum}")
    private String exitSerialNum;
    @Value("${ExitRequest.description}")
    private String exitDescription;

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

    @Bean
    @Scope("prototype")
    public WUTransaction countriesCurrencies() {
        WUTransaction cc = new WUTransaction();
        cc.setDasName(countryCurrencyDasName);
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
        cc.setDasName(usStateDasName);
        HashMap<String, String> dasFilters = new HashMap<>();
        dasFilters.put("queryfilter2", "");
        cc.setDasFilters(dasFilters);
        return cc;
    }

    @Bean
    @Scope("prototype")
    public WUTransaction mexicoCityState() {
        WUTransaction cc = new WUTransaction();
        cc.setDasName(mexicoCityStateDasName);
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

    @Bean
    @Scope("prototype")
    public DASServiceRequest usStateListRequest() {
        DASServiceRequest usStateListRequest = new DASServiceRequest();
        usStateListRequest.setRequestID(usStateSerialNum);
        usStateListRequest.setDescription(usStateDescription);
        usStateListRequest.setDasName(usStateDasName);
        usStateListRequest.setTxnData(usStateInfo());
        usStateListRequest.setDasService(getUSStateList());
        return usStateListRequest;
    }

    @Bean
    @Scope("prototype")
    public DASServiceRequest countryCurrencyListRequest() {
        DASServiceRequest countryCurrencyListRequest = new DASServiceRequest();
        countryCurrencyListRequest.setRequestID(countryCurrencySerialNum);
        countryCurrencyListRequest.setDescription(countryCurrencyDescription);
        countryCurrencyListRequest.setDasName(countryCurrencyDasName);
        countryCurrencyListRequest.setTxnData(countriesCurrencies());
        countryCurrencyListRequest.setDasService(getCountriesCurrencies());
        return countryCurrencyListRequest;
    }

    @Bean
    @Scope("prototype")
    public DASServiceRequest mexicoCityStateRequest() {
        DASServiceRequest mexicoCityStateRequest = new DASServiceRequest();
        mexicoCityStateRequest.setRequestID(mexicoCityStateSerialNum);
        mexicoCityStateRequest.setDescription(mexicoCityStateDescription);
        mexicoCityStateRequest.setDasName(mexicoCityStateDasName);
        mexicoCityStateRequest.setTxnData(mexicoCityState());
        mexicoCityStateRequest.setDasService(getMexicoCityState());
        return mexicoCityStateRequest;
    }

    @Bean
    @Scope("prototype")
    public PricingTableRequest pricingTableRequest() {
        PricingTableRequest pricingTableRequest = new PricingTableRequest();
        pricingTableRequest.setRequestID(pricingTableSerialNum);
        pricingTableRequest.setDescription(pricingTableDescription);
        return pricingTableRequest;
    }

    @Bean
    @Scope("prototype")
    public DeliveryServiceListRequest deliveryServiceListRequest() {
        DeliveryServiceListRequest deliveryServiceListRequest = new DeliveryServiceListRequest();
        deliveryServiceListRequest.setRequestID(deliveryServiceSerialNum);
        deliveryServiceListRequest.setDescription(deliveryServiceDescription);
        return deliveryServiceListRequest;
    }

    @Bean
    @Scope("prototype")
    public APNDataScopeRequest apnDataScopeRequest() {
        APNDataScopeRequest apnDataScopeRequest = new APNDataScopeRequest();
        apnDataScopeRequest.setRequestID(apnDataScopeSerialNum);
        apnDataScopeRequest.setDescription(apnDataScopeDescription);
        return apnDataScopeRequest;
    }

    @Bean
    @Scope("prototype")
    public ExitRequest exitRequest() {
        ExitRequest exitRequest = new ExitRequest();
        exitRequest.setRequestID(exitSerialNum);
        exitRequest.setDescription(exitDescription);
        return exitRequest;
    }

    @Bean
    @Scope("prototype")
    public HashMap<String, AbstractServiceRequest> requestMap(APNDynamicRequest apnDynamicRequest, GenerateMTCNRequest genMTCNRequest) {
        HashMap<String, AbstractServiceRequest> requestMap = new HashMap<>();
        requestMap.put(genMtcnSerialNum, genMTCNRequest);
        requestMap.put(countryCurrencySerialNum, countryCurrencyListRequest());
        requestMap.put(usStateSerialNum, usStateListRequest());
        requestMap.put(mexicoCityStateSerialNum, mexicoCityStateRequest());
        requestMap.put(apnDynamicRequest.getRequestID(), apnDynamicRequest);
        requestMap.put(pricingTableSerialNum, pricingTableRequest());
        requestMap.put(deliveryServiceSerialNum, deliveryServiceListRequest());
        requestMap.put(apnDataScopeSerialNum, apnDataScopeRequest());
        requestMap.put(exitSerialNum, exitRequest());
        return requestMap;
    }

    @Bean
    @Scope("singleton")
    public XMLAssemblerHandler AISAssemblerHandler() {
        XMLAssemblerHandler handler = new XMLAssemblerHandler();
        List<Resource> assemblers = new ArrayList<>();
        assemblers.add(new ClassPathResource("/properties/XMLAssembler-Send-AIS.properties"));
        assemblers.add(new ClassPathResource("/properties/XMLAssembler-CSCFunc-AIS.properties"));
        assemblers.add(new ClassPathResource("/properties/XMLAssembler-DAS.properties"));
        handler.setAssemberResources(assemblers);
        return handler;
    }

    @Bean
    @Scope("singleton")
    public XMLAssemblerHandler BISAssemblerHandler() {
        XMLAssemblerHandler handler = new XMLAssemblerHandler();
        List<Resource> assemblers = new ArrayList<>();
        assemblers.add(new ClassPathResource("/properties/XMLAssembler-Send-BIS.properties"));
        assemblers.add(new ClassPathResource("/properties/XMLAssembler-DAS.properties"));
        handler.setAssemberResources(assemblers);
        return handler;
    }

    @Bean
    @Scope("singleton")
    public WUGWRuntime WUGWRuntime(XMLAssemblerHandler AISAssemblerHandler,
            XMLAssemblerHandler BISAssemblerHandler, HashMap<String, AbstractServiceRequest> requestMap) {
        WUGWRuntime runtime = new WUGWRuntime();
        runtime.setRequestMap(requestMap);
        runtime.setAISAssemblerHandler(AISAssemblerHandler);
        runtime.setBISAssemblerHandler(BISAssemblerHandler);
        return runtime;
    }
}
