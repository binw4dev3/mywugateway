package com.wu.xmlhandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

/**
 * Loads XML assembler property files and builds the element/assembler maps
 * used at runtime by WUGWRuntime.
 *
 * Previously extended PropertyPlaceholderConfigurer, which had the side effect
 * of taking over @Value placeholder resolution for the entire Spring context
 * (using only the assembler files as sources, not application.properties).
 *
 * Now a plain Spring-managed bean: resources are set by the @Bean factory
 * method in WUGWConfig, and @PostConstruct triggers the load once the bean
 * is fully initialized. Spring Boot's default PropertySourcesPlaceholderConfigurer
 * handles all @Value resolution from application.properties as normal.
 */
public class XMLAssemblerHandler {

    private List<Resource> assemberResources;

    private HashMap<String, String> elements = new HashMap<>();

    private HashMap<String, AbstractXMLAssembler> assemblers = new HashMap<>();

    private PropertiesPersister persister = new DefaultPropertiesPersister();

    /**
     * Loads all configured assembler property files and populates the
     * elements and assemblers maps. Called automatically by Spring after
     * the bean is fully constructed and its resources have been set.
     */
    @PostConstruct
    public void init() {
        if (assemberResources == null) {
            return;
        }
        try {
            Properties props = new Properties();
            for (Resource assemberResource : assemberResources) {
                persister.load(props, assemberResource.getInputStream());
                buildAssembler(props);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void buildAssembler(Properties props) {

        for (Object keyObj : props.keySet()) {

            String key = (String) keyObj;
            String prop = props.getProperty(key);

            if (key.endsWith(".assembler")) {

                if (!assemblers.containsKey(key)) {

                    try {
                        Class assemblerClass = Class.forName(prop);
                        AbstractXMLAssembler assembler = (AbstractXMLAssembler) assemblerClass.newInstance();
                        assemblers.put(key, assembler);

                    } catch (ClassNotFoundException ce) {
                        ce.printStackTrace();
                    } catch (IllegalAccessException ie) {
                        ie.printStackTrace();
                    } catch (InstantiationException ise) {
                        ise.printStackTrace();
                    }
                }

            } else {

                if (!elements.containsKey(key)) {
                    elements.put(key, prop);
                }
            }
        }
    }

    public List<Resource> getAssemberResources() {
        return assemberResources;
    }

    public void setAssemberResources(List<Resource> assemberResources) {
        this.assemberResources = assemberResources;
    }

    public HashMap<String, String> getElements() {
        return elements;
    }

    public void setElements(HashMap<String, String> elements) {
        this.elements = elements;
    }

    public HashMap<String, AbstractXMLAssembler> getAssemblers() {
        return assemblers;
    }

    public void setAssemblers(HashMap<String, AbstractXMLAssembler> assemblers) {
        this.assemblers = assemblers;
    }

    public PropertiesPersister getPersister() {
        return persister;
    }

    public void setPersister(PropertiesPersister persister) {
        this.persister = persister;
    }
}
