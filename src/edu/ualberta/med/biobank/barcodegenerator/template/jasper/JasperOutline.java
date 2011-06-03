package edu.ualberta.med.biobank.barcodegenerator.template.jasper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import edu.ualberta.med.biobank.barcodegenerator.template.jasper.containers.BarcodeImage;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.containers.PatientInfo;

public class JasperOutline {

    private Branding branding;
    private PatientInfo patientInfo;
    private PatientBarcodeInformation patientBarcpdeInf;
    private InputStream jasperTemplateStream;

    public void setOutline(Branding bb, PatientInfo p,
        PatientBarcodeInformation bi, InputStream jasperStream) {
        branding = bb;
        patientInfo = p;
        patientBarcpdeInf = bi;
        jasperTemplateStream = jasperStream;
    }

    public PatientInfo getPatientInfo() {
        return patientInfo;
    }

    public PatientBarcodeInformation getPatientBarcpdeInf() {
        return patientBarcpdeInf;
    }

    public InputStream getJasperTemplateStream() {
        return jasperTemplateStream;
    }

    public Branding getBranding() {
        return branding;
    }

    public static class Branding {
        public String projectTitle;
        public ByteArrayInputStream logo; // png

        public Branding(String projectTitle, ByteArrayInputStream logo) {
            this.projectTitle = projectTitle;
            this.logo = logo;
        }
    };

    public static class PatientBarcodeInformation {
        private ArrayList<BarcodeImage> barcodeImageLayouts;

        public PatientBarcodeInformation() {
            barcodeImageLayouts = new ArrayList<BarcodeImage>();
        }

        public ArrayList<BarcodeImage> getLayout() {
            return this.barcodeImageLayouts;
        }
    }

}
