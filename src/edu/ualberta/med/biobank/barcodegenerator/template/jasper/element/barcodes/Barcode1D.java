package edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.barcodes;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.eclipse.swt.graphics.Rectangle;

import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.Element;

public class Barcode1D extends Element {

	Font font;

	public Barcode1D(Rectangle rect, String message, Font font) {
		this.rect = rect;
		this.type = Element.TYPE.GenCode128;
		this.message = message;

		if (font != null) {
			this.font = font;
		} else {
			this.font = new Font("Times New Roman", Font.PLAIN, 22);
		}
	}

	public boolean verify() {
		return (message != null && message.replaceAll("[^a-zA-Z0-9 ]", "")
				.length() == message.length());
	}

	public void render(Graphics2D g) throws IOException {
			BufferedImage barcode1DImg = BarcodeGenerator.generate1DBarcode(
					message, font, rect, 200);
			g.drawImage(barcode1DImg,  rect.x, rect.y, null);
	}
}