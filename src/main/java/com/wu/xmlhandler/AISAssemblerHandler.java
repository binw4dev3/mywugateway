package com.wu.xmlhandler;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Spring-managed XML assembler handler for the AIS (Account Interface System)
 * gateway integration.
 *
 * Resource paths are declared in application.properties under the key
 * AIS.assembler.resources (comma-separated list of classpath locations).
 * Spring injects them as a List<String>; each entry is wrapped in a
 * ClassPathResource and the files are loaded in @PostConstruct.
 */
@Component("AISAssemblerHandler")
public class AISAssemblerHandler extends XMLAssemblerHandler {

    @Value("${AIS.assembler.resources}")
    private List<String> resourcePaths;

    @PostConstruct
    public void init() {
        setAssemberResources(
            resourcePaths.stream()
                .map(ClassPathResource::new)
                .collect(Collectors.toList())
        );
        load();
    }
}
