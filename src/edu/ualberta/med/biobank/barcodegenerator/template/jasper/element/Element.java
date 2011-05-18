package edu.ualberta.med.biobank.barcodegenerator.template.jasper.element;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.eclipse.swt.graphics.Rectangle;

public abstract  class Element{
	public static enum TYPE {
		GenCode128, DataMatrix, Text,None,
	}

	protected Rectangle rect;
	protected TYPE type = TYPE.None;
	protected String message = null;
	
	abstract public boolean verify();
	abstract public void render(Graphics2D g)  throws IOException;
};