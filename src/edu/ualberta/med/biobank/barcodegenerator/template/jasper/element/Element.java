package edu.ualberta.med.biobank.barcodegenerator.template.jasper.element;

import java.awt.Graphics2D;

import org.eclipse.swt.graphics.Rectangle;

import edu.ualberta.med.biobank.barcodegenerator.template.jasper.exceptions.BarcodeCreationException;

public abstract  class Element{
	public static enum TYPE {
		GenCode128, DataMatrix, Text,None,
	}

	protected Rectangle rect;
	protected TYPE type = TYPE.None;
	protected String message = null;
	
	abstract public void render(Graphics2D g)  throws BarcodeCreationException;
};