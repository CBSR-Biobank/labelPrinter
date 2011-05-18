package edu.ualberta.med.biobank.barcodegenerator.template;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;

import org.eclipse.swt.graphics.Rectangle;

import edu.ualberta.med.biobank.barcodegenerator.template.configuration.Configuration;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.JasperOutline;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.JasperOutline.Branding;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.JasperOutline.PatientBarcodeInformation;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.containers.PatientInfo;

//TODO make template Serializable
public abstract class Template extends JasperOutline implements Serializable {
	
	protected Configuration config;
	
	protected byte[] jasperTemplateFileData;
	protected abstract void setJasperFileData(byte[] jasperData);
	
	protected abstract void setConfiguration(Configuration configuration) throws Exception;

}
