package edu.ualberta.med.biobank.barcodegenerator.template.configuration;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Configuration {

    protected Map<String, Rectangle> settings = new LinkedHashMap<String, Rectangle>();

    public void setSettings(Map<String, Rectangle> settings) {
        this.settings = settings;
    }

    public Map<String, Rectangle> getSettings() {
        return this.settings;
    }

    public Rectangle getSettingsKey(String key) {
        return this.settings.get(key);
    }

    public void setSettingsEntry(String key, Rectangle value) {
        this.settings.put(key, value);
    }

}
