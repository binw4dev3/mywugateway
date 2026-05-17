package com.wu.console;

import com.wu.gw.model.WUTransaction;
import com.wu.gw.service.GetMexicoCityState;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class MexicoCityStateListRequest extends DASServiceRequest {

    @Value("${MexicoCityStateRequest.serialNum}")
    private String serialNum;

    @Value("${MexicoCityStateRequest.description}")
    private String descriptionValue;

    @Value("${MexicoCityStateRequest.dasName}")
    private String dasNameValue;

    @Autowired
    public MexicoCityStateListRequest(
            @Qualifier("mexicoCityState") WUTransaction txnData,
            GetMexicoCityState dasService) {
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
