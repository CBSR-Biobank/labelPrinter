package edu.ualberta.med.biobank.barcodegenerator.template.jasper;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import edu.ualberta.med.biobank.barcodegenerator.template.jasper.containers.BarcodeImage;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.Element;

import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.FileResolver;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

public class JasperFiller {

	private JasperOutline templateData;

	JasperConstants jasperConstants = new JasperConstants();

	private class JasperConstants {
		public static final String titleField = "PROJECT_TITLE";
		public static final String logoField = "LOGO";
		public static final String patientImageField = "PATIENT_INFO_IMG";
		public static final String patientBarcodeBase = "PATIENT_BARCODE_";

		public int barcodeCount = 32;
		public int barcodeImageWidth = 153;
		public int barcodeImageHeight = 63;

		public int patientImageWidth = 238;
		public int patientImageHeight = 182;
	}

	public JasperFiller(JasperOutline req)
			throws Exception {
		this.templateData = req;

		loadTemplateConstants();

		if (templateData.patientBarcpdeInf.getLayout().size() != jasperConstants.barcodeCount) {
			throw new Exception("Error: requires "
					+ jasperConstants.barcodeCount + " barcode IDs");
		}
	}

	private void loadTemplateConstants() throws Exception {
		
		//System.out.println("Available jasper file bytes: " +this.templateData.jasperTemplateStream.available());

		//TODO make input stream work with jasper
		//has to do with the classloader and eclipse.
		JasperDesign jasperSubDesign = JRXmlLoader.load(new File("LabelFormTemplate.jrxml")); //this.templateData.jasperTemplateStream

		if (jasperSubDesign == null) {
			// TODO implment proper error handling
			throw new Exception("Failed to load jasper design");
		}

		JRElement patientImg = jasperSubDesign.getTitle().getElementByKey(
				JasperConstants.patientImageField);
		if (jasperSubDesign.getTitle() != null && patientImg != null) {
			jasperConstants.barcodeImageWidth = patientImg.getWidth();
			jasperConstants.patientImageHeight = patientImg.getHeight();
		} else {
			throw new Exception(
					"Failed to patient image dimensions from the jasper report.");
		}

		if (jasperSubDesign.getPageFooter() != null
				&& jasperSubDesign.getPageFooter().getElements() != null
				&& jasperSubDesign.getPageFooter().getElements().length > 0) {
			jasperConstants.barcodeImageWidth = jasperSubDesign
					.getPageFooter().getElements()[0].getWidth();
			jasperConstants.barcodeImageHeight = jasperSubDesign
					.getPageFooter().getElements()[0].getHeight();
			jasperConstants.barcodeCount = jasperSubDesign.getPageFooter()
					.getElements().length;

			for (JRElement jr : jasperSubDesign.getPageFooter().getElements()) {
				if (jr.getWidth() != jasperConstants.barcodeImageWidth
						|| jr.getHeight() != jasperConstants.barcodeImageHeight) {
					throw new Exception(
							"All barcode image fields must be of equal size.");
				}
			}
		} else {
			throw new Exception(
					"Failed to barcode image dimensions from the jasper report.");
		}
	}

	public byte[] generatePdfData() throws Exception {

		// place patient image.
		ByteArrayInputStream patientInfoImg = drawElementsToPngStream(
				templateData.patientInfo.getElements(),
				jasperConstants.patientImageWidth,
				jasperConstants.patientImageHeight);

		// place patient barcode images
		ArrayList<ByteArrayInputStream> barcodeIDBufferList = new ArrayList<ByteArrayInputStream>();
		for (BarcodeImage bi : templateData.patientBarcpdeInf.getLayout()) {
			barcodeIDBufferList.add(drawElementsToPngStream(bi.getElements(),
					jasperConstants.barcodeImageWidth,
					jasperConstants.barcodeImageHeight));
		}

		// generate parameters for jasper
		HashMap<String, Object> parameters = generateParameters(patientInfoImg,
				barcodeIDBufferList);

		// generate jasper report from template
		JasperReport jasperReport = JasperCompileManager//TODO replace with stream
				.compileReport("LabelFormTemplate.jrxml");
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
				parameters, new JREmptyDataSource());
		byte[] reportPdfBtyes = JasperExportManager
				.exportReportToPdf(jasperPrint);

		return reportPdfBtyes;
	}

	private HashMap<String, Object> generateParameters(
			ByteArrayInputStream patientInfoImg,
			ArrayList<ByteArrayInputStream> barcodeIDImageList) {

		HashMap<String, Object> parameters = new HashMap<String, Object>();

		parameters.put(JasperConstants.titleField,
				templateData.branding.projectTitle);
		parameters.put(JasperConstants.logoField, templateData.branding.logo);
		parameters.put(JasperConstants.patientImageField, patientInfoImg);

		for (int i = 0; i < barcodeIDImageList.size(); i++)
			parameters.put(JasperConstants.patientBarcodeBase + i,
					barcodeIDImageList.get(i));

		parameters.put("REPORT_FILE_RESOLVER", new FileResolver() {
			@Override
			public File resolveFile(String fileName) {
				URI uri;
				try {
					uri = new URI(this.getClass().getResource(fileName)
							.getPath());
					return new File(uri.getPath());
				} catch (URISyntaxException e) {

					e.printStackTrace();
					return null;
				}
			}
		});

		return parameters;
	}

	private ByteArrayInputStream drawElementsToPngStream(
			ArrayList<Element> elementList, int width, int height)
			throws IOException {

		BufferedImage bi = new BufferedImage(width * 4, height * 4,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		g.setPaintMode();

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width * 4, height * 4);

		// border
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, width * 4 - 1, height * 4 - 1);

		for (Element e : elementList) {
			if (e.verify()) {
				e.render(g);
			} else {
				// TODO handle element verificiation failure
				throw new RuntimeException("Failed to verify layout element.");
			}
		}
		// convert to our bufferedImage to a png based ByteArrayInputStream
		ByteArrayOutputStream binaryOutputStream = new ByteArrayOutputStream();
		try {

			ImageIO.write(bi, "PNG", binaryOutputStream);

		} catch (IOException e) {
			e.printStackTrace();
			binaryOutputStream = null;
		}
		return new ByteArrayInputStream(binaryOutputStream.toByteArray());
	}
}
