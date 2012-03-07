package edu.ualberta.med.biobank.labelprinter.template.configuration;

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

    // this updates any old key values and adds new ones where nessary.
    public boolean upgradeKeys() {
        Integer newVersion = ConfigurationUpdater.updateKeys(settings,
            version);

        // keys are up to date
        if ((newVersion == null) || newVersion.equals(version))
            return false;

        // keys were updated
        this.version = newVersion;
        return true;
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
