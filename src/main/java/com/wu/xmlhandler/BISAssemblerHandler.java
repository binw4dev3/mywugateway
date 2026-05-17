package com.wu.xmlhandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Composite handler for the BIS (Bank Interface System) gateway integration.
 *
 * Does not load any files directly. Instead it receives the two BIS-related
 * leaf handlers via constructor injection — Spring guarantees each leaf is
 * fully initialized (including @PostConstruct) before being injected here —
 * and merges their element/assembler maps into this bean's own maps.
 *
 * WUGWRuntime receives this bean as its BISAssemblerHandler.
 */
@Component("BISAssemblerHandler")
public class BISAssemblerHandler extends XMLAssemblerHandler {

    @Autowired
    public BISAssemblerHandler(
            @Qualifier("sendBISHandler") XMLAssemblerHandler sendBIS,
            @Qualifier("dasHandler") XMLAssemblerHandler das) {
        mergeFrom(sendBIS);
        mergeFrom(das);
    }
}
