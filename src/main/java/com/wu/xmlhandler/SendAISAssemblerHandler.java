package com.wu.xmlhandler;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Leaf handler that loads XMLAssembler-Send-AIS.properties.
 * Resource path is configured in application.properties under
 * AIS.send.assembler.resource.
 */
@Component("sendAISHandler")
public class SendAISAssemblerHandler extends XMLAssemblerHandler {

    @Value("${AIS.send.assembler.resource}")
    private String resourcePath;

    @PostConstruct
    public void init() {
        loadFrom(resourcePath);
    }
}
