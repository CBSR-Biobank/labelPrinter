package edu.ualberta.med.biobank.barcodegenerator.template.jasper;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import edu.ualberta.med.biobank.barcodegenerator.template.jasper.containers.BarcodeImage;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.containers.PatientInfo;


public abstract class JasperOutline {
	
	protected InputStream jasperTemplateStream;

	public static class Branding {
		public String projectTitle;
		public ByteArrayInputStream logo; //png
		
		public Branding(String projectTitle,ByteArrayInputStream logo){
			this.projectTitle = projectTitle;
			this.logo = logo;
		}
	};

	public static class PatientBarcodeInformation {
		private ArrayList<BarcodeImage> barcodeImageLayouts;
		public PatientBarcodeInformation(){
			barcodeImageLayouts = new ArrayList<BarcodeImage>();
		}
		public ArrayList<BarcodeImage> getLayout(){
			return this.barcodeImageLayouts;
		}
	}

	protected Branding branding;
	protected PatientInfo patientInfo;
	protected PatientBarcodeInformation patientBarcpdeInf;
	
	protected abstract void setOutline(Branding bb,PatientInfo p ,PatientBarcodeInformation bi) ;
	

}
