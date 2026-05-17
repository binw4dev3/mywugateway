package com.wu.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.wu.api.service.MtcnService;
import com.wu.console.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
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
public class WUGWConfig implements EnvironmentAware {

        private Environment environment;

        @Override
        public void setEnvironment(Environment environment) {
                this.environment = environment;
        }

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
                cc.setDasName(environment.getProperty("CountryCurrencyListRequest.dasName"));
                HashMap<String, String> dasFilters = new HashMap<String, String>();
                dasFilters.put("queryfilter3", "");
                dasFilters.put("queryfilter4", "");
                cc.setDasFilters(dasFilters);
                return cc;
        }

        @Bean
        @Scope("prototype")
        public WUTransaction usStateInfo() {
                WUTransaction cc = new WUTransaction();
                cc.setDasName(environment.getProperty("USStateListRequest.dasName"));
                HashMap<String, String> dasFilters = new HashMap<String, String>();
                dasFilters.put("queryfilter2", "");
                cc.setDasFilters(dasFilters);
                return cc;
        }

        @Bean
        @Scope("prototype")
        public WUTransaction mexicoCityState() {
                WUTransaction cc = new WUTransaction();
                cc.setDasName(environment.getProperty("MexicoCityStateRequest.dasName"));
                HashMap<String, String> dasFilters = new HashMap<String, String>();
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
                HashMap<String, String> dasFilters = new HashMap<String, String>();
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
                HashMap<String, String> dasFilters = new HashMap<String, String>();
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
                HashMap<String, String> dasFilters = new HashMap<String, String>();
                dasFilters.put("queryfilter2", "");
                cc.setDasFilters(dasFilters);
                return cc;
        }

        @Bean
        @Scope("prototype")
        public GenerateMTCNRequest genMTCNRequest(MtcnService mtcnService) {
                GenerateMTCNRequest genMTCNRequest = new GenerateMTCNRequest();
                genMTCNRequest.setRequestID(environment.getProperty("GenMTCNRequest.serialNum"));
                genMTCNRequest.setDescription(environment.getProperty("GenMTCNRequest.description"));
                genMTCNRequest.setMtcnService(mtcnService);
                return genMTCNRequest;
        }

        @Bean
        @Scope("prototype")
        public DASServiceRequest usStateListRequest() {
                DASServiceRequest usStateListRequest = new DASServiceRequest();
                usStateListRequest.setRequestID(environment.getProperty("USStateListRequest.serialNum"));
                usStateListRequest.setDescription(environment.getProperty("USStateListRequest.description"));
                usStateListRequest.setDasName(environment.getProperty("USStateListRequest.dasName"));
                usStateListRequest.setTxnData(usStateInfo());
                usStateListRequest.setDasService(getUSStateList());
                return usStateListRequest;
        }

        @Bean
        @Scope("prototype")
        public DASServiceRequest countryCurrencyListRequest() {
                DASServiceRequest countryCurrencyListRequest = new DASServiceRequest();
                countryCurrencyListRequest.setRequestID(environment.getProperty("CountryCurrencyListRequest.serialNum"));
                countryCurrencyListRequest.setDescription(environment.getProperty("CountryCurrencyListRequest.description"));
                countryCurrencyListRequest.setDasName(environment.getProperty("CountryCurrencyListRequest.dasName"));
                countryCurrencyListRequest.setTxnData(countriesCurrencies());
                countryCurrencyListRequest.setDasService(getCountriesCurrencies());
                return countryCurrencyListRequest;
        }

        @Bean
        @Scope("prototype")
        public DASServiceRequest mexicoCityStateRequest() {
                DASServiceRequest mexicoCityStateRequest = new DASServiceRequest();
                mexicoCityStateRequest.setRequestID(environment.getProperty("MexicoCityStateRequest.serialNum"));
                mexicoCityStateRequest.setDescription(environment.getProperty("MexicoCityStateRequest.description"));
                mexicoCityStateRequest.setDasName(environment.getProperty("MexicoCityStateRequest.dasName"));
                mexicoCityStateRequest.setTxnData(mexicoCityState());
                mexicoCityStateRequest.setDasService(getMexicoCityState());
                return mexicoCityStateRequest;
        }

        /*@Bean
        @Scope("prototype")
        public APNDynamicRequest apnDynamicRequest() {
                APNDynamicRequest apnDynamicRequest = new APNDynamicRequest();
                apnDynamicRequest.setRequestID(environment.getProperty("APNDynamicRequest.serialNum"));
                apnDynamicRequest.setDescription(environment.getProperty("APNDynamicRequest.description"));
                return apnDynamicRequest;
        }*/

        @Bean
        @Scope("prototype")
        public PricingTableRequest pricingTableRequest() {
                PricingTableRequest pricingTableRequest = new PricingTableRequest();
                pricingTableRequest.setRequestID(environment.getProperty("PricingTableRequest.serialNum"));
                pricingTableRequest.setDescription(environment.getProperty("PricingTableRequest.description"));
                return pricingTableRequest;
        }

        @Bean
        @Scope("prototype")
        public DeliveryServiceListRequest deliveryServiceListRequest() {
                DeliveryServiceListRequest deliveryServiceListRequest = new DeliveryServiceListRequest();
                deliveryServiceListRequest.setRequestID(environment.getProperty("DeliveryServiceListRequest.serialNum"));
                deliveryServiceListRequest.setDescription(environment.getProperty("DeliveryServiceListRequest.description"));
                return deliveryServiceListRequest;
        }

        @Bean
        @Scope("prototype")
        public APNDataScopeRequest apnDataScopeRequest() {
                APNDataScopeRequest apnDataScopeRequest = new APNDataScopeRequest();
                apnDataScopeRequest.setRequestID(environment.getProperty("APNDataScopeRequest.serialNum"));
                apnDataScopeRequest.setDescription(environment.getProperty("APNDataScopeRequest.description"));
                return apnDataScopeRequest;
        }

        @Bean
        @Scope("prototype")
        public ExitRequest exitRequest() {
                ExitRequest exitRequest = new ExitRequest();
                exitRequest.setRequestID(environment.getProperty("ExitRequest.serialNum"));
                exitRequest.setDescription(environment.getProperty("ExitRequest.description"));
                return exitRequest;
        }

        @Bean
        @Scope("prototype")
        public HashMap<String, AbstractServiceRequest> requestMap(APNDynamicRequest apnDynamicRequest, MtcnService mtcnService) {
                HashMap<String, AbstractServiceRequest> requestMap = new HashMap<String, AbstractServiceRequest>();
                requestMap.put(environment.getProperty("GenMTCNRequest.serialNum"), genMTCNRequest(mtcnService));
                requestMap.put(environment.getProperty("CountryCurrencyListRequest.serialNum"), countryCurrencyListRequest());
                requestMap.put(environment.getProperty("USStateListRequest.serialNum"), usStateListRequest());
                requestMap.put(environment.getProperty("MexicoCityStateRequest.serialNum"), mexicoCityStateRequest());
                requestMap.put(environment.getProperty("APNDynamicRequest.serialNum"), apnDynamicRequest);
                requestMap.put(environment.getProperty("PricingTableRequest.serialNum"), pricingTableRequest());
                requestMap.put(environment.getProperty("DeliveryServiceListRequest.serialNum"), deliveryServiceListRequest());
                requestMap.put(environment.getProperty("APNDataScopeRequest.serialNum"), apnDataScopeRequest());
                requestMap.put(environment.getProperty("ExitRequest.serialNum"), exitRequest());
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
