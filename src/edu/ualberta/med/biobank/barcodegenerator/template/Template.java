package edu.ualberta.med.biobank.barcodegenerator.template;

import java.io.InputStream;
import java.util.HashMap;

import org.eclipse.swt.graphics.Rectangle;

import edu.ualberta.med.biobank.barcodegenerator.template.configuration.Configuration;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.JasperOutline;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.JasperOutline.Branding;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.JasperOutline.PatientBarcodeInformation;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.containers.PatientInfo;

public abstract class Template extends JasperOutline {
	
	protected Configuration config;
	
	protected abstract void setJasperStream(InputStream jasperStream);
	
	protected abstract void setConfiguration(Configuration configuration) throws Exception;

}
