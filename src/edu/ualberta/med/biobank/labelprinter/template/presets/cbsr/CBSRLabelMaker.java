package edu.ualberta.med.biobank.labelprinter.template.presets.cbsr;

import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.eclipse.osgi.util.NLS;

import edu.ualberta.med.biobank.labelprinter.template.Template;
import edu.ualberta.med.biobank.labelprinter.template.configuration.Configuration;
import edu.ualberta.med.biobank.labelprinter.template.configuration.Rectangle;
import edu.ualberta.med.biobank.labelprinter.template.jasper.JasperFiller;
import edu.ualberta.med.biobank.labelprinter.template.jasper.JasperOutline;
import edu.ualberta.med.biobank.labelprinter.template.jasper.containers.BarcodeImage;
import edu.ualberta.med.biobank.labelprinter.template.jasper.containers.PatientInfo;
import edu.ualberta.med.biobank.labelprinter.template.jasper.element.FieldGenerator;
import edu.ualberta.med.biobank.labelprinter.template.jasper.element.barcodes.Barcode1D;
import edu.ualberta.med.biobank.labelprinter.template.jasper.element.barcodes.Barcode2D;
import edu.ualberta.med.biobank.labelprinter.template.jasper.element.text.Text;
import edu.ualberta.med.biobank.labelprinter.template.jasper.exceptions.ElementCreationException;
import edu.ualberta.med.biobank.labelprinter.template.jasper.exceptions.JasperFillException;
import edu.ualberta.med.biobank.labelprinter.template.presets.cbsr.exceptions.CBSRPdfGenException;

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

    // per sheet
    private static final String SHEET_INFO_FIELD_1_TEXT = "Sheet Info.Custom Field 1.Text"; 
    private static final String SHEET_INFO_FIELD_1_BARCODE_1D = "Sheet Info.Custom Field 1.1D Barcode"; 

    private static final String SHEET_INFO_FIELD_2_TEXT = "Sheet Info.Custom Field 2.Text"; 
    private static final String SHEET_INFO_FIELD_2_BARCODE_1D = "Sheet Info.Custom Field 2.1D Barcode"; 

    private static final String SHEET_INFO_FIELD_3_TEXT = "Sheet Info.Custom Field 3.Text"; 
    private static final String SHEET_INFO_FIELD_3_BARCODE_1D = "Sheet Info.Custom Field 3.1D Barcode"; 

    private static final String SHEET_INFO_PATIENT_NUM_BARCODE_1D = "Sheet Info.Patient Number.1D Barcode"; 

    // per label general
    private static final String LABEL_GENERAL_FIELD_TEXT = "Labels.General.Text"; 
    private static final String LABEL_GENERAL_BARCODE_1D = "Labels.General.Barcode 1D"; 
    private static final String LABEL_GENERAL_BARCODE_2D = "Labels.General.Barcode 2D"; 
    private static final String LABEL_GENERAL_BARCODE_2D_TEXT = "Labels.General.Barcode 2D Text"; 

    // per label individual
    private static final String LABEL_INDIVIDUAL_BARCODE_1D_FORMATTED = "Labels.Individual.Label %03d.Barcode 1D"; 
    private static final String LABEL_INDIVIDUAL_BARCODE_2D_FORMATTED = "Labels.Individual.Label %03d.Barcode 2D"; 
    private static final String LABEL_INDIVIDUAL_BARCODE_2D_TEXT = "Labels.Individual.Label %03d.Barcode 2D Text"; 
    private static final String LABEL_INDIVIDUAL_FIELD_TEXT_FORMATTED = "Labels.Individual.Label %03d.Text"; 

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
                "Configuration data is invalid. Template is corrupt.");
        }

        JasperOutline jo = generateJasperOutline(cbsrData, barcodeStrings);

        byte[] pdfData = null;
        try {
            JasperFiller tm = new JasperFiller(jo);
            pdfData = tm.generatePdfData();
        } catch (JasperFillException e) {
            throw new CBSRPdfGenException(
                "Failed to fill configuration data into jasper template."
                    + "\n" + e.getError()); 
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
                "Failed to fill configuration data into jasper template for printing."
                    + "\n" + e.getError()); 
        }
    }

    private static JasperOutline generateJasperOutline(CBSRData cbsrData,
        List<String> barcodeStrings) throws CBSRPdfGenException {

        Template tplt = cbsrData.template;

        if (cbsrData.projectTileStr == null) {
            throw new CBSRPdfGenException(
                "Cannot have a null title");
        }

        if ((barcodeStrings == null) || (barcodeStrings.size() == 0)) {
            throw new CBSRPdfGenException(
                "Require a valid amount of barcode strings");
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
                    tplt.getKey(SHEET_INFO_FIELD_1_TEXT), cbsrData.label1Str,
                    cbsrData.value1Str, baseFont.deriveFont(23),
                    tplt.getKey(SHEET_INFO_FIELD_1_BARCODE_1D),
                    cbsrData.barcode1Print));

            patientInfo.getElements().addAll(
                FieldGenerator.generateElements(
                    tplt.getKey(SHEET_INFO_FIELD_2_TEXT), cbsrData.label2Str,
                    cbsrData.value2Str, baseFont.deriveFont(23),
                    tplt.getKey(SHEET_INFO_FIELD_2_BARCODE_1D),
                    cbsrData.barcode2Print));

            patientInfo.getElements().addAll(
                FieldGenerator.generateElements(
                    tplt.getKey(SHEET_INFO_FIELD_3_TEXT), cbsrData.label3Str,
                    cbsrData.value3Str, baseFont.deriveFont(23),
                    tplt.getKey(SHEET_INFO_FIELD_3_BARCODE_1D),
                    cbsrData.barcode3Print));

            patientInfo.getElements().add(
                new Barcode1D(tplt.getKey(SHEET_INFO_PATIENT_NUM_BARCODE_1D),
                    cbsrData.patientNumberStr, baseFont.deriveFont(22)));

        } catch (ElementCreationException eee) {
            throw new CBSRPdfGenException(NLS.bind(
                "Failed to create element in patient info box: {0}",
                eee.getError()));
        } catch (JAXBException ee) {
            throw new CBSRPdfGenException(NLS.bind(
                "Failed to load configuration setting: {0}", ee.getMessage()));
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

                    Rectangle master = tplt.getKey(LABEL_GENERAL_BARCODE_1D);
                    Rectangle barcode = tplt.getKey(String.format(
                        LABEL_INDIVIDUAL_BARCODE_1D_FORMATTED, i));

                    Rectangle r = new Rectangle(master.getX() + barcode.getX(),
                        master.getY() + barcode.getY(), master.getWidth()
                            + barcode.getWidth(), master.getHeight()
                            + barcode.getHeight());

                    Barcode1D item1D = new Barcode1D(r,
                        cbsrData.patientNumberStr, baseFont.deriveFont(22));
                    bi.getElements().add(item1D);
                } else {
                    throw new CBSRPdfGenException(
                        "Empty or null barcode string was specified.");
                }

                // 2d barcode;
                if ((rStrArray != null)
                    && (rStrArray.length() > 0)
                    && (rStrArray.replaceAll("[^a-zA-Z0-9 ]", "").length() == 12)) {  

                    Rectangle master = tplt.getKey(LABEL_GENERAL_BARCODE_2D);
                    Rectangle barcode = tplt.getKey(String.format(
                        LABEL_INDIVIDUAL_BARCODE_2D_FORMATTED, i));

                    Rectangle r = new Rectangle(master.getX() + barcode.getX(),
                        master.getY() + barcode.getY(), master.getWidth()
                            + barcode.getWidth(), master.getHeight()
                            + barcode.getHeight());

                    Barcode2D item2D = new Barcode2D(r, rStrArray);
                    bi.getElements().add(item2D);

                    // 2d barcode text:
                    if (cbsrData.printBarcode2DTextBoolean) {
                        Rectangle master2 = tplt
                            .getKey(LABEL_GENERAL_BARCODE_2D_TEXT);

                        Rectangle barcode2DTextRect = tplt.getKey(String
                            .format(LABEL_INDIVIDUAL_BARCODE_2D_TEXT, i));

                        Rectangle rectdim = new Rectangle(master2.getX()
                            + barcode2DTextRect.getX(), master2.getY()
                            + barcode2DTextRect.getY(), master2.getWidth()
                            + barcode2DTextRect.getWidth(), master2.getHeight()
                            + barcode2DTextRect.getHeight());

                        Text itemText = new Text(rectdim, rStrArray,
                            baseFont.deriveFont(22));

                        bi.getElements().add(itemText);
                    }

                } else {
                    throw new CBSRPdfGenException(
                        "Barcode ID must be a 12 character alphanumeric string.");
                }

                if ((cbsrData.specimenTypeStr != null)
                    && (cbsrData.specimenTypeStr.length() > 0)) {

                    Rectangle master = tplt.getKey(LABEL_GENERAL_FIELD_TEXT);

                    Rectangle barcode = tplt.getKey(String.format(
                        LABEL_INDIVIDUAL_FIELD_TEXT_FORMATTED, i));

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
                "Failed to create element in PatientBarcodeInformation box: {0}", e2.getError()));
        } catch (JAXBException e1) {
            throw new CBSRPdfGenException(NLS.bind(
                "Failed to load configuration setting: {0}",
                e1.getMessage()));
        }

        if (!tplt.jasperTemplateExists()) {
            throw new CBSRPdfGenException(
                "A valid jasper file is required.");
        }

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(tplt
                .getJasperTemplateXML().getBytes());

            JasperOutline jo = new JasperOutline();
            jo.setOutline(branding, patientInfo, pbi, inputStream);
            return jo;
        } catch (Exception e5) {
            throw new CBSRPdfGenException(NLS.bind(
                "Failed to create element in patient info box: {0}",
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

        config.setSetting(SHEET_INFO_FIELD_1_TEXT, new Rectangle(1, 4, 0, 0));
        config.setSetting(SHEET_INFO_FIELD_1_BARCODE_1D, new Rectangle(38, 1,
            29, 8));
        config.setSetting(SHEET_INFO_FIELD_2_TEXT, new Rectangle(1, 13, 0, 0));
        config.setSetting(SHEET_INFO_FIELD_2_BARCODE_1D, new Rectangle(38, 13,
            29, 8));
        config.setSetting(SHEET_INFO_FIELD_3_TEXT, new Rectangle(1, 25, 0, 0));
        config.setSetting(SHEET_INFO_FIELD_3_BARCODE_1D, new Rectangle(38, 25,
            29, 8));

        config.setSetting(LABEL_GENERAL_BARCODE_2D_TEXT, new Rectangle(32, 15,
            0, 0));

        config.setSetting(SHEET_INFO_PATIENT_NUM_BARCODE_1D, new Rectangle(1,
            33, 29, 8));
        config
            .setSetting(LABEL_GENERAL_BARCODE_1D, new Rectangle(11, 5, 29, 8));
        config.setSetting(LABEL_GENERAL_BARCODE_2D, new Rectangle(43, 5, 6, 6));
        config
            .setSetting(LABEL_GENERAL_FIELD_TEXT, new Rectangle(11, 17, 0, 0));

        for (int i = 1; i <= BARCODE_COUNT; i++) {
            config.setSetting(
                String.format(LABEL_INDIVIDUAL_BARCODE_1D_FORMATTED, i),
                new Rectangle(0, 0, 0, 0));
            config.setSetting(
                String.format(LABEL_INDIVIDUAL_BARCODE_2D_FORMATTED, i),
                new Rectangle(0, 0, 0, 0));
            config.setSetting(
                String.format(LABEL_INDIVIDUAL_BARCODE_2D_TEXT, i),
                new Rectangle(0, 0, 0, 0));
            config.setSetting(
                String.format(LABEL_INDIVIDUAL_FIELD_TEXT_FORMATTED, i),
                new Rectangle(0, 0, 0, 0));
        }
        return config;
    }

    // order does not matter for this.
    private static List<String> getConfigurationKeyList() {
        String[] configKeyList = new String[] { SHEET_INFO_FIELD_1_TEXT,
            SHEET_INFO_FIELD_1_BARCODE_1D, SHEET_INFO_FIELD_2_TEXT,
            SHEET_INFO_FIELD_2_BARCODE_1D, SHEET_INFO_FIELD_3_TEXT,
            SHEET_INFO_FIELD_3_BARCODE_1D, SHEET_INFO_PATIENT_NUM_BARCODE_1D,
            LABEL_GENERAL_BARCODE_1D, LABEL_GENERAL_BARCODE_2D,
            LABEL_GENERAL_BARCODE_2D_TEXT, LABEL_GENERAL_FIELD_TEXT };

        List<String> output = new ArrayList<String>();
        for (String ckl : configKeyList)
            output.add(ckl);

        for (int i = 1; i <= BARCODE_COUNT; i++) {
            output.add(String.format(LABEL_INDIVIDUAL_BARCODE_1D_FORMATTED, i));
            output.add(String.format(LABEL_INDIVIDUAL_BARCODE_2D_FORMATTED, i));
            output.add(String.format(LABEL_INDIVIDUAL_BARCODE_2D_TEXT, i));
            output.add(String.format(LABEL_INDIVIDUAL_FIELD_TEXT_FORMATTED, i));
        }
        return output;

    }

    private static boolean verifyConfiguration(Configuration c) {
        for (String k : c.keySet()) {
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
