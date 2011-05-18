package edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.barcodes;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.eclipse.swt.graphics.Rectangle;

import edu.ualberta.med.biobank.barcodegenerator.template.jasper.element.Element;

public class Barcode2D extends Element {

	public Barcode2D(Rectangle rect, String message) {
		this.rect = rect;
		this.type = Element.TYPE.DataMatrix;
		this.message = message;
	}

	public boolean verify() {
		return (message != null && message.replaceAll("[^a-zA-Z0-9]", "")
				.length() == 12);
	}

	public void render(Graphics2D g) throws IOException {
		BufferedImage barcode2DImg = BarcodeGenerator
				.generate2DBarcode(message);
		g.drawImage(barcode2DImg,  rect.x,  rect.y, null);
	}
}
