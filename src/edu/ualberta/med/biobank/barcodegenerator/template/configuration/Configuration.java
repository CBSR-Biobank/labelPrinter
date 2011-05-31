package edu.ualberta.med.biobank.barcodegenerator.template.configuration;

import java.io.Serializable;
import java.util.LinkedHashMap;

import org.eclipse.swt.graphics.Rectangle;

public class Configuration implements Serializable {

	private static final long serialVersionUID = 2922691660660772442L;

	protected LinkedHashMap<String, Rectangle> settings = new LinkedHashMap<String, Rectangle>();

	public void setSettings(LinkedHashMap<String, Rectangle> settings) {
		this.settings = settings;
	}

	public LinkedHashMap<String, Rectangle> getSettings() {
		return this.settings;
	}

	public Rectangle getSettingsKey(String key) {
		return this.settings.get(key);
	}

	public void setSettingsEntry(String key, Rectangle value) {
		this.settings.put(key, value);
	}

}
