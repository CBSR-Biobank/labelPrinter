package edu.ualberta.med.biobank.barcodegenerator.template.configuration;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.graphics.Rectangle;

//TODO make Configuration Serializable
public class Configuration implements Serializable {
	protected HashMap<String, Rectangle> settings;


	public void setSettings(HashMap<String, Rectangle> settings) {
		this.settings = settings;
	}

	public HashMap<String, Rectangle> getSettings() {
		return this.settings;
	}

	public Rectangle getSettingsKey(String key) {
		return this.settings.get(key);
	}

}
