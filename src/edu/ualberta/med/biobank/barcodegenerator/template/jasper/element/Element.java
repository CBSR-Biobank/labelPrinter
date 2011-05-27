package edu.ualberta.med.biobank.barcodegenerator.template.jasper.element;

import java.awt.Graphics2D;

import org.eclipse.swt.graphics.Rectangle;

import edu.ualberta.med.biobank.barcodegenerator.template.jasper.exceptions.BarcodeCreationException;

public abstract  class Element{
	public static enum TYPE {
		GenCode128, DataMatrix, Text,None,
	}
	
	protected static Rectangle scaleToPixels(Rectangle r, int scale, int dpi) {
		return new Rectangle((int) ((r.x * dpi * scale) / 25.4), (int) ((r.y
				* dpi * scale) / 25.4), (int) ((r.width * dpi * scale) / 25.4),
				(int) ((r.height * dpi * scale) / 25.4));
	}
	
	
	protected static int mmToPixel(int mm, int scale) {
		
		return (int) ((mm * 75 * scale) / 25.4); //dpi is 75 (jasper)
	}

	
	protected Rectangle rect;
	protected TYPE type = TYPE.None;
	protected String message = null;
	
	abstract public void render(Graphics2D g, int ImageScale)  throws BarcodeCreationException;
};