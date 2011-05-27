package edu.ualberta.med.biobank.barcodegenerator.template.presets.cbsr;

import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

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
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.text.Text;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.exceptions.*;
import edu.ualberta.med.biobank.barcodegenerator.template.presets.cbsr.exceptions.CBSRPdfGenException;

//FIXME CBSRTemplate expects a constant (32) amount of barcodes
public class CBSRTemplate extends Template {

	private static final long serialVersionUID = -6346822010546940605L;

	public byte[] generatePdfCBSR(CBSRData cbsrData,
			ArrayList<String> barcodeStrings) throws CBSRPdfGenException {

		if (cbsrData.projectTileStr == null) {
			throw new CBSRPdfGenException("Cannot have a null project title");
		}

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
					FieldGenerator.generateElements(this
							.getKey("Patient Info.Top Field.Field Text"),
							cbsrData.label1Str, cbsrData.value1Str, new Font(
									"Times New Roman", Font.PLAIN, 23),
							this.getKey("Patient Info.Top Field.1D Barcode"),
							cbsrData.barcode1Print));

			patientInfo
					.getElements()
					.addAll(FieldGenerator.generateElements(
							this.getKey("Patient Info.Middle Field.Field Text"),
							cbsrData.label2Str,
							cbsrData.value2Str,
							new Font("Times New Roman", Font.PLAIN, 23),
							this.getKey("Patient Info.Middle Field.1D Barcode"),
							cbsrData.barcode2Print));

			patientInfo
					.getElements()
					.addAll(FieldGenerator.generateElements(
							this.getKey("Patient Info.Bottom Field.Field Text"),
							cbsrData.label3Str,
							cbsrData.value3Str,
							new Font("Times New Roman", Font.PLAIN, 23),
							this.getKey("Patient Info.Bottom Field.1D Barcode"),
							cbsrData.barcode3Print));

			patientInfo.getElements().add(
					new Barcode1D(this
							.getKey("Patient Info.Patient ID.1D Barcode"),
							cbsrData.patientIdStr, new Font("Times New Roman",
									Font.PLAIN, 22)));

		} catch (ElementCreationException e) {
			throw new CBSRPdfGenException(
					"Failed to create element in patient info box : "
							+ e.getError());
		}
		// -------barcode info------------
		JasperOutline.PatientBarcodeInformation pbi = new JasperOutline.PatientBarcodeInformation();

		try {
			int i = 0;
			for (String rStrArray : barcodeStrings) {

				BarcodeImage bi = new BarcodeImage();

				i++;
				// 1d barcode
				if (cbsrData.patientIdStr != null
						&& cbsrData.patientIdStr.length() > 0) {

					Rectangle master = this
							.getKey("Barcodes.All.Barcode 1D");
					Rectangle barcode = this
							.getKey("Barcodes.Individual.Barcode "
									+ addPaddingZeros(i) + ".Barcode 1D");

					Rectangle r = new Rectangle(master.x + barcode.x, master.y
							+ barcode.y, master.width + barcode.width,
							master.height + barcode.height);

					Barcode1D item1D = new Barcode1D(r, cbsrData.patientIdStr,
							new Font("Times New Roman", Font.PLAIN, 22));
					bi.getElements().add(item1D);
				} else {
					throw new CBSRPdfGenException(
							"Empty or null barcode string was specified.");
				}

				// 2d barcode;
				if (rStrArray != null
						&& rStrArray.length() > 0
						&& rStrArray.replaceAll("[^a-zA-Z0-9 ]", "").length() == 12) {

					Rectangle master = this
							.getKey("Barcodes.All.Barcode 2D");
					Rectangle barcode = this
							.getKey("Barcodes.Individual.Barcode "
									+ addPaddingZeros(i) + ".Barcode 2D");

					Rectangle r = new Rectangle(master.x + barcode.x, master.y
							+ barcode.y, master.width + barcode.width,
							master.height + barcode.height);

					Barcode2D item2D = new Barcode2D(r, rStrArray);
					bi.getElements().add(item2D);
				} else {
					throw new CBSRPdfGenException(
							"Barcode ID must be a 12 character alphanumeric string.");
				}

				if (cbsrData.sampleTypeStr != null
						&& cbsrData.sampleTypeStr.length() > 0) {

					Rectangle master = this
							.getKey("Barcodes.All.Sample Text");

					Rectangle barcode = this
							.getKey("Barcodes.Individual.Barcode "
									+ addPaddingZeros(i) + ".Sample Text");

					Rectangle rectdim = new Rectangle(master.x + barcode.x,
							master.y + barcode.y, master.width + barcode.width,
							master.height + barcode.height);

					Text itemText = new Text(rectdim, cbsrData.sampleTypeStr,
							new Font("Times New Roman", Font.PLAIN, 22));
					bi.getElements().add(itemText);
				}
				pbi.getLayout().add(bi);
			}
		} catch (ElementCreationException e) {
			throw new CBSRPdfGenException(
					"Failed to create element in PatientBarcodeInformation box : "
							+ e.getError());
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

	private String addPaddingZeros(int i) {
		String out = String.valueOf(i);
		if (out.length() <= 0) {
			out = "00";
		} else if (out.length() <= 1) {
			out = "0" + out;
		}
		return out;
	}

	public void setDefaultConfiguration() {
		LinkedHashMap<String, Rectangle> data = new LinkedHashMap<String, Rectangle>();

		data.put("Patient Info.Top Field.Field Text", new Rectangle(1, 4, 0,0));
		data.put("Patient Info.Top Field.1D Barcode", new Rectangle(38, 1, 29,8));
		data.put("Patient Info.Middle Field.Field Text", new Rectangle(1, 13,0, 0));
		data.put("Patient Info.Middle Field.1D Barcode", new Rectangle(38, 13,29, 8));
		data.put("Patient Info.Bottom Field.Field Text", new Rectangle(1, 25,0, 0));
		data.put("Patient Info.Bottom Field.1D Barcode", new Rectangle(38, 25,29, 8));
		data.put("Patient Info.Patient ID.1D Barcode", new Rectangle(1, 33,29, 8));
		data.put("Barcodes.All.Barcode 1D", new Rectangle(8, 7, 29, 8));
		data.put("Barcodes.All.Barcode 2D", new Rectangle(40, 7, 6, 6));
		data.put("Barcodes.All.Sample Text", new Rectangle(8, 2, 0, 0));

		for (int i = 1; i <= 32; i++) {
			data.put("Barcodes.Individual.Barcode " + addPaddingZeros(i)
					+ ".Barcode 1D", new Rectangle(0, 0, 0, 0));
			data.put("Barcodes.Individual.Barcode " + addPaddingZeros(i)
					+ ".Barcode 2D", new Rectangle(0, 0, 0, 0));
			data.put("Barcodes.Individual.Barcode " + addPaddingZeros(i)
					+ ".Sample Text", new Rectangle(0, 0, 0, 0));
		}
		this.config = new Configuration();
		this.config.setSettings(data);
	}

	@Override
	public ArrayList<String> getConfigurationKeyList() {
		String[] configKeyList = new String[] {
				"Patient Info.Top Field.Field Text",
				"Patient Info.Top Field.1D Barcode",
				"Patient Info.Middle Field.Field Text",
				"Patient Info.Middle Field.1D Barcode",
				"Patient Info.Bottom Field.Field Text",
				"Patient Info.Bottom Field.1D Barcode",
				"Patient Info.Patient ID.1D Barcode",
				"Barcodes.All.Barcode 1D", "Barcodes.All.Barcode 2D",
				"Barcodes.All.Sample Text" };

		String[] iteratedConfigKeyList = new String[] {
				"Barcodes.Individual.Barcode ", "Barcodes.Individual.Barcode ",
				"Barcodes.Individual.Barcode " };

		ArrayList<String> output = new ArrayList<String>();
		for (String ckl : configKeyList) {
			output.add(ckl);
		}
		for (String ickl : iteratedConfigKeyList) {
			for (int i = 1; i <= 32; i++) {
				output.add(ickl + addPaddingZeros(i));
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
