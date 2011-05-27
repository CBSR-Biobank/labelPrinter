package edu.ualberta.med.biobank.barcodegenerator.template.jasper;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.imageio.ImageIO;

import edu.ualberta.med.biobank.barcodegenerator.template.jasper.containers.BarcodeImage;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.Element;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.exceptions.BarcodeCreationException;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.exceptions.JasperFillException;

import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
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
			throw new JasperFillException("Error: requires "
					+ jasperConstants.barcodeCount + " barcode IDs");
		}

	}

	public byte[] generatePdfData() throws JasperFillException {

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
					"Failed to draw barcodeinfo to image buffer"
							+ e.getMessage());
		} catch (BarcodeCreationException e) {
			throw new JasperFillException(
					"Failed to create barcode barcodeinfo : " + e.getError());
		}

		// generate parameters for jasper
		LinkedHashMap<String, Object> parameters = generateParameters(
				patientInfoImg, barcodeIDBufferList);

		byte[] reportPdfBtyes = null;

		try {
			// generate jasper report from template
			templateData.getJasperTemplateStream().reset();
			JasperReport jasperReport = JasperCompileManager
					.compileReport(templateData.getJasperTemplateStream());
			JasperPrint jasperPrint = JasperFillManager.fillReport(
					jasperReport, parameters, new JREmptyDataSource());
			reportPdfBtyes = JasperExportManager.exportReportToPdf(jasperPrint);
		} catch (JRException e) {
			throw new JasperFillException(
					"Jasper failed to create pdf. Reason : " + e.getMessage());
		} catch (IOException e) {
			throw new JasperFillException(
					"Could not reset jasper file stream : " + e.getMessage());
		}

		return reportPdfBtyes;
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
			e.render(g,imageScale);
		}

		ByteArrayOutputStream binaryOutputStream = new ByteArrayOutputStream();
		ImageIO.write(bi, "PNG", binaryOutputStream);
		return new ByteArrayInputStream(binaryOutputStream.toByteArray());
	}
}
