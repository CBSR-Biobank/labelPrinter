package edu.ualberta.med.biobank.barcodegenerator.template;

import java.io.Serializable;
import java.util.ArrayList;
import edu.ualberta.med.biobank.barcodegenerator.template.configuration.Configuration;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.JasperOutline;

public abstract class Template extends JasperOutline implements Serializable{

	private static final long serialVersionUID = -4213741888020425604L;
	
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
	

	protected byte[] jasperTemplateFileData = null;
	protected abstract void setJasperFileData(byte[] jasperData);
	protected abstract boolean jasperFileDataExists();

	protected Configuration config = null;
	protected abstract Configuration getConfiguration();
	public abstract void setConfiguration(Configuration configuration) throws Exception;
	public abstract  ArrayList<String> getConfigurationKeyList();

}
