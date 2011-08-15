package edu.ualberta.med.biobank.barcodegenerator.template.configuration;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

    private Integer version;

    // this updates any old key values.
    // call this routine after loading the Configuration object from the
    // database.
    public boolean upgradeKeys() {

        final int NEWEST_VERSION_NUMBER = 2;
        // CHANGE THIS AND UPDATE ConfigurationTranslator each time you change
        // key names.

        if ((this.version == null) || (this.version < NEWEST_VERSION_NUMBER)) {

            Map<String, Rectangle> settingsUpgraded = new LinkedHashMap<String, Rectangle>();

            for (String key : this.settings.keySet()) {
                settingsUpgraded.put(
                    ConfigurationTranslator.translate(key, this.version),
                    this.settings.get(key));
            }

            this.settings.clear();
            for (String key : settingsUpgraded.keySet())
                this.settings.put(key, settingsUpgraded.get(key));

            this.version = NEWEST_VERSION_NUMBER;
            return true;
        }
        return false;
    }

    public boolean exists() {
        return (this.settings != null);
    }

    public Set<String> keySet() {
        return this.settings.keySet();
    }

    public Set<Entry<String, Rectangle>> entrySet() {
        return this.settings.entrySet();
    }

    public boolean containsKey(String key) {
        return this.settings.containsKey(key);
    }

    public Rectangle getSetting(String key) {
        return this.settings.get(key);
    }

    public void setSetting(String key, Rectangle value) {
        this.settings.put(key, value);
    }

}
