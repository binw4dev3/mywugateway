package com.wu.xmlhandler;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Leaf handler that loads XMLAssembler-Send-BIS.properties.
 * Resource path is configured in application.properties under
 * BIS.send.assembler.resource.
 */
@Component("sendBISHandler")
public class SendBISAssemblerHandler extends XMLAssemblerHandler {

    @Value("${BIS.send.assembler.resource}")
    private String resourcePath;

    @PostConstruct
    public void init() {
        loadFrom(resourcePath);
    }
}
