package edu.ualberta.med.biobank.labelprinter.template.jasper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.labelprinter.template.jasper.containers.BarcodeImage;
import edu.ualberta.med.biobank.labelprinter.template.jasper.containers.PatientInfo;

/**
 * After gui data is obtained the jasper maker class with generate a jasper
 * outline. The jasper outline contains a list of elements for each field in the
 * jasper template.
 * 
 * Jasper filler uses information from this class to create the label sheet.
 * 
 * @author Thomas Polasek 2011
 * 
 */
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
        private List<BarcodeImage> barcodeImageLayouts;

        public PatientBarcodeInformation() {
            barcodeImageLayouts = new ArrayList<BarcodeImage>();
        }

        public List<BarcodeImage> getLayout() {
            return this.barcodeImageLayouts;
        }
    }

}
