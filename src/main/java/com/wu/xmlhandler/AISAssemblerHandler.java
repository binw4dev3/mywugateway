package com.wu.xmlhandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Composite handler for the AIS (Account Interface System) gateway integration.
 *
 * Does not load any files directly. Instead it receives the three AIS-related
 * leaf handlers via constructor injection — Spring guarantees each leaf is
 * fully initialized (including @PostConstruct) before being injected here —
 * and merges their element/assembler maps into this bean's own maps.
 *
 * WUGWRuntime receives this bean as its AISAssemblerHandler.
 */
@Component("AISAssemblerHandler")
public class AISAssemblerHandler extends XMLAssemblerHandler {

    @Autowired
    public AISAssemblerHandler(
            @Qualifier("sendAISHandler") XMLAssemblerHandler sendAIS,
            @Qualifier("cscFuncAISHandler") XMLAssemblerHandler cscFuncAIS,
            @Qualifier("dasHandler") XMLAssemblerHandler das) {
        mergeFrom(sendAIS);
        mergeFrom(cscFuncAIS);
        mergeFrom(das);
    }
}
