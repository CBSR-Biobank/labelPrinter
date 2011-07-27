package edu.ualberta.med.biobank.barcodegenerator.template.presets.cbsr;

import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.eclipse.osgi.util.NLS;

import edu.ualberta.med.biobank.barcodegenerator.template.Template;
import edu.ualberta.med.biobank.barcodegenerator.template.configuration.Configuration;
import edu.ualberta.med.biobank.barcodegenerator.template.configuration.Rectangle;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.JasperFiller;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.JasperOutline;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.containers.BarcodeImage;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.containers.PatientInfo;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.FieldGenerator;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.barcodes.Barcode1D;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.barcodes.Barcode2D;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.text.Text;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.exceptions.ElementCreationException;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.exceptions.JasperFillException;
import edu.ualberta.med.biobank.barcodegenerator.template.presets.cbsr.exceptions.CBSRPdfGenException;

/**
 * 
 * Used for setting up the default configuration key-value settings. Creating a
 * jasper outline from a specific jasper-file layout. This class also has
 * supporting functions to create a pdf and print to printer. Essientially it
 * provides the relationship between the gui and the jasper filler.
 * 
 * 
 * @author Thomas Polasek 2011
 * 
 */
public class CBSRLabelMaker {

    private static final int BARCODE_COUNT = 32;

    /**
     * Generates a jasper outline and creates a pdf file byte array.
     * 
     * @param cbsrData
     * @param barcodeStrings
     * @return
     * @throws CBSRPdfGenException
     * @throws JAXBException
     */
    public static byte[] generatePdfCBSR(CBSRData cbsrData,
        List<String> barcodeStrings) throws CBSRPdfGenException, JAXBException {

        Configuration configDataStr = cbsrData.template.getConfiguration();

        if (!verifyConfiguration(configDataStr)) {
            throw new CBSRPdfGenException(
                Messages.CBSRLabelMaker_config_data_invalid_error);
        }

        JasperOutline jo = generateJasperOutline(cbsrData, barcodeStrings);

        byte[] pdfData = null;
        try {
            JasperFiller tm = new JasperFiller(jo);
            pdfData = tm.generatePdfData();
        } catch (JasperFillException e) {
            throw new CBSRPdfGenException(
                Messages.CBSRLabelMaker_fill_config_failed
                    + "\n" + e.getError()); //$NON-NLS-1$
        }
        return pdfData;
    }

    /**
     * Generates a jasper outline and prints to a specified printer.
     * 
     * @param cbsrData
     * @param barcodeStrings
     * @throws CBSRPdfGenException
     */
    public static void printLabelsCBSR(CBSRData cbsrData,
        List<String> barcodeStrings) throws CBSRPdfGenException {

        JasperOutline jo = generateJasperOutline(cbsrData, barcodeStrings);

        try {
            JasperFiller tm = new JasperFiller(jo);
            tm.printJasperToPrinter(cbsrData.printerNameStr);
        } catch (JasperFillException e) {

            throw new CBSRPdfGenException(
                Messages.CBSRLabelMaker_fill_config_print_failed
                    + "\n" + e.getError()); //$NON-NLS-1$
        }
    }

    private static JasperOutline generateJasperOutline(CBSRData cbsrData,
        List<String> barcodeStrings) throws CBSRPdfGenException {

        Template tplt = cbsrData.template;

        if (cbsrData.projectTileStr == null) {
            throw new CBSRPdfGenException(
                Messages.CBSRLabelMaker_null_title_error);
        }

        if ((barcodeStrings == null) || (barcodeStrings.size() == 0)) {
            throw new CBSRPdfGenException(
                Messages.CBSRLabelMaker_barcodes_size_error);
        }

        // -------branding------------a
        JasperOutline.Branding branding = new JasperOutline.Branding(
            cbsrData.projectTileStr, cbsrData.logoStream);

        Font baseFont = new Font(cbsrData.fontName, Font.PLAIN, 22);

        // Point position, Dimension size, String message,String label, String
        // value, Font font, boolean printBarcode
        // -------patient info------------
        PatientInfo patientInfo = new PatientInfo();

        try {
            patientInfo.getElements().addAll(
                FieldGenerator.generateElements(
                    tplt.getKey("Patient Info.Custom Field 1.Field Text"), //$NON-NLS-1$
                    cbsrData.label1Str, cbsrData.value1Str,
                    baseFont.deriveFont(23),
                    tplt.getKey("Patient Info.Custom Field 1.1D Barcode"), //$NON-NLS-1$
                    cbsrData.barcode1Print));

            patientInfo.getElements().addAll(
                FieldGenerator.generateElements(
                    tplt.getKey("Patient Info.Custom Field 2.Field Text"), //$NON-NLS-1$
                    cbsrData.label2Str, cbsrData.value2Str,
                    baseFont.deriveFont(23),
                    tplt.getKey("Patient Info.Custom Field 2.1D Barcode"), //$NON-NLS-1$
                    cbsrData.barcode2Print));

            patientInfo.getElements().addAll(
                FieldGenerator.generateElements(
                    tplt.getKey("Patient Info.Custom Field 3.Field Text"), //$NON-NLS-1$
                    cbsrData.label3Str, cbsrData.value3Str,
                    baseFont.deriveFont(23),
                    tplt.getKey("Patient Info.Custom Field 3.1D Barcode"), //$NON-NLS-1$
                    cbsrData.barcode3Print));

            patientInfo.getElements().add(
                new Barcode1D(
                    tplt.getKey("Patient Info.Patient ID.1D Barcode"), //$NON-NLS-1$
                    cbsrData.patientNumberStr, baseFont.deriveFont(22)));

        } catch (ElementCreationException eee) {
            throw new CBSRPdfGenException(NLS.bind(
                Messages.CBSRLabelMaker_patient_elt_create_error,
                eee.getError()));
        } catch (JAXBException ee) {
            throw new CBSRPdfGenException(NLS.bind(
                Messages.CBSRLabelMaker_load_config_error, ee.getMessage()));
        }
        // -------barcode info------------
        JasperOutline.PatientBarcodeInformation pbi = new JasperOutline.PatientBarcodeInformation();

        try {
            int i = 0;
            for (String rStrArray : barcodeStrings) {

                BarcodeImage bi = new BarcodeImage();

                i++;
                // 1d barcode
                if ((cbsrData.patientNumberStr != null)
                    && (cbsrData.patientNumberStr.length() > 0)) {

                    Rectangle master = tplt
                        .getKey("Barcodes.General.Barcode 1D"); //$NON-NLS-1$
                    Rectangle barcode = tplt.getKey(String.format(
                        "Barcodes.Individual.Barcode %03d.Barcode 1D", i)); //$NON-NLS-1$

                    Rectangle r = new Rectangle(master.getX() + barcode.getX(),
                        master.getY() + barcode.getY(), master.getWidth()
                            + barcode.getWidth(), master.getHeight()
                            + barcode.getHeight());

                    Barcode1D item1D = new Barcode1D(r,
                        cbsrData.patientNumberStr, baseFont.deriveFont(22));
                    bi.getElements().add(item1D);
                } else {
                    throw new CBSRPdfGenException(
                        Messages.CBSRLabelMaker_empty_barcode_error);
                }

                // 2d barcode;
                if ((rStrArray != null)
                    && (rStrArray.length() > 0)
                    && (rStrArray.replaceAll("[^a-zA-Z0-9 ]", "").length() == 12)) { //$NON-NLS-1$ //$NON-NLS-2$

                    Rectangle master = tplt
                        .getKey("Barcodes.General.Barcode 2D"); //$NON-NLS-1$
                    Rectangle barcode = tplt.getKey(String.format(
                        "Barcodes.Individual.Barcode %03d.Barcode 2D", i)); //$NON-NLS-1$

                    Rectangle r = new Rectangle(master.getX() + barcode.getX(),
                        master.getY() + barcode.getY(), master.getWidth()
                            + barcode.getWidth(), master.getHeight()
                            + barcode.getHeight());

                    Barcode2D item2D = new Barcode2D(r, rStrArray);
                    bi.getElements().add(item2D);
                } else {
                    throw new CBSRPdfGenException(
                        Messages.CBSRLabelMaker_barcode_characters_error);
                }

                if ((cbsrData.specimenTypeStr != null)
                    && (cbsrData.specimenTypeStr.length() > 0)) {

                    Rectangle master = tplt
                        .getKey("Barcodes.General.Specimen Text"); //$NON-NLS-1$

                    Rectangle barcode = tplt.getKey(String.format(
                        "Barcodes.Individual.Barcode %03d.Specimen Text", i)); //$NON-NLS-1$

                    Rectangle rectdim = new Rectangle(master.getX()
                        + barcode.getX(), master.getY() + barcode.getY(),
                        master.getWidth() + barcode.getWidth(),
                        master.getHeight() + barcode.getHeight());

                    Text itemText = new Text(rectdim, cbsrData.specimenTypeStr,
                        baseFont.deriveFont(22));
                    bi.getElements().add(itemText);
                }
                pbi.getLayout().add(bi);
            }
        } catch (ElementCreationException e2) {
            throw new CBSRPdfGenException(NLS.bind(
                Messages.CBSRLabelMaker_info_box_create_error, e2.getError()));
        } catch (JAXBException e1) {
            throw new CBSRPdfGenException(NLS.bind(
                Messages.CBSRLabelMaker_load_config_settings_error,
                e1.getMessage()));
        }

        if (!tplt.jasperTemplateExists()) {
            throw new CBSRPdfGenException(
                Messages.CBSRLabelMaker_valid_jasper_error);
        }

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(tplt
                .getJasperTemplateXML().getBytes());

            JasperOutline jo = new JasperOutline();
            jo.setOutline(branding, patientInfo, pbi, inputStream);
            return jo;
        } catch (Exception e5) {
            throw new CBSRPdfGenException(NLS.bind(
                Messages.CBSRLabelMaker_patient_info_box_create_error,
                e5.getMessage()));
        }
    }

    /**
     * 
     * Set of default configurations. This class is required to be called upon
     * the creation of a new jasper template. This configuration class uses
     * these key values to plot the appropiate tree for editing configuration
     * settings. Please be cautious when changing any key value names. Ensure
     * that all references to these key values are updated.
     * 
     * @return
     */
    public static Configuration getDefaultConfiguration() {
        Configuration config = new Configuration();

        config.setSetting("Patient Info.Custom Field 1.Field Text", //$NON-NLS-1$
            new Rectangle(1, 4, 0, 0));
        config.setSetting("Patient Info.Custom Field 1.1D Barcode", //$NON-NLS-1$
            new Rectangle(38, 1, 29, 8));
        config.setSetting("Patient Info.Custom Field 2.Field Text", //$NON-NLS-1$
            new Rectangle(1, 13, 0, 0));
        config.setSetting("Patient Info.Custom Field 2.1D Barcode", //$NON-NLS-1$
            new Rectangle(38, 13, 29, 8));
        config.setSetting("Patient Info.Custom Field 3.Field Text", //$NON-NLS-1$
            new Rectangle(1, 25, 0, 0));
        config.setSetting("Patient Info.Custom Field 3.1D Barcode", //$NON-NLS-1$
            new Rectangle(38, 25, 29, 8));
        config.setSetting("Patient Info.Patient ID.1D Barcode", new Rectangle( //$NON-NLS-1$
            1, 33, 29, 8));
        config.setSetting("Barcodes.General.Barcode 1D", new Rectangle(11, 5, //$NON-NLS-1$
            29, 8));
        config.setSetting("Barcodes.General.Barcode 2D", new Rectangle(43, 5, //$NON-NLS-1$
            6, 6));
        config.setSetting("Barcodes.General.Specimen Text", new Rectangle(11, //$NON-NLS-1$
            17, 0, 0));

        for (int i = 1; i <= BARCODE_COUNT; i++) {
            config
                .setSetting(String.format(
                    "Barcodes.Individual.Barcode %03d.Barcode 1D", i), //$NON-NLS-1$
                    new Rectangle(0, 0, 0, 0));
            config
                .setSetting(String.format(
                    "Barcodes.Individual.Barcode %03d.Barcode 2D", i), //$NON-NLS-1$
                    new Rectangle(0, 0, 0, 0));
            config.setSetting(String.format(
                "Barcodes.Individual.Barcode %03d.Specimen Text", i), //$NON-NLS-1$
                new Rectangle(0, 0, 0, 0));
        }
        return config;
    }

    // order does not matter for this.
    private static List<String> getConfigurationKeyList() {
        String[] configKeyList = new String[] {
            "Patient Info.Custom Field 1.Field Text", //$NON-NLS-1$
            "Patient Info.Custom Field 1.1D Barcode", //$NON-NLS-1$
            "Patient Info.Custom Field 2.Field Text", //$NON-NLS-1$
            "Patient Info.Custom Field 2.1D Barcode", //$NON-NLS-1$
            "Patient Info.Custom Field 3.Field Text", //$NON-NLS-1$
            "Patient Info.Custom Field 3.1D Barcode", //$NON-NLS-1$
            "Patient Info.Patient ID.1D Barcode", //$NON-NLS-1$
            "Barcodes.General.Barcode 1D", "Barcodes.General.Barcode 2D", //$NON-NLS-1$ //$NON-NLS-2$
            "Barcodes.General.Specimen Text" }; //$NON-NLS-1$

        List<String> output = new ArrayList<String>();
        for (String ckl : configKeyList)
            output.add(ckl);

        for (int i = 1; i <= BARCODE_COUNT; i++) {
            output.add(String.format(
                "Barcodes.Individual.Barcode %03d.Barcode 1D", i)); //$NON-NLS-1$
            output.add(String.format(
                "Barcodes.Individual.Barcode %03d.Barcode 2D", i)); //$NON-NLS-1$
            output.add(String.format(
                "Barcodes.Individual.Barcode %03d.Specimen Text", i)); //$NON-NLS-1$
        }
        return output;

    }

    private static boolean verifyConfiguration(Configuration c) {
        for (String k : c.getSettings().keySet()) {
            boolean found = false;

            for (String ckl : getConfigurationKeyList()) {
                if (k.equals(ckl)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

}
