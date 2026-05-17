package com.wu.console;

import com.wu.gw.model.WUTransaction;
import com.wu.gw.service.GetUSStateList;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class USStateListRequest extends DASServiceRequest {

    @Value("${USStateListRequest.serialNum}")
    private String serialNum;

    @Value("${USStateListRequest.description}")
    private String descriptionValue;

    @Value("${USStateListRequest.dasName}")
    private String dasNameValue;

    @Autowired
    public USStateListRequest(
            @Qualifier("usStateInfo") WUTransaction txnData,
            GetUSStateList dasService) {
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
