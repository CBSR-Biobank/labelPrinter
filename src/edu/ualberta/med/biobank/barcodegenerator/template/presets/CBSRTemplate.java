package edu.ualberta.med.biobank.barcodegenerator.template.presets;

import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.swt.graphics.Rectangle;

import edu.ualberta.med.biobank.barcodegenerator.template.Template;
import edu.ualberta.med.biobank.barcodegenerator.template.configuration.Configuration;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.JasperFiller;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.JasperOutline;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.containers.BarcodeImage;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.containers.PatientInfo;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.FieldGenerator;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.barcodes.Barcode1D;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.barcodes.Barcode2D;
import edu.ualberta.med.biobank.barcodegenerator.views.LabelPrinterView.BarcodeViewGuiData;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.exceptions.*;
import edu.ualberta.med.biobank.barcodegenerator.template.exceptions.*;

public class CBSRTemplate extends Template {

	public byte[] generatePdfCBSR(CBSRData cbsrData,
			ArrayList<String> barcodeStrings) throws CBSRPdfGenException {

		if (cbsrData.projectTileStr == null) {
			throw new CBSRPdfGenException("Cannot have a null project title");
		}

		// TODO set barcode string count restriction correctly.
		if (barcodeStrings == null || barcodeStrings.size() == 0) {
			throw new CBSRPdfGenException(
					"Require a valid amount of barcode strings");
		}

		// -------branding------------a
		JasperOutline.Branding branding = new JasperOutline.Branding(
				cbsrData.projectTileStr, cbsrData.logoStream);

		// Point position, Dimension size, String message,String label, String
		// value, Font font, boolean printBarcode
		// -------patient info------------
		PatientInfo patientInfo = new PatientInfo();

		try {
			patientInfo.getElements().addAll(
					FieldGenerator.generateElements(
							this.getKey("PATIENT_INFO_1_TEXT"),
							cbsrData.label1Str, cbsrData.value1Str, new Font("Times New Roman",Font.PLAIN,23),
							this.getKey("PATIENT_INFO_1_BARCODE"),
							cbsrData.barcode1Print));

			patientInfo.getElements().addAll(
					FieldGenerator.generateElements(
							this.getKey("PATIENT_INFO_2_TEXT"),
							cbsrData.label2Str, cbsrData.value2Str,  new Font("Times New Roman",Font.PLAIN,23),
							this.getKey("PATIENT_INFO_2_BARCODE"),
							cbsrData.barcode2Print));

			patientInfo.getElements().addAll(
					FieldGenerator.generateElements(
							this.getKey("PATIENT_INFO_3_TEXT"),
							cbsrData.label3Str, cbsrData.value3Str,  new Font("Times New Roman",Font.PLAIN,23),
							this.getKey("PATIENT_INFO_3_BARCODE"),
							cbsrData.barcode3Print));

			patientInfo.getElements().add(
					new Barcode1D(this.getKey("PATIENT_INFO_ID_BARCODE"),
							cbsrData.patientIdStr,  new Font("Times New Roman",Font.PLAIN,22)));

		} catch (ElementCreationException e) {
			throw new CBSRPdfGenException(
					"Failed to create element in patient info box : " + e.getError());
		}
		// -------barcode info------------
		JasperOutline.PatientBarcodeInformation pbi = new JasperOutline.PatientBarcodeInformation();

		try {
			int i = 0;
			for (String rStrArray : barcodeStrings) {

				BarcodeImage bi = new BarcodeImage();

				i++;
				// 1d barcode
				if (cbsrData.patientIdStr != null) {
					Rectangle r = new Rectangle(
							this.getKey("BARCODES_1D_ROOT").x
									+ this.getKey("BARCODES_1D_NUM_" + i).x,
							this.getKey("BARCODES_1D_ROOT").y
									+ this.getKey("BARCODES_1D_NUM_" + i).y,
							this.getKey("BARCODES_1D_ROOT").width
									+ this.getKey("BARCODES_1D_NUM_" + i).width,
							this.getKey("BARCODES_1D_ROOT").height
									+ this.getKey("BARCODES_1D_NUM_" + i).height);

					Barcode1D item1D = new Barcode1D(r, cbsrData.patientIdStr,
							new Font("Times New Roman",Font.PLAIN,22));
					bi.getElements().add(item1D);
				}

				// 2d barcode;
				if (rStrArray != null) {
					Rectangle r = new Rectangle(
							this.getKey("BARCODES_2D_ROOT").x
									+ this.getKey("BARCODES_2D_NUM_" + i).x,
							this.getKey("BARCODES_2D_ROOT").y
									+ this.getKey("BARCODES_2D_NUM_" + i).y,
							this.getKey("BARCODES_2D_ROOT").width
									+ this.getKey("BARCODES_2D_NUM_" + i).width,
							this.getKey("BARCODES_2D_ROOT").height
									+ this.getKey("BARCODES_2D_NUM_" + i).height);

					Barcode2D item2D = new Barcode2D(r, rStrArray);
					bi.getElements().add(item2D);
				}

				if (cbsrData.sampleTypeStr != null) {
					Rectangle r = new Rectangle(
							this.getKey("SAMPLE_TYPE_TEXT_ROOT").x
									+ this.getKey("BARCODES_SAMPLE_TEXT_NUM_"
											+ i).x,
							this.getKey("SAMPLE_TYPE_TEXT_ROOT").y
									+ this.getKey("BARCODES_SAMPLE_TEXT_NUM_"
											+ i).y,
							this.getKey("SAMPLE_TYPE_TEXT_ROOT").width
									+ this.getKey("BARCODES_SAMPLE_TEXT_NUM_"
											+ i).width,
							this.getKey("SAMPLE_TYPE_TEXT_ROOT").height
									+ this.getKey("BARCODES_SAMPLE_TEXT_NUM_"
											+ i).height);

					edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.text.Text itemText = new edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.text.Text(
							r, cbsrData.sampleTypeStr, new Font(
									"Times New Roman", Font.PLAIN, 22));
					bi.getElements().add(itemText);
				}
				pbi.getLayout().add(bi);
			}
		} catch (ElementCreationException e) {
			throw new CBSRPdfGenException(
					"Failed to create element in PatientBarcodeInformation box : " + e.getError());
		}

		if (jasperTemplateFileData == null) {
			throw new CBSRPdfGenException("A valid jasper file is required.");
		}

		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				jasperTemplateFileData);
		this.setOutline(branding, patientInfo, pbi, inputStream);

		byte[] pdfData = null;
		try {
			JasperFiller tm = new JasperFiller(this);
			pdfData = tm.generatePdfData();
		} catch (JasperFillException e) {
			throw new CBSRPdfGenException(
					"Failed to fill configuration data into jasper template.\n"
							+ e.getError());
		}
		return pdfData;
	}

	private Rectangle getKey(String key) {
		return config.getSettingsKey(key);
	}

	public void setDefaultConfiguration() {
		HashMap<String, Rectangle> data = new HashMap<String, Rectangle>();

		data.put("PATIENT_INFO_1_TEXT", new Rectangle(0, 10, 20, 20));
		data.put("PATIENT_INFO_1_BARCODE", new Rectangle(450, 10, 20, 20));
		data.put("PATIENT_INFO_2_TEXT", new Rectangle(0, 150, 20, 20));
		data.put("PATIENT_INFO_2_BARCODE", new Rectangle(450, 150, 20, 20));
		data.put("PATIENT_INFO_3_TEXT", new Rectangle(0, 300, 20, 20));
		data.put("PATIENT_INFO_3_BARCODE", new Rectangle(450, 300, 20, 20));
		data.put("PATIENT_INFO_ID_BARCODE", new Rectangle(1, 400, 20, 20));
		data.put("BARCODES_1D_ROOT", new Rectangle(23 * 4, 25 * 4, 6, 15));
		data.put("BARCODES_2D_ROOT", new Rectangle(400, 25 * 4, 100, 1));
		data.put("SAMPLE_TYPE_TEXT_ROOT", new Rectangle(23 * 4, 28, 100, 1));

		for (int i = 1; i <= 32; i++) {
			data.put("BARCODES_1D_NUM_" + i, new Rectangle(0, 0, 0, 0));
			data.put("BARCODES_2D_NUM_" + i, new Rectangle(0, 0, 0, 0));
			data.put("BARCODES_SAMPLE_TEXT_NUM_" + i, new Rectangle(0, 0, 0, 0));
		}
		this.config = new Configuration();
		this.config.setSettings(data);
	}

	@Override
	public ArrayList<String> getConfigurationKeyList() {
		String[] configKeyList = new String[] { "PATIENT_INFO_1_TEXT",
				"PATIENT_INFO_1_BARCODE", "PATIENT_INFO_2_TEXT",
				"ATIENT_INFO_2_BARCODE", "PATIENT_INFO_3_TEXT",
				"ATIENT_INFO_3_BARCODE", "PATIENT_INFO_ID_BARCODE",
				"BARCODES_1D_ROOT", "BARCODES_2D_ROOT", "SAMPLE_TYPE_TEXT_ROOT" };
		String[] iteratedConfigKeyList = new String[] { "BARCODES_1D_NUM_",
				"BARCODES_2D_NUM_", "BARCODES_SAMPLE_TEXT_NUM_" };

		ArrayList<String> output = new ArrayList<String>();
		for (String ckl : configKeyList) {
			output.add(ckl);
		}
		for (String ickl : iteratedConfigKeyList) {
			for (int i = 1; i <= 32; i++) {
				output.add(ickl + i);
			}
		}
		return output;

	}

	public void setConfiguration(Configuration c) throws Exception {
		for (String k : c.getSettings().keySet()) {
			boolean found = false;

			for (String ckl : getConfigurationKeyList()) {
				if (k.equals(ckl)) {
					found = true;
					break;
				}
			}
			if (!found) {
				throw new Exception(
						"Key configuration contained an invalid key");

			}
		}
		this.config = c;
	}

	@Override
	public void setJasperFileData(byte[] jasperData) {
		this.jasperTemplateFileData = jasperData;
	}

	@Override
	public Configuration getConfiguration() {
		return this.config;

	}

	@Override
	public boolean jasperFileDataExists() {
		return (this.jasperTemplateFileData != null);
	}

}
