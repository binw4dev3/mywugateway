package com.wu.console;

import com.wu.gw.model.WUTransaction;
import com.wu.gw.service.GetCountriesCurrencies;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CountryCurrencyListRequest extends DASServiceRequest {

    @Value("${CountryCurrencyListRequest.serialNum}")
    private String serialNum;

    @Value("${CountryCurrencyListRequest.description}")
    private String descriptionValue;

    @Value("${CountryCurrencyListRequest.dasName}")
    private String dasNameValue;

    @Autowired
    public CountryCurrencyListRequest(
            @Qualifier("countriesCurrencies") WUTransaction txnData,
            GetCountriesCurrencies dasService) {
        super();
        setTxnData(txnData);
        setDasService(dasService);
    }

    @PostConstruct
    private void init() {
        setRequestID(serialNum);
        setDescription(descriptionValue);
        setDasName(dasNameValue);
    }
}
