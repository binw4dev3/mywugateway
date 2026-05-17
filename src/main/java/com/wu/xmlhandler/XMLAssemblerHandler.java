package com.wu.xmlhandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

/**
 * Base class that holds the assembled element and assembler maps consumed at
 * runtime by WUGWRuntime.
 *
 * Two categories of subclass extend this:
 *
 *   Leaf handlers (one per property file) — call loadFrom(resourcePath) from
 *   their @PostConstruct to populate this class's maps from a single classpath
 *   resource.
 *
 *   Composite handlers (AISAssemblerHandler, BISAssemblerHandler) — receive
 *   the relevant leaf handlers via constructor injection and call mergeFrom()
 *   to aggregate their maps. They do not load any files directly.
 */
public class XMLAssemblerHandler {

    private HashMap<String, String> elements = new HashMap<>();

    private HashMap<String, AbstractXMLAssembler> assemblers = new HashMap<>();

    private PropertiesPersister persister = new DefaultPropertiesPersister();

    /**
     * Loads a single classpath property file and populates the elements and
     * assemblers maps. Called by each leaf-handler subclass from @PostConstruct.
     */
    protected void loadFrom(String resourcePath) {
        try {
            Properties props = new Properties();
            persister.load(props, new ClassPathResource(resourcePath).getInputStream());
            buildAssembler(props);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Copies all elements and assemblers from another handler into this one.
     * Called by composite-handler subclasses to aggregate leaf-handler maps.
     */
    protected void mergeFrom(XMLAssemblerHandler other) {
        elements.putAll(other.getElements());
        assemblers.putAll(other.getAssemblers());
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
