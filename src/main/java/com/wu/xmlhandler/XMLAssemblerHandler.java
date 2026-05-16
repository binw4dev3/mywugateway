package com.wu.xmlhandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

public class XMLAssemblerHandler extends PropertyPlaceholderConfigurer {

	private List<Resource> assemberResources;
	
	private HashMap<String, String> elements = new HashMap<String, String>();
	
	private HashMap<String, AbstractXMLAssembler> assemblers = new HashMap<String, AbstractXMLAssembler>();

	private PropertiesPersister persister = new DefaultPropertiesPersister();

	@Override
	protected void loadProperties(Properties props) throws IOException {
		
		//super.loadProperties(props);
		
		if(assemberResources != null) {
			
			try {
				for(Resource assemberResource : assemberResources) {
					persister.load(props, assemberResource.getInputStream());
					buildAssembler(props);
				}
				
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	private void buildAssembler(Properties props) {
		
		for(Object keyObj : props.keySet()) {
			
			String key = (String)keyObj;			
			String prop = props.getProperty(key);
			
			if(key.endsWith(".assembler")) {
				
				if(!assemblers.containsKey(key)) {
					
					try {
						
						Class assemblerClass = Class.forName(prop);
						AbstractXMLAssembler assembler = (AbstractXMLAssembler)assemblerClass.newInstance();
						
						assemblers.put(key, assembler);
					
					} catch(ClassNotFoundException ce) {
						
						ce.printStackTrace();
						
					} catch(IllegalAccessException ie) {
						
						ie.printStackTrace();
						
					} catch(InstantiationException ise) {
						
						ise.printStackTrace();
					}
				}
				
			} else {
				
				if(!elements.containsKey(key)) {
					
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
