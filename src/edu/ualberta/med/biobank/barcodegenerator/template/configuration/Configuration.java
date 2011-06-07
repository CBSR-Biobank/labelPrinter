package edu.ualberta.med.biobank.barcodegenerator.template.configuration;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Configuration {

    private Map<String, Rectangle> settings = new HashMap<String, Rectangle>();

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
