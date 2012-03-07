package edu.ualberta.med.biobank.labelprinter.template.jasper.element.barcodes;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.ualberta.med.biobank.labelprinter.template.configuration.Rectangle;
import edu.ualberta.med.biobank.labelprinter.template.jasper.element.Element;
import edu.ualberta.med.biobank.labelprinter.template.jasper.exceptions.BarcodeCreationException;
import edu.ualberta.med.biobank.labelprinter.template.jasper.exceptions.ElementCreationException;

/**
 * Used for generating and rendering a 128 Code based 1D barcode.
 * 
 * @author Thomas Polasek 2011
 * 
 */
public class Barcode1D extends Element {

    Font font;

    public Barcode1D(Rectangle rect, String message, Font font)
        throws ElementCreationException {

        if ((message == null) || (message.length() == 0))
            throw new ElementCreationException(
                Messages.Barcode1D_empty_msg);

        if (rect == null)
            throw new ElementCreationException(
                Messages.Barcode1D_null_dimensions_msg);

        if (font == null)
            throw new ElementCreationException(
                Messages.Barcode1D_null_font_msg);

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
                Messages.Barcode1D_img_buffer_error);
        }
        g.drawImage(barcode1DImg, mmToPixel(rect.getX(), scale),
            mmToPixel(rect.getY(), scale), mmToPixel(rect.getWidth(), scale),
            mmToPixel(rect.getHeight(), scale), null);

    }
}