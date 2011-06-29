package edu.ualberta.med.biobank.barcodegenerator.template.configuration;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * Holds jasper maker and jasper file specific configuration data.
 * Configurations are loaded and edited in the configuration tree class.
 * 
 * Format of the key is string is alphanumeric text with a period seperator. For
 * instance, "Patient Info.Custom Field 3.Field Text".
 * 
 * @author Thomas Polasek 2011
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Configuration {

    private Map<String, Rectangle> settings = Collections
        .synchronizedMap(new LinkedHashMap<String, Rectangle>());

    public Map<String, Rectangle> getSettings() {
        return this.settings;
    }

    public Rectangle getSetting(String key) {
        return this.settings.get(key);
    }

    public void setSetting(String key, Rectangle value) {
        this.settings.put(key, value);
    }

}
