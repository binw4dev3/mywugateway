package com.wu.gw.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wu.gw.model.PKJsonTxnObject;
import com.wu.gw.model.PKMTCNPushResponse;
import com.wu.api.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

//@Service
public class PKMTCNPushService {
    //@Autowired
    RestTemplate restTemplate = new RestTemplate();

    private static String PK_PUSH_URL = "https://coreyrevuatprxy.prod.wudip.com/cashpak/WWCEWebservicesMTO/api/remittance/sendTransactionCashMto";

    public ApiResponse<Object> pushMTCN(PKJsonTxnObject txnObject) {
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        ObjectMapper mapper = new ObjectMapper();
        String s = null;
        try {
            s = mapper.writeValueAsString(txnObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(s);
        try {
            HttpEntity<PKJsonTxnObject> httpEntity = new HttpEntity<>(txnObject, headers);
            ResponseEntity<Object> response =
                    restTemplate.postForEntity(PK_PUSH_URL, httpEntity, Object.class);
            Object resp = response.getBody();
            if(resp != null) {
                System.out.println("resp = " + resp);
                return ApiResponse.ok("MTCN Pushed.", resp);
            }
            return ApiResponse.error("Failed push MTCN : " + txnObject.getTransaction().getMgiTransactionId());
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
