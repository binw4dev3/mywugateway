package com.wu.xmlhandler;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Leaf handler that loads XMLAssembler-CSCFunc-AIS.properties.
 * Resource path is configured in application.properties under
 * AIS.cscfunc.assembler.resource.
 */
@Component("cscFuncAISHandler")
public class CSCFuncAISAssemblerHandler extends XMLAssemblerHandler {

    @Value("${AIS.cscfunc.assembler.resource}")
    private String resourcePath;

    @PostConstruct
    public void init() {
        loadFrom(resourcePath);
    }
}
