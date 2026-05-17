package com.wu.xmlhandler;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Leaf handler that loads XMLAssembler-DAS.properties.
 * This handler is shared: it is merged into both AISAssemblerHandler and
 * BISAssemblerHandler, so its resource is loaded exactly once.
 * Resource path is configured in application.properties under
 * DAS.assembler.resource.
 */
@Component("dasHandler")
public class DASAssemblerHandler extends XMLAssemblerHandler {

    @Value("${DAS.assembler.resource}")
    private String resourcePath;

    @PostConstruct
    public void init() {
        loadFrom(resourcePath);
    }
}
