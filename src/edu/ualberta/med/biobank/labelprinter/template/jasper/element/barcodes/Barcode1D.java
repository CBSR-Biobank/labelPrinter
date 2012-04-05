package edu.ualberta.med.biobank.labelprinter.template.jasper.element.barcodes;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.ualberta.med.biobank.labelprinter.template.configuration.Rectangle;
import edu.ualberta.med.biobank.labelprinter.template.jasper.element.Element;
import edu.ualberta.med.biobank.labelprinter.template.jasper.exceptions.BarcodeCreationException;

/**
 * Used for generating and rendering a 128 Code based 1D barcode.
 * 
 * @author Thomas Polasek 2011
 * 
 */
@SuppressWarnings("nls")
public class Barcode1D extends Element {

    Font font;

    public Barcode1D(Rectangle rect, String message, Font font) {

        if ((message == null) || (message.length() == 0))
            throw new IllegalArgumentException(
                "empty or null message specified to 1D barcode element.");

        if (rect == null)
            throw new IllegalArgumentException(
                "null dimensions specified to 1D barcode element.");

        if (font == null)
            throw new IllegalArgumentException(
                "null font specified to 1D barcode element.");

        this.font = font;
        this.rect = rect;
        this.type = Element.TYPE.GenCode128;
        this.message = message;

    }

    @Override
    public void render(Graphics2D g, int scale) throws BarcodeCreationException {
        BufferedImage barcode1DImg;
        try {
            barcode1DImg = BarcodeGenerator.generate1DBarcode(message, font,
                300);
        } catch (IOException e) {
            throw new BarcodeCreationException(
                "Failed to create image buffer for barcode");
        }
        g.drawImage(barcode1DImg, mmToPixel(rect.getX(), scale),
            mmToPixel(rect.getY(), scale), mmToPixel(rect.getWidth(), scale),
            mmToPixel(rect.getHeight(), scale), null);

    }
}