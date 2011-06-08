package edu.ualberta.med.biobank.barcodegenerator.template.jasper.element;

import java.awt.Graphics2D;

import edu.ualberta.med.biobank.barcodegenerator.template.configuration.Rectangle;
import edu.ualberta.med.biobank.barcodegenerator.template.jasper.exceptions.BarcodeCreationException;

/**
 * Elements are objects that rendered onto a given jasper template. The jasper
 * filler is responcible for loading and plotting elements.
 * 
 * @author Thomas Polasek 2011
 * 
 */
public abstract class Element {
    public static enum TYPE {
        GenCode128, DataMatrix, Text, None,
    }

    protected static Rectangle scaleToPixels(Rectangle r, int scale, int dpi) {
        return new Rectangle((int) ((r.getX() * dpi * scale) / 25.4),
            (int) ((r.getY() * dpi * scale) / 25.4),
            (int) ((r.getWidth() * dpi * scale) / 25.4), (int) ((r.getHeight()
                * dpi * scale) / 25.4));
    }

    protected static int mmToPixel(int mm, int scale) {

        return (int) ((mm * 72 * scale) / 25.4); // dpi is 72 (jasper)
    }

    protected Rectangle rect;
    protected TYPE type = TYPE.None;
    protected String message = null;

    abstract public void render(Graphics2D g, int ImageScale)
        throws BarcodeCreationException;
};