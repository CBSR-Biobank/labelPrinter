package edu.ualberta.med.biobank.labelprinter.template.jasper;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PrintQuality;

import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.eclipse.osgi.util.NLS;

import edu.ualberta.med.biobank.labelprinter.template.jasper.containers.BarcodeImage;
import edu.ualberta.med.biobank.labelprinter.template.jasper.element.Element;
import edu.ualberta.med.biobank.labelprinter.template.jasper.exceptions.BarcodeCreationException;
import edu.ualberta.med.biobank.labelprinter.template.jasper.exceptions.JasperFillException;

/**
 * 
 * Generates a jasper print file that is used to make pdfs and print to
 * printers. It parses the jasper xml file and loads constants such as image
 * sizes into the JasperConstants class. These are referenced and checked
 * against the jasper outline that is provided to the jasper filler. Any
 * inconsistencies such as missing barcodes will result in exceptions being
 * thrown.
 * 
 * @author Thomas Polasek 2011
 * 
 */
@SuppressWarnings("nls")
public class JasperFiller {

    private JasperOutline templateData;

    JasperConstants jasperConstants = new JasperConstants();

    /**
     * Holds jasper file specific constants. These are checked against the
     * supplied jasper outline class.
     * 
     * @author Thomas Polasek 2011
     *
     */
    private class JasperConstants {
        public static final String TITLE_FIELD = "PROJECT_TITLE";
        public static final String LOGO_FIELD = "LOGO";
        public static final String PATIENT_IMAGE_FIELD = "PATIENT_INFO_IMG";
        public static final String PATIENT_BARCODE_BASE = "PATIENT_BARCODE_";

        public int barcodeCount = 0;
        public int barcodeImageWidth = 0;
        public int barcodeImageHeight = 0;

        public int patientImageWidth = 0;
        public int patientImageHeight = 0;
    }

    /**
     * 
     * Jasper file is parsed in the constructor of the jasper filler class.
     * 
     * @param req
     * @throws JasperFillException
     */
    public JasperFiller(JasperOutline req) {

        if (req == null)
            throw new IllegalArgumentException(
                "Null request for jasper filler.");

        this.templateData = req;
    }

    /**
     * Constants are loaded from the jasper file.
     * 
     * @throws JasperFillException
     */
    private void loadTemplateConstants() throws JasperFillException {

        try {
            templateData.getJasperTemplateStream().reset();
        } catch (IOException e) {
            throw new JasperFillException(MessageFormat.format(
                "Failed to reset template data stream : {0}", e.getMessage()));
        }
        JasperDesign jasperSubDesign;
        try {
            jasperSubDesign = JRXmlLoader.load(templateData
                .getJasperTemplateStream());
        } catch (JRException e) {
            throw new JasperFillException(NLS.bind(
                "Failed to load jasper design: {0}", e.getMessage()));
        }

        JRElement patientImg = jasperSubDesign.getTitle().getElementByKey(
            JasperConstants.PATIENT_IMAGE_FIELD);
        if ((jasperSubDesign.getTitle() != null) && (patientImg != null)) {
            jasperConstants.patientImageWidth = patientImg.getWidth();
            jasperConstants.patientImageHeight = patientImg.getHeight();
        } else {
            throw new JasperFillException(
                "Failed to patient image dimensions from the jasper report.");
        }

        if ((jasperSubDesign.getPageFooter() != null)
            && (jasperSubDesign.getPageFooter().getElements() != null)
            && (jasperSubDesign.getPageFooter().getElements().length > 0)) {
            jasperConstants.barcodeImageWidth = jasperSubDesign.getPageFooter()
                .getElements()[0].getWidth();
            jasperConstants.barcodeImageHeight = jasperSubDesign
                .getPageFooter().getElements()[0].getHeight();
            jasperConstants.barcodeCount = jasperSubDesign.getPageFooter()
                .getElements().length;

            for (JRElement jr : jasperSubDesign.getPageFooter().getElements()) {
                if ((jr.getWidth() != jasperConstants.barcodeImageWidth)
                    || (jr.getHeight() != jasperConstants.barcodeImageHeight)) {
                    throw new JasperFillException(
                        "All barcode image fields must be of equal size.");
                }
            }
        } else {
            throw new JasperFillException(
                "Failed to barcode image dimensions from the jasper report.");
        }

        if (templateData.getPatientBarcpdeInf().getLayout().size() != jasperConstants.barcodeCount) {
            throw new JasperFillException(
                NLS.bind(
                    "Error: jasper file contains {0} barcode IDs. Configuration data is designed for: {1} barcode IDs.",
                    jasperConstants.barcodeCount, templateData
                        .getPatientBarcpdeInf().getLayout().size()));
        }

    }

    /**
     * This prints a JasperPrint class to the sepecified printer name.
     * 
     * @param printerName is a lookupPrintServices printer name.
     * @throws JasperFillException
     */
    public void printJasperToPrinter(String printerName)
        throws JasperFillException {
        if (printerName == null) {
            throw new IllegalArgumentException(
                "Error: No printer was selected!");
        }

        loadTemplateConstants();

        PrinterJob job = PrinterJob.getPrinterJob();
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null,
            null);

        int selectedService = -1;
        for (int i = 0; i < services.length; i++) {
            if (services[i].getName().equals(printerName)) {
                selectedService = i;
                break;
            }
        }
        if (selectedService < 0) {
            throw new JasperFillException("Failed to find selected printer.");
        }

        try {
            job.setPrintService(services[selectedService]);
        } catch (PrinterException e1) {
            throw new JasperFillException(NLS.bind(
                "Failed to set print service: {0}", e1.getMessage()));
        }

        JasperPrint print = generateJasperPint();

        PrintRequestAttributeSet printRequestAttributeSet =
            new HashPrintRequestAttributeSet();
        printRequestAttributeSet.add(new Copies(1));
        printRequestAttributeSet.add(PrintQuality.HIGH);
        printRequestAttributeSet.add(MediaSizeName.NA_LETTER);
        printRequestAttributeSet.add(new MediaPrintableArea(0, 0, print
            .getPageWidth() / 72f, print.getPageHeight() / 72f,
            MediaPrintableArea.INCH));

        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE,
            services[selectedService]);
        exporter.setParameter(
            JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET,
            printRequestAttributeSet);
        exporter.setParameter(
            JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
        exporter
            .setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG,
                Boolean.FALSE);

        try {
            exporter.exportReport();
        } catch (JRException e) {
            throw new JasperFillException(NLS.bind("Failed to print: {0}",
                e.getMessage()));
        }
    }

    /**
     * Converts a jasper print into a pdf file.
     * 
     * @return
     * @throws JasperFillException
     */
    public byte[] generatePdfData() throws JasperFillException {

        byte[] reportPdfBtyes = null;

        loadTemplateConstants();
        JasperPrint jp = generateJasperPint();
        try {
            reportPdfBtyes = JasperExportManager.exportReportToPdf(jp);
        } catch (JRException e) {

            throw new JasperFillException(NLS.bind(
                "Jasper failed to create pdf. Reason : {0}", e.getMessage()));
        }
        return reportPdfBtyes;
    }

    /**
     * Creates a jasperprint from a jasper outline and the specified jasper
     * file. Elements are rendered here. All images generated are passed to
     * jasper as a png image input stream.
     * 
     * @return
     * @throws JasperFillException
     */
    private JasperPrint generateJasperPint() throws JasperFillException {
        ByteArrayInputStream patientInfoImg;
        List<ByteArrayInputStream> barcodeIDBufferList =
            new ArrayList<ByteArrayInputStream>();

        // place patient image.
        try {
            patientInfoImg = drawElementsToPngStream(templateData
                .getPatientInfo().getElements(),
                jasperConstants.patientImageWidth,
                jasperConstants.patientImageHeight);
        } catch (IOException e) {
            throw new JasperFillException(
                "Failed to draw patientInfoImg to image buffer"
                    + e.getMessage());
        } catch (BarcodeCreationException e) {
            throw new JasperFillException(NLS.bind(
                "Failed to create barcode patientInfoImg : {0}", e.getError()));
        }
        // place patient barcode images
        try {
            for (BarcodeImage bi : templateData.getPatientBarcpdeInf()
                .getLayout()) {
                barcodeIDBufferList.add(drawElementsToPngStream(
                    bi.getElements(), jasperConstants.barcodeImageWidth,
                    jasperConstants.barcodeImageHeight));
            }
        } catch (IOException e) {
            throw new JasperFillException(NLS.bind(
                "Failed to draw barcodeinfo to image buffer: {0}",
                e.getMessage()));
        } catch (BarcodeCreationException e) {
            throw new JasperFillException(NLS.bind(
                "Failed to create barcode barcodeinfo: {0}", e.getError()));
        }

        // generate parameters for jasper
        Map<String, Object> parameters = generateParameters(patientInfoImg,
            barcodeIDBufferList);

        try {
            // generate jasper report from template
            templateData.getJasperTemplateStream().reset();
            JasperReport jasperReport = JasperCompileManager
                .compileReport(templateData.getJasperTemplateStream());
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport, parameters, new JREmptyDataSource());

            jasperPrint.setBottomMargin(0);
            jasperPrint.setTopMargin(0);
            jasperPrint.setLeftMargin(0);
            jasperPrint.setRightMargin(0);

            return jasperPrint;
        } catch (JRException e) {
            throw new JasperFillException(NLS.bind(
                "Jasper failed to create pdf. Reason: {0}", e.getMessage()));
        } catch (IOException e) {
            throw new JasperFillException(NLS.bind(
                "Could not reset jasper file stream: {0}", e.getMessage()));
        }

    }

    private Map<String, Object> generateParameters(
        ByteArrayInputStream patientInfoImg,
        List<ByteArrayInputStream> barcodeIDImageList) {

        Map<String, Object> parameters = new HashMap<String, Object>();

        parameters.put(JasperConstants.TITLE_FIELD,
            templateData.getBranding().projectTitle);
        parameters.put(JasperConstants.LOGO_FIELD,
            templateData.getBranding().logo);
        parameters.put(JasperConstants.PATIENT_IMAGE_FIELD, patientInfoImg);

        for (int i = 0; i < barcodeIDImageList.size(); i++)
            parameters.put(JasperConstants.PATIENT_BARCODE_BASE + i,
                barcodeIDImageList.get(i));

        return parameters;
    }

    private ByteArrayInputStream drawElementsToPngStream(
        List<Element> elementList, int width, int height) throws IOException,
        BarcodeCreationException {

        int imageScale = 4;

        BufferedImage bi = new BufferedImage(width * imageScale, height
            * imageScale, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = (Graphics2D) bi.getGraphics();
        g.setPaintMode();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width * imageScale, height * imageScale);

        for (Element e : elementList) {
            e.render(g, imageScale);
        }

        ByteArrayOutputStream binaryOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bi, "PNG", binaryOutputStream);
        return new ByteArrayInputStream(binaryOutputStream.toByteArray());
    }
}
