package edu.ualberta.med.biobank.barcodegenerator.template;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.graphics.Rectangle;

import edu.ualberta.med.biobank.barcodegenerator.template.configuration.Configuration;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.JasperOutline;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.JasperOutline.Branding;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.JasperOutline.PatientBarcodeInformation;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.containers.PatientInfo;

//TODO make template Serializable
public abstract class Template extends JasperOutline implements Serializable{
	
	private String name = "default";
	
	public static void Clone(Template original, Template clone){
		clone.name = original.name;
		
		clone.config = new Configuration();
		clone.config.setSettings(original.config.getSettings());
		
		if (original.jasperTemplateFileData != null) {
			clone.jasperTemplateFileData = new byte[original.jasperTemplateFileData.length];
			System.arraycopy(original.jasperTemplateFileData, 0,
					clone.jasperTemplateFileData, 0,
					original.jasperTemplateFileData.length);
		} else {
			clone.jasperTemplateFileData = null;
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	

	protected byte[] jasperTemplateFileData;
	protected abstract void setJasperFileData(byte[] jasperData);

	protected Configuration config;
	protected abstract Configuration getConfiguration();
	public abstract void setConfiguration(Configuration configuration) throws Exception;
	public abstract  ArrayList<String> getConfigurationKeyList();

}
