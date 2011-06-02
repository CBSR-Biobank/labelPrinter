package edu.ualberta.med.biobank.barcodegenerator.template.jasper;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PrintQuality;

import edu.ualberta.med.biobank.barcodegenerator.template.jasper.containers.BarcodeImage;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.Element;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.exceptions.BarcodeCreationException;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.exceptions.JasperFillException;

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

public class JasperFiller {

    private JasperOutline templateData;

    JasperConstants jasperConstants = new JasperConstants();

    private class JasperConstants {
        public static final String titleField = "PROJECT_TITLE";
        public static final String logoField = "LOGO";
        public static final String patientImageField = "PATIENT_INFO_IMG";
        public static final String patientBarcodeBase = "PATIENT_BARCODE_";

        public int barcodeCount = 0;
        public int barcodeImageWidth = 0;
        public int barcodeImageHeight = 0;

        public int patientImageWidth = 0;
        public int patientImageHeight = 0;
    }

    public JasperFiller(JasperOutline req) throws JasperFillException {

        if (req == null)
            throw new JasperFillException("Null request for jasper filler.");

        this.templateData = req;

        loadTemplateConstants();

    }

    private void loadTemplateConstants() throws JasperFillException {

        try {
            templateData.getJasperTemplateStream().reset();
        } catch (IOException e) {
            throw new JasperFillException(
                "Failed to reset template data stream : " + e.getMessage());
        }
        JasperDesign jasperSubDesign;
        try {
            jasperSubDesign = JRXmlLoader.load(templateData
                .getJasperTemplateStream());
        } catch (JRException e) {
            throw new JasperFillException("Failed to load jasper design: "
                + e.getMessage());
        }

        JRElement patientImg = jasperSubDesign.getTitle().getElementByKey(
            JasperConstants.patientImageField);
        if (jasperSubDesign.getTitle() != null && patientImg != null) {
            jasperConstants.patientImageWidth = patientImg.getWidth();
            jasperConstants.patientImageHeight = patientImg.getHeight();
        } else {
            throw new JasperFillException(
                "Failed to patient image dimensions from the jasper report.");
        }

        if (jasperSubDesign.getPageFooter() != null
            && jasperSubDesign.getPageFooter().getElements() != null
            && jasperSubDesign.getPageFooter().getElements().length > 0) {
            jasperConstants.barcodeImageWidth = jasperSubDesign.getPageFooter()
                .getElements()[0].getWidth();
            jasperConstants.barcodeImageHeight = jasperSubDesign
                .getPageFooter().getElements()[0].getHeight();
            jasperConstants.barcodeCount = jasperSubDesign.getPageFooter()
                .getElements().length;

            for (JRElement jr : jasperSubDesign.getPageFooter().getElements()) {
                if (jr.getWidth() != jasperConstants.barcodeImageWidth
                    || jr.getHeight() != jasperConstants.barcodeImageHeight) {
                    throw new JasperFillException(
                        "All barcode image fields must be of equal size.");
                }
            }
        } else {
            throw new JasperFillException(
                "Failed to barcode image dimensions from the jasper report.");
        }

        if (templateData.getPatientBarcpdeInf().getLayout().size() != jasperConstants.barcodeCount) {
            throw new JasperFillException("Error: jasper file contains "
                + jasperConstants.barcodeCount
                + " barcode IDs. Configuration data is designed for :"
                + templateData.getPatientBarcpdeInf().getLayout().size()
                + " barcode IDs.");
        }

    }

    public void printJasperToPrinter(String printerName)
        throws JasperFillException {

        if (printerName == null) {
            throw new JasperFillException("Error: No printer was selected!");
        }

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
            throw new JasperFillException("Failed to set print service: "
                + e1.getMessage());
        }

        JasperPrint print = generateJasperPint();

        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
        printRequestAttributeSet.add(new Copies(1));
        printRequestAttributeSet.add(PrintQuality.HIGH);
        printRequestAttributeSet.add(MediaSizeName.NA_LETTER);
        printRequestAttributeSet.add(new MediaPrintableArea(0, 0, (float) print
            .getPageWidth() / 72f, (float) print.getPageHeight() / 72f,
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
            throw new JasperFillException("Failed to print: " + e.getMessage());
        }
    }

    public byte[] generatePdfData() throws JasperFillException {

        byte[] reportPdfBtyes = null;

        JasperPrint jp = generateJasperPint();
        try {
            reportPdfBtyes = JasperExportManager.exportReportToPdf(jp);
        } catch (JRException e) {

            throw new JasperFillException(
                "Jasper failed to create pdf. Reason : " + e.getMessage());
        }
        return reportPdfBtyes;
    }

    private JasperPrint generateJasperPint() throws JasperFillException {
        ByteArrayInputStream patientInfoImg;
        ArrayList<ByteArrayInputStream> barcodeIDBufferList = new ArrayList<ByteArrayInputStream>();

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
            throw new JasperFillException(
                "Failed to create barcode patientInfoImg : " + e.getError());
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
            throw new JasperFillException(
                "Failed to draw barcodeinfo to image buffer" + e.getMessage());
        } catch (BarcodeCreationException e) {
            throw new JasperFillException(
                "Failed to create barcode barcodeinfo : " + e.getError());
        }

        // generate parameters for jasper
        LinkedHashMap<String, Object> parameters = generateParameters(
            patientInfoImg, barcodeIDBufferList);

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
            throw new JasperFillException(
                "Jasper failed to create pdf. Reason : " + e.getMessage());
        } catch (IOException e) {
            throw new JasperFillException(
                "Could not reset jasper file stream : " + e.getMessage());
        }

    }

    private LinkedHashMap<String, Object> generateParameters(
        ByteArrayInputStream patientInfoImg,
        ArrayList<ByteArrayInputStream> barcodeIDImageList) {

        LinkedHashMap<String, Object> parameters = new LinkedHashMap<String, Object>();

        parameters.put(JasperConstants.titleField,
            templateData.getBranding().projectTitle);
        parameters.put(JasperConstants.logoField,
            templateData.getBranding().logo);
        parameters.put(JasperConstants.patientImageField, patientInfoImg);

        for (int i = 0; i < barcodeIDImageList.size(); i++)
            parameters.put(JasperConstants.patientBarcodeBase + i,
                barcodeIDImageList.get(i));

        return parameters;
    }

    private ByteArrayInputStream drawElementsToPngStream(
        ArrayList<Element> elementList, int width, int height)
        throws IOException, BarcodeCreationException {

        int imageScale = 4;

        BufferedImage bi = new BufferedImage(width * imageScale, height
            * imageScale, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = (Graphics2D) bi.getGraphics();
        g.setPaintMode();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width * imageScale, height * imageScale);

        // border
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width * imageScale - 1, height * imageScale - 1);

        for (Element e : elementList) {
            e.render(g, imageScale);
        }

        ByteArrayOutputStream binaryOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bi, "PNG", binaryOutputStream);
        return new ByteArrayInputStream(binaryOutputStream.toByteArray());
    }
}
