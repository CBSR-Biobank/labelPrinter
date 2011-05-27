package edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.barcodes;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.eclipse.swt.graphics.Rectangle;

import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.Element;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.exceptions.BarcodeCreationException;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.exceptions.ElementCreationException;

public class Barcode1D extends Element {

	Font font;

	public Barcode1D(Rectangle rect, String message, Font font)
			throws ElementCreationException {

		if (message == null || message.length() == 0)
			throw new ElementCreationException(
					"empty or null message specified to 1D barcode element.");

		if (rect == null)
			throw new ElementCreationException(
					"null dimensions specified to 1D barcode element.");

		if (font == null)
			throw new ElementCreationException(
					"null font specified to 1D barcode element.");

		this.font = font;
		this.rect = rect;
		this.type = Element.TYPE.GenCode128;
		this.message = message;

	}
	
	public void render(Graphics2D g, int scale) throws BarcodeCreationException {
		BufferedImage barcode1DImg;
		try {
			barcode1DImg = BarcodeGenerator.generate1DBarcode(message, font,
					300);
		} catch (IOException e) {
			throw new BarcodeCreationException(
					"Failed to create image buffer for barcode");
		}
		g.drawImage(barcode1DImg, mmToPixel(rect.x,scale), mmToPixel(rect.y,scale), mmToPixel(rect.width,scale),
				mmToPixel(rect.height,scale), null);
		
	}
}