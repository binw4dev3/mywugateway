package com.wu.xmlhandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.springframework.core.io.Resource;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

/**
 * Base class that loads XML assembler property files and builds the
 * element/assembler maps consumed at runtime by WUGWRuntime.
 *
 * This class contains all loading and map-building logic.
 * Concrete subclasses (AISAssemblerHandler, BISAssemblerHandler) are the
 * Spring-managed @Component beans; they supply their own resource lists from
 * application.properties and call load() from @PostConstruct.
 */
public class XMLAssemblerHandler {

    private List<Resource> assemberResources;

    private HashMap<String, String> elements = new HashMap<>();

    private HashMap<String, AbstractXMLAssembler> assemblers = new HashMap<>();

    private PropertiesPersister persister = new DefaultPropertiesPersister();

    /**
     * Loads all configured assembler property files and populates the
     * elements and assemblers maps. Called by each concrete subclass from
     * its own @PostConstruct method, after assemberResources has been set.
     */
    protected void load() {
        if (assemberResources == null) {
            return;
        }
        try {
            Properties props = new Properties();
            for (Resource resource : assemberResources) {
                persister.load(props, resource.getInputStream());
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
